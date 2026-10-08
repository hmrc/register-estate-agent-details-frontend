/*
 * Copyright 2026 HM Revenue & Customs
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

package models.pages

import base.RegistrationSpecBase
import play.api.libs.json.Json

class AddressSpec extends RegistrationSpecBase {

  private val uk   = UKAddress("line1", "line2", postcode = "AB1 1AB")
  private val intl = InternationalAddress("line1", "line2", country = "FR")

  "UKAddress" must {

    "write postCode and a GB country" in {
      Json.toJson(uk)(using UKAddress.writes) mustBe
        Json.obj("line1" -> "line1", "line2" -> "line2", "postCode" -> "AB1 1AB", "country" -> "GB")
    }

    "read from postCode" in {
      Json
        .obj("line1" -> "line1", "line2" -> "line2", "postCode" -> "AB1 1AB")
        .validate[UKAddress](using UKAddress.reads)
        .asOpt mustBe Some(uk)
    }
  }

  "Address" must {

    "read a UK address" in {
      Json.toJson[Address](uk).validate[Address].asOpt mustBe Some(uk)
    }

    "fall back to an international address when there is no postCode" in {
      Json.toJson[Address](intl).validate[Address].asOpt mustBe Some(intl)
    }

    "write a UK address" in {
      Json.toJson[Address](uk) mustBe Json.toJson(uk)(using UKAddress.writes)
    }

    "write an international address" in {
      Json.toJson[Address](intl) mustBe Json.toJson(intl)(using InternationalAddress.format)
    }
  }

}
