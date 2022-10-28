/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBase
import models.CheckMode
import models.pages.{InternationalAddress, UKAddress}
import pages._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}
import controllers.routes._

class AgentDetailsPrinterSpec extends SpecBase {

  val printHelper: AgentDetailsPrinter = injector.instanceOf[AgentDetailsPrinter]
  val agentName: String = "Name"

  "AgentDetailsPrinter" must {

    "return answer section" when {

      val baseAnswers = emptyUserAnswers
        .set(AgentInternalReferencePage, "ref").success.value
        .set(AgentNamePage, agentName).success.value
        .set(AgentTelephoneNumberPage, "tel").success.value

      "UK address" in {
        val answers = baseAnswers
          .set(AgentUKAddressYesNoPage, true).success.value
          .set(AgentUKAddressPage, UKAddress("Line 1", "Line 2", None, None, "AB1 1AB")).success.value

        val result = printHelper.apply(answers)
        // scalastyle:off
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow("agentInternalReference.checkYourAnswersLabel", Html("ref"), Some(AgentInternalReferenceController.onPageLoad(CheckMode).url), agentName),
            AnswerRow("agentName.checkYourAnswersLabel", Html(agentName), Some(AgentNameController.onPageLoad(CheckMode).url), agentName),
            AnswerRow("agentUKAddressYesNo.checkYourAnswersLabel", Html("Yes"), Some(AgentUKAddressYesNoController.onPageLoad(CheckMode).url), agentName),
            AnswerRow("site.address.uk.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />AB1 1AB"), Some(AgentUKAddressController.onPageLoad(CheckMode).url), agentName),
            AnswerRow("agentTelephoneNumber.checkYourAnswersLabel", Html("tel"), Some(AgentTelephoneNumberController.onPageLoad(CheckMode).url), agentName)
          ),
          sectionKey = None
        )
      }
      // scalastyle:on
      "international address" in {
        val answers = baseAnswers
          .set(AgentUKAddressYesNoPage, false).success.value
          .set(AgentInternationalAddressPage, InternationalAddress("Line 1", "Line 2", None, "FR")).success.value

        val result = printHelper.apply(answers)

        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow("agentInternalReference.checkYourAnswersLabel", Html("ref"), Some(AgentInternalReferenceController.onPageLoad(CheckMode).url), agentName),
            AnswerRow("agentName.checkYourAnswersLabel", Html(agentName), Some(AgentNameController.onPageLoad(CheckMode).url), agentName),
            AnswerRow("agentUKAddressYesNo.checkYourAnswersLabel", Html("No"), Some(AgentUKAddressYesNoController.onPageLoad(CheckMode).url), agentName),
            AnswerRow("site.address.international.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />France"), Some(AgentInternationalAddressController.onPageLoad(CheckMode).url), agentName),
            AnswerRow("agentTelephoneNumber.checkYourAnswersLabel", Html("tel"), Some(AgentTelephoneNumberController.onPageLoad(CheckMode).url), agentName)
          ),
          sectionKey = None
        )
      }
    }
  }

}
