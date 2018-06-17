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

import HospitalLevel.HospitalLevel
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery
import spray.json._

object HospitalLevel extends Enumeration {
  type HospitalLevel = Value
  val local = Value("local")
  val national = Value("national")

  implicit val mapper = MappedColumnType.base[HospitalLevel, String](e => e.toString, s => HospitalLevel.withName(s))
}

case class Hospital(val id: Option[Int], val name: String, level: HospitalLevel) {
  override def toString: String = s"Hospital $id, name: $name, level: $level"
}

object HospitalJsonProtocol extends DefaultJsonProtocol with NullOptions {
  implicit object HospitalJsonFormat extends RootJsonFormat[Hospital] {
    override def write(h: Hospital): JsValue = {
      JsObject("id" -> h.id.toJson, "name" -> JsString(h.name), "level" -> JsString(h.level.toString))
    }

    override def read(value: JsValue): Hospital = {
      value.asJsObject.getFields("id", "name", "level") match {
        case Seq(JsNumber(id), JsString(name), JsString(level)) =>
          // Let it throw.
          Hospital(Some(id.toInt), name, HospitalLevel.withName(level))
        case Seq(JsString(name), JsString(level)) =>
          Hospital(None, name, HospitalLevel.withName(level))
        case _ => throw new DeserializationException("Hospital object expected.")
      }
    }
  }
}

class HospitalTable(tag: Tag) extends Table[Hospital](tag, "hospital") {

  import HospitalLevel.mapper

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def level = column[HospitalLevel]("level")

  def * = (id.?, name, level) <> (Hospital.tupled, Hospital.unapply)

  def query = TableQuery[HospitalTable]
}

object HospitalTable {
  implicit val query = TableQuery[HospitalTable]
}
