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

package config

import base.SpecBase

class FrontendAppConfigSpec extends SpecBase {

  "FrontendAppConfig" must {

    "ask feedback-frontend to render the exit survey with the service navigation component" in {
      frontendAppConfig.feedbackFrontendUrl mustBe
        "http://localhost:9514/feedback/estates?useServiceNavigation"
    }

    "sign out through bas-gateway" in {
      frontendAppConfig.logoutWithBasGatewayUrl mustBe "http://localhost:9553/gg/sign-out"
    }

    "keep the time-out continue URL pointing at this service" in {
      frontendAppConfig.timeOutUrl mustBe
        "http://localhost:8826/register-an-estate/agent-details/this-service-has-been-reset"
    }

    "expose the language constants used by the country lists and language switch" in {
      frontendAppConfig.ENGLISH         mustBe "en"
      frontendAppConfig.WELSH           mustBe "cy"
      frontendAppConfig.UK_COUNTRY_CODE mustBe "GB"
    }

    "route the language switch through the LanguageSwitchController" in {
      frontendAppConfig.routeToSwitchLanguage("cymraeg") mustBe
        controllers.routes.LanguageSwitchController.switchToLanguage("cymraeg")
    }
  }

}
