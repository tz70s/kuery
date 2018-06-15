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

object HospitalLevel extends Enumeration {
  type HospitalLevel = Value
  val local = Value("local")
  val national = Value("national")
}

case class Hospital(override val id: Int, override val name: String, level: HospitalLevel) extends Searchable {
  override def toString: String = s"Hospital $id, name: $name, level: $level"
}

class HospitalTable(tag: Tag) extends Table[Hospital](tag, "hospital") {

  implicit val mapper = MappedColumnType.base[HospitalLevel, String](e => e.toString, s => HospitalLevel.withName(s))

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def level = column[HospitalLevel]("level")

  def * = (id, name, level) <> (Hospital.tupled, Hospital.unapply)

  def query = TableQuery[HospitalTable]
}

object HospitalTable {
  def query = TableQuery[HospitalTable]
}
