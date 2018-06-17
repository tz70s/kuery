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

import akka.http.scaladsl.server.Route
import kuery.model._
import akka.http.scaladsl.server.Directives._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await

trait InsertService {
  this: Sequelizer =>

  /**
   * Insert of hospital, transform directly into sql statement and ignore the id value.
   * INSERT (name, level) INTO `hospital` VALUES (name, level);
   */
  implicit def insertHospital(hospital: Hospital): Route = {

    val statement = DBIO.seq(HospitalTable.query += hospital)

    val _ = Await.result(db.run(statement), timeout)

    complete("Ok")
  }

  /**
   * Unlike hospital, personnel has the foreign key to hospital identification.
   * Before insertion, user might need to first discover the hospital ident by specific condition matching.
   * TODO: We may consider to support automatically match this by opening two sql statements transactionally.
   */
  implicit def insertPersonnel(personnel: Personnel): Route = {
    val statement = DBIO.seq(PersonnelTable.query += personnel)

    val _ = Await.result(db.run(statement), timeout)

    complete("Ok")
  }

  /**
   * Unlike hospital, pharmacy has the foreign key to hospital identification.
   * Before insertion, user might need to first discover the hospital ident by specific condition matching.
   * TODO: We may consider to support automatically match this by opening two sql statements transactionally.
   */
  implicit def insertPharmacy(pharmacy: Pharmacy): Route = {
    val statement = DBIO.seq(PharmacyTable.query += pharmacy)

    val _ = Await.result(db.run(statement), timeout)

    complete("Ok")
  }
}
