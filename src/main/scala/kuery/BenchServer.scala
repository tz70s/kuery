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

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.event.slf4j.Logger
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import kamon.Kamon
import kamon.prometheus.PrometheusReporter
import kamon.zipkin.ZipkinReporter
import kuery.nosql.Couch
import kuery.sql.Sequelize
import pureconfig.loadConfigOrThrow

import scala.concurrent.Future
import scala.concurrent.duration._

object BenchConfig {
  val timeout = loadConfigOrThrow[Int]("kuery.timeout") seconds
}

object BenchServer {

  private[this] val kLogger = Logger("kuery.bench")

  private[this] def routers[R <: Router](routers: R*): Route = {
    routers map (_.route) reduce (_ ~ _)
  }

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("kuery-actor-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val server = "0.0.0.0"
    val port = loadConfigOrThrow[Int]("kuery.port")

    Kamon.addReporter(new PrometheusReporter())
    Kamon.addReporter(new ZipkinReporter())

    kLogger.info(s"Spawn the benchmarking server for SQLs execution, http://127.0.0.1:$port")

    val sequelize = Sequelize()
    val route = routers(Primitive, sequelize, Couch())

    val bindingFuture = Http().bindAndHandle(route, server, port)

    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseActorSystemTerminate, "closeDB") { () =>
      kLogger.info("Clean up database connection.")
      sequelize.closeOut()
      Future.successful(Done)
    }

    bindingFuture.failed.foreach { ex =>
      Console.err.print("Failed to binding server address. {}", ex.getMessage)
    }
  }
}
