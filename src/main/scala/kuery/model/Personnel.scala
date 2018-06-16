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

package kuery.model

import kuery.model.MedicalJob.MedicalJob
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

object MedicalJob extends Enumeration {
  type MedicalJob = Value
  val nurse = Value("nurse")
  val pharmacist = Value("pharmacist")
  val doctor = Value("doctor")

  implicit val mapper = MappedColumnType.base[MedicalJob, String](e => e.toString, s => MedicalJob.withName(s))

  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}

case class Personnel(override val id: Int, override val name: String, job: MedicalJob, hospital: Int)
    extends Searchable {
  override def toString: String = s"Personnel $id, name: $name, job: $job, hospital: $hospital"
}

class PersonnelTable(tag: Tag) extends Table[Personnel](tag, "medical_personnel") {

  import MedicalJob.mapper

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def job = column[MedicalJob]("job")
  def hospital = column[Int]("hospital_id")

  def * = (id, name, job, hospital) <> (Personnel.tupled, Personnel.unapply)

}

object PersonnelTable {
  implicit val query = TableQuery[PersonnelTable]
}
