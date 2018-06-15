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

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

/** Utility trait for construction. */
trait Router {
  def route: Route
}

/** Middleware for integrating bench service routes. */
trait BenchService extends Router {

  final override def route: Route =
    guard {
      hospitalRoute ~ personnelRoute ~ pharmacyRoute
    }

  /**
   * DSL for the nicer expression.
   * The guard will be a pathPrefix, including: sql, nosql and newsql.
   * @return Route the akka server route.
   */
  def guard: (=> Route) => Route

  /**
   * Hospital relative route.
   *
   * GET /guard/hospital will list all hospital in table.
   * GET /guard/hospital?name=NiceHospital will return the row of matching hospital.
   *
   * @return Route the akka server route.
   */
  def hospitalRoute: Route

  /**
   * Personnel relative route.
   *
   * GET /guard/personnel will list all personnel in table.
   * GET /guard/personnel?name=Jon will return the row of matching personnel.
   *
   * @return Route the akka server route.
   */
  def personnelRoute: Route

  /**
   * Pharmacy relative route.
   *
   * GET /guard/pharmacy will list all personnel in table.
   * GET /guard/pharmacy?name=nurse will return the row of matching pharmacy.
   *
   * @return Route the akka server route.
   */
  def pharmacyRoute: Route
}
