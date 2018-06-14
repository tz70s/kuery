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

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object Index {

  private val index =
    """|Howdy, here's the kuery playground:
       |
       |/plain               => route for testing pure http performance with short plain text.
       |/sql/:route          => route for testing legacy sql performance with various sub-routes.
       |/nosql/:route        => route for testing nosql performance with various sub-routes.
       |/newsql/:route       => route for testing newsql performance with various sub-routes.
       |
    """.stripMargin

  private val plain = "Howdy!"

  // Default index route.
  val route: Route =
    get {
      path("") {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, index))
      } ~
        path("plain") {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, plain))
        }
    }
}
