/*
 * Copyright 2023 HM Revenue & Customs
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

package navigation

import base.RegistrationSpecBase
import pages._
import models._

class AgentNavigatorSpec extends RegistrationSpecBase {

  val navigator = new AgentNavigator

  "Agent navigator" when {

    "add journey navigation" must {

      val mode = NormalMode

      "Agent Internal Reference page -> Agent name page" in {
        navigator.nextPage(AgentInternalReferencePage, mode, emptyUserAnswers)
          .mustBe(controllers.routes.AgentNameController.onPageLoad(mode))
      }

      "Agent name page -> Agent UK Address Yes No page" in {
        navigator.nextPage(AgentNamePage, mode, emptyUserAnswers)
          .mustBe(controllers.routes.AgentUKAddressYesNoController.onPageLoad(mode))
      }

      "Agent UK Address Yes No page -> Agent UK Address page - when user answers yes" in {
        val answers = emptyUserAnswers.set(AgentUKAddressYesNoPage, value = true).success.value
        navigator.nextPage(AgentUKAddressYesNoPage, mode, answers)
          .mustBe(controllers.routes.AgentUKAddressController.onPageLoad(mode))
      }

      "Agent UK Address-> Agent Telephone Number page" in {
        navigator.nextPage(AgentUKAddressPage, mode, emptyUserAnswers)
          .mustBe(controllers.routes.AgentTelephoneNumberController.onPageLoad(mode))
      }

      "Agent UK Address Yes No page -> Agent International Address page - when user answers no" in {
        val answers = emptyUserAnswers.set(AgentUKAddressYesNoPage, value = false).success.value
        navigator.nextPage(AgentUKAddressYesNoPage, mode, answers)
          .mustBe(controllers.routes.AgentInternationalAddressController.onPageLoad(mode))
      }

      "Agent International Address-> Agent Telephone Number page" in {
        navigator.nextPage(AgentInternationalAddressPage, mode, emptyUserAnswers)
          .mustBe(controllers.routes.AgentTelephoneNumberController.onPageLoad(mode))
      }

      "Agent Telephone Number page -> Check Answers page" in {
        navigator.nextPage(AgentTelephoneNumberPage, mode, emptyUserAnswers)
          .mustBe(controllers.routes.CheckYourAnswersController.onPageLoad)
      }

    }

  }

}
