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
import kuery.{BenchService, PostService}
import pureconfig.loadConfigOrThrow
import spray.json._

import scala.concurrent.ExecutionContext

case class CouchConfig(uri: String)

object Couch {
  def apply()(implicit system: ActorSystem, materializer: Materializer, executionContext: ExecutionContext) =
    new Couch()
}

trait DocumentService {

  implicit val system: ActorSystem

  implicit val executionContext: ExecutionContext

  val http: HttpExt

  val baseUri: String
}

trait SelectAllDocumentService extends DocumentService {

  def selectAllDoc(model: String): Route =
    get {
      val listView = s"$baseUri/_design/$model/_view/$model"
      complete(http.singleRequest(HttpRequest(uri = listView)))
    }

  def bigJoin(model: String): Route =
    path("join") {
      get {
        val joinView = s"$baseUri/_design/$model/_view/join"
        complete(http.singleRequest(HttpRequest(uri = joinView)))
      }
    }

}

trait PostDocumentService extends JsonSupport with DocumentService {

  import HospitalJsonProtocol._
  import PersonnelJsonProtocol._

  private[this] def typeInjection[DocModel](model: DocModel) = {
    model match {
      case hospital: Hospital =>
        JsObject("hospital" -> hospital.toJson)
      case personnel: Personnel =>
        JsObject("personnel" -> personnel.toJson)
      case pharmacy: Pharmacy =>
        JsObject("pharmacy" -> pharmacy.toJson)
      case _ => throw new DeserializationException("Document object expected.")
    }
  }

  def postDocument[DocModel](model: DocModel): Route = {
    val injectModel = typeInjection(model)
    val entityFuture = Marshal(injectModel).to[RequestEntity]
    val resp = entityFuture.flatMap { entity =>
      val request = HttpRequest(uri = baseUri, method = HttpMethods.POST, entity = entity)
      http.singleRequest(request)
    }
    complete(resp)
  }
}

class Couch()(implicit val system: ActorSystem, val materializer: Materializer, val executionContext: ExecutionContext)
    extends BenchService
    with PostService
    with PostDocumentService
    with SelectAllDocumentService {

  val config = loadConfigOrThrow[CouchConfig]("couch")

  override val baseUri: String = s"${config.uri}/medical"

  override def guard: (=> Route) => Route = pathPrefix("nosql")

  override val http: HttpExt = Http()

  override def hospitalRoute: Route = selectAllDoc("hospital") ~ postHospital(postDocument)

  override def personnelRoute: Route =
    bigJoin("personnel") ~ selectAllDoc("personnel") ~ postPersonnel(postDocument)

  override def pharmacyRoute: Route = selectAllDoc("pharmacy") ~ postPharmacy(postDocument)
}
