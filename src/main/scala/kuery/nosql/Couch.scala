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
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import kuery.model._
import kuery.{BenchServer, BenchService, PostService}
import pureconfig.loadConfigOrThrow

import scala.concurrent.{Await, ExecutionContext}

case class CouchConfig(val uri: String)

object Couch {
  def apply()(implicit system: ActorSystem, materializer: Materializer, executionContext: ExecutionContext) =
    new Couch()
}

trait PostDocService extends JsonSupport {

  import HospitalJsonProtocol._
  import PersonnelJsonProtocol._

  val postUrl: String

  implicit val system: ActorSystem

  implicit val executionContext: ExecutionContext

  val http: HttpExt

  def postHospitalDoc(hospital: Hospital): Route = {
    val resp = Marshal(hospital).to[RequestEntity] flatMap { entity =>
      val request = HttpRequest(uri = postUrl, method = HttpMethods.POST, entity = entity)
      http.singleRequest(request)
    }
    complete(resp)
  }

  def postPersonnelDoc(personnel: Personnel): Route = {
    val resp = Marshal(personnel).to[RequestEntity] flatMap { entity =>
      val request = HttpRequest(uri = postUrl, method = HttpMethods.POST, entity = entity)
      http.singleRequest(request)
    }
    complete(resp)
  }

  def postPharmacyDoc(pharmacy: Pharmacy): Route = {
    val resp = Marshal(pharmacy).to[RequestEntity] flatMap { entity =>
      val request = HttpRequest(uri = postUrl, method = HttpMethods.POST, entity = entity)
      http.singleRequest(request)
    }
    complete(resp)
  }

}

class Couch()(implicit val system: ActorSystem, val materializer: Materializer, val executionContext: ExecutionContext)
    extends BenchService
    with PostService
    with PostDocService {

  val config = loadConfigOrThrow[CouchConfig]("couch")

  override val postUrl: String = s"${config.uri}/medical"

  override def guard: (=> Route) => Route = pathPrefix("nosql")

  override val http: HttpExt = Http()

  override def hospitalRoute: Route =
    path("hospital") {
      get {
        val search = s"${config.uri}/medical/_design/hospital/_view/hospital"
        complete(http.singleRequest(HttpRequest(uri = search)))
      } ~ postHospital(postHospitalDoc)
    }

  override def personnelRoute: Route =
    path("personnel") {
      get {
        val search = s"${config.uri}/medical/_design/personnel/_view/personnel"
        complete(http.singleRequest(HttpRequest(uri = search)))
      } ~ postPersonnel(postPersonnelDoc)
    }

  override def pharmacyRoute: Route =
    path("pharmacy") {
      get {
        val search = s"${config.uri}/medical/_design/pharmacy/_view/pharmacy"
        complete(http.singleRequest(HttpRequest(uri = search)))
      } ~ postPharmacy(postPharmacyDoc)
    }
}
