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
import kuery.BenchService
import kuery.model.{HospitalTable, PersonnelTable, PharmacyTable}
import slick.jdbc.MySQLProfile.api._
import slick.lifted.AbstractTable

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

/** Cake pattern for injecting database in common services */
trait DatabaseService {

  /** Have to inject the execution context. */
  implicit val executionContext: ExecutionContext

  /** For overriding this during class initialization. */
  val db: Database

  /** Basic listing function, SELECT * FROM table */
  def list[T <: AbstractTable[_]](implicit query: TableQuery[T]) =
    get {
      val future = db.run(query.result) map {
        _.map(_.toString).reduce(_ + "\n" + _)
      }
      val text = Await.result(future, 10 seconds)
      complete(text)
    }
}

trait Sequelizer extends BenchService with SearchService {
  this: DatabaseService =>

  override def hospitalRoute: Route = {
    import HospitalTable.query
    path("hospital") {
      list
    }
  }

  override def personnelRoute: Route = {
    import PersonnelTable.query
    pathPrefix("personnel") {
      joinSearch ~ jobSearch ~ list
    }
  }

  override def pharmacyRoute: Route = {
    import PharmacyTable.query
    path("pharmacy") {
      list
    }
  }
}

object Sequelize {

  def apply()(implicit executionContext: ExecutionContext) = new Sequelize
}

class Sequelize()(implicit val executionContext: ExecutionContext) extends Sequelizer with DatabaseService {

  override val db: Database = Database.forConfig("slicker")

  override def guard: (=> Route) => Route = pathPrefix("sql")
}
