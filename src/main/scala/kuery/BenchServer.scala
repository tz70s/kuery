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

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import kuery.nosql.Couch
import kuery.sql.Sequelize
import pureconfig.loadConfigOrThrow

import scala.concurrent.duration._

object BenchServer {

  private val logger = Logger(this.getClass)

  val timeout = loadConfigOrThrow[Int]("kuery.timeout") seconds

  def router[R <: Router](routers: R*): Route = {
    routers.map(_.route).reduce(_ ~ _)
  }

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("kuery-actor-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val server = "0.0.0.0"
    val port = loadConfigOrThrow[Int]("kuery.port")

    logger.info(s"Start comparison with SQLs! http://localhost:8080")

    val route = router(Primitive, Sequelize(), Couch())

    val bindingFuture = Http().bindAndHandle(route, server, port)

    bindingFuture.failed.foreach { ex =>
      logger.error("Failed to binding server address. {}", ex.getMessage)
    }
  }
}
