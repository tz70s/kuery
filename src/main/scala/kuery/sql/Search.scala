/*
 * Copyright 2018 Tzu-Chiao Yeh.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kuery.sql

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import kuery.model.MedicalJob.MedicalJob
import kuery.model.{HospitalTable, MedicalJob, PersonnelTable, PharmacyTable}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.util.Try
import scala.concurrent.duration._

case class JobSearch(job: String, count: Boolean)

/** Searching related services */
trait SearchService {
  this: DatabaseService =>

  /** SELECT COUNT(job) FROM medical_job WHERE job=<job> */
  private def countJob(implicit job: MedicalJob) = {
    val countQuery = PersonnelTable.query.filter(_.job === job).length
    db.run(countQuery.result).map(_.toString)
  }

  /** SELECT * FROM medical_job WHERE job=<job> */
  private def plainJob(implicit job: MedicalJob) = {
    val jobQuery = for { person <- PersonnelTable.query if person.job === job } yield person
    db.run(jobQuery.result) map { _.map(_.toString).reduce(_ + "\n" + _) }
  }

  /**
   * Most critical performance testing, a three table JOIN operation.
   *
   * SELECT * FROM medical_personnel
   * JOIN pharmacy ON medical_personnel.`hospital_id` = pharmacy.`hospital_id`
   * JOIN hospital ON medical_personnel.`hospital_id` = hospital.`id`
   * WHERE medical_personnel.`job` = `pharmacist`
   *
   */
  private def bigJoin() = {
    val joinQuery = for {
      ((person, pharmacy), hospital) <- PersonnelTable.query join
        PharmacyTable.query on (_.hospital === _.hospital) join
        HospitalTable.query on (_._1.hospital === _.id) if person.job === MedicalJob.pharmacist
    } yield (person, pharmacy, hospital)

    db.run(joinQuery.result) map {
      _.map { collect =>
        s"${collect._1.name} is a ${collect._1.job}, working at ${collect._2.name} in ${collect._3.name} (${collect._3.level})"
      }.reduce(_ + "\n" + _)
    }
  }

  /**
   * Search the job from personnel table.
   *
   * SELECT * FROM medical_personnel WHERE job=<job>;
   *
   * GET /sql/personnel?job=<job>
   *
   * @return Route akka server route.
   */
  def jobSearch: Route =
    parameters('job.as[String], 'count.as[Boolean]).as(JobSearch) { search =>
      implicit val medicalJob: MedicalJob = MedicalJob.withName(search.job)
      Try {
        val fut = if (search.count) countJob else plainJob
        Await.result(fut, 10 seconds)
      } map (complete(_)) getOrElse reject
    }

  def joinSearch: Route =
    path("join") {
      get {
        val text = Await.result(bigJoin, 30 seconds)
        complete(text)
      }
    }
}
