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
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import pureconfig.loadConfig
import slick.jdbc.JdbcBackend.Database

object RestServer {

  private val logger = Logger(this.getClass)

  val route = Index.route

  val db = Database.forConfig("slicker")

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("kuery-actor-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val server = "0.0.0.0"
    val port = loadConfig[Int]("kuery.port") getOrElse 8080

    logger.info(s"Start comparison with SQLs! http://localhost:8080")

    val bindingFuture = Http().bindAndHandle(route, server, port)

    bindingFuture.failed.foreach { ex =>
      logger.error("Failed to binding server address. {}", ex.getMessage)
      db.close()
    }
  }
}
