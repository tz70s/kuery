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

import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

case class Pharmacy(override val id: Int, override val name: String, hospital: Int) extends Searchable {
  override def toString: String = s"Pharmacy $id, name: $name, hospital: $hospital"
}

class PharmacyTable(tag: Tag) extends Table[Pharmacy](tag, "pharmacy") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def hospital = column[Int]("hospital_id")

  def * = (id, name, hospital) <> (Pharmacy.tupled, Pharmacy.unapply)
}

object PharmacyTable {
  implicit val query = TableQuery[PharmacyTable]
}
