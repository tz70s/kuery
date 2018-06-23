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
import spray.json._

object MedicalJob extends Enumeration {
  type MedicalJob = Value
  val nurse = Value("nurse")
  val pharmacist = Value("pharmacist")
  val doctor = Value("doctor")

  implicit val mapper = MappedColumnType.base[MedicalJob, String](e => e.toString, s => MedicalJob.withName(s))
}

case class Personnel(id: Option[Int], name: String, job: MedicalJob, hospital: Int) {
  override def toString: String = s"Personnel $id, name: $name, job: $job, hospital: $hospital"
}

object PersonnelJsonProtocol extends DefaultJsonProtocol with NullOptions {
  implicit object PersonnelJsonFormat extends RootJsonFormat[Personnel] {
    override def write(p: Personnel): JsValue = {
      JsObject(
        "id" -> p.id.toJson,
        "name" -> JsString(p.name),
        "job" -> JsString(p.job.toString),
        "hospital" -> JsNumber(p.hospital))
    }

    override def read(value: JsValue): Personnel = {
      value.asJsObject.getFields("id", "name", "job", "hospital") match {
        case Seq(JsNumber(id), JsString(name), JsString(job), JsNumber(hospital)) =>
          // Let it throw.
          Personnel(Some(id.toInt), name, MedicalJob.withName(job), hospital.toInt)
        case Seq(JsString(name), JsString(job), JsNumber(hospital)) =>
          Personnel(None, name, MedicalJob.withName(job), hospital.toInt)
        case _ => throw DeserializationException("Personnel object expected.")
      }
    }
  }
}

class PersonnelTable(tag: Tag) extends Table[Personnel](tag, "medical_personnel") {

  import MedicalJob.mapper

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def job = column[MedicalJob]("job")
  def hospital = column[Int]("hospital_id")

  def * = (id.?, name, job, hospital) <> (Personnel.tupled, Personnel.unapply)
}

object PersonnelTable {
  val query = TableQuery[PersonnelTable]
}
