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

package utils.print

import base.RegistrationSpecBase
import models.pages.InternationalAddress

class CheckAnswersFormattersSpec extends RegistrationSpecBase {

  private val formatters = injector.instanceOf[CheckAnswersFormatters]

  "internationalAddress" must {

    "leave the country blank when the code is not recognised" in {
      formatters.internationalAddress(InternationalAddress("line1", "line2", None, "XX")).toString mustBe
        "line1<br />line2<br />"
    }
  }
}