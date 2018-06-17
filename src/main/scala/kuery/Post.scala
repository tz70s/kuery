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

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import kuery.model._

/**
 * DSL trait for post route.
 * Only do unmarshalling, by passing various function into this.
 */
trait PostService extends JsonSupport {

  import HospitalJsonProtocol._
  import PersonnelJsonProtocol._

  def postHospital(r: Hospital => Route): Route =
    post {
      entity(as[Hospital]) { hos =>
        r(hos)
      }
    }

  def postPersonnel(p: Personnel => Route): Route =
    post {
      entity(as[Personnel]) { per =>
        p(per)
      }
    }

  def postPharmacy(p: Pharmacy => Route): Route =
    post {
      entity(as[Pharmacy]) { pharmacy =>
        p(pharmacy)
      }
    }
}
