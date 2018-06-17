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

package kuery.nosql

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import kuery.BenchService
import pureconfig.loadConfigOrThrow

import scala.concurrent.ExecutionContext

case class CouchConfig(val uri: String)

object Couch {
  def apply()(implicit system: ActorSystem, materializer: Materializer, executionContext: ExecutionContext) =
    new Couch()
}

class Couch()(implicit system: ActorSystem, materializer: Materializer, executionContext: ExecutionContext)
    extends BenchService {

  // TODO: temporary let it throw out.
  val config = loadConfigOrThrow[CouchConfig]("couch")

  override def guard: (=> Route) => Route = pathPrefix("nosql")

  override def hospitalRoute: Route =
    path("hospital") {
      get {
        val search = s"${config.uri}/medical/_design/hospital/_view/hospital"
        complete(Http().singleRequest(HttpRequest(uri = search)))
      }
    }

  override def personnelRoute: Route =
    path("personnel") {
      get {
        val search = s"${config.uri}/medical/_design/personnel/_view/personnel"
        complete(Http().singleRequest(HttpRequest(uri = search)))
      }
    }

  override def pharmacyRoute: Route =
    path("pharmacy") {
      get {
        val search = s"${config.uri}/medical/_design/pharmacy/_view/pharmacy"
        complete(Http().singleRequest(HttpRequest(uri = search)))
      }
    }
}
