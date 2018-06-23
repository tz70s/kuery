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

import akka.http.scaladsl.server.Directives.{complete, get}
import akka.http.scaladsl.server.Route
import slick.lifted.AbstractTable
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await

trait SelectAllService {
  this: Sequelizer =>

  /** Basic listing function, SELECT * FROM table */
  def selectAll[T <: AbstractTable[_]](query: TableQuery[T]): Route =
    get {
      val future = db.run(query.result) map {
        _ map (_.toString) reduce (_ + "\n" + _)
      }
      val text = Await.result(future, timeout)
      complete(text)
    }
}
