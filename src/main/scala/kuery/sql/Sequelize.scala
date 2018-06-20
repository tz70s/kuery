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
import kuery.{BenchConfig, BenchService, PostService}
import kuery.model.{HospitalTable, PersonnelTable, PharmacyTable}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

/** Cake pattern for injecting database in common services */
trait Sequelizer {

  /** Have to inject the execution context. */
  implicit val executionContext: ExecutionContext

  val timeout = BenchConfig.timeout

  /** For overriding this during class initialization. */
  val db: Database
}

object Sequelize {
  def apply()(implicit executionContext: ExecutionContext) = new Sequelize
}

class Sequelize()(implicit val executionContext: ExecutionContext)
    extends BenchService
    with SearchService
    with PostService
    with InsertService
    with SelectAllService
    with Sequelizer {

  override val db: Database = Database.forConfig("sql")

  override def guard: (=> Route) => Route = pathPrefix("sql")

  override def hospitalRoute: Route = postHospital(insertHospital) ~ selectAll(HospitalTable.query)

  override def personnelRoute: Route =
    postPersonnel(insertPersonnel) ~ jobSearch ~ joinSearch ~ selectAll(PersonnelTable.query)

  override def pharmacyRoute: Route = postPharmacy(insertPharmacy) ~ selectAll(PharmacyTable.query)
}
