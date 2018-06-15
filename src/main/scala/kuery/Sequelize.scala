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

package kuery

import slick.jdbc.MySQLProfile.api._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import kuery.model.{HospitalTable, PersonnelTable, PharmacyTable, Searchable}
import slick.lifted.AbstractTable

import scala.util.Try

trait Sequelizer extends BenchService {

  /** Have to inject the execution context. */
  implicit val executionContext: ExecutionContext

  /** Use lazy to ensure initialization ordering. */
  protected lazy val db = Database.forConfig("slicker")

  protected val hospitals = HospitalTable.query

  protected val personnels = PersonnelTable.query

  protected val pharmacies = PharmacyTable.query

  protected def list[T <: AbstractTable[_]](query: TableQuery[T], timeout: Duration) =
    get {
      val future = db.run(query.result) map {
        _.map(_.toString).reduce(_ + "\n" + _)
      }
      val text = Await.result(future, timeout)
      complete(text)
    }

  protected def search[T <: AbstractTable[_]](model: String, query: TableQuery[T], timeout: Duration) =
    path(model) {
      parameter('name) { name =>
        get {
          val fut =
            db.run(query.result) map {
              _.filter { s =>
                Try {
                  // TODO: consider to use the strict type system
                  s.asInstanceOf[Searchable]
                } map { _.name == name } getOrElse false
              }
            }
          val level = Await.result(fut, timeout)
          complete(level.map(_.toString).reduce(_ + "\n" + _))
        }
      } ~ list(query, timeout)
    }

  override def hospitalRoute: Route = search("hospital", hospitals, 3 seconds)

  override def personnelRoute: Route = search("personnel", personnels, 3 seconds)

  override def pharmacyRoute: Route = search("pharmacy", pharmacies, 3 seconds)

}

object Sequelize {
  def apply()(implicit executionContext: ExecutionContext) = new Sequelize
}

class Sequelize(implicit val executionContext: ExecutionContext) extends Sequelizer {

  override def guard: (=> Route) => Route = pathPrefix("sql")
}
