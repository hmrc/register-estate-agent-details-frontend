/*
 * Copyright 2024 HM Revenue & Customs
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

import controllers.routes._
import models.{CheckMode, UserAnswers}
import pages._
import play.api.i18n.Messages
import viewmodels.AnswerSection

import javax.inject.Inject

class AgentDetailsPrinter @Inject()(checkYourAnswersHelper: CheckYourAnswersHelper) {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): AnswerSection = {

    val name = userAnswers.get(AgentNamePage).getOrElse("")

    val bound = checkYourAnswersHelper.bind(userAnswers, name)

    AnswerSection(
      None,
      Seq(
        bound.stringQuestion(AgentInternalReferencePage, "agentInternalReference", AgentInternalReferenceController.onPageLoad(CheckMode).url),
        bound.stringQuestion(AgentNamePage, "agentName", AgentNameController.onPageLoad(CheckMode).url),
        bound.yesNoQuestion(AgentUKAddressYesNoPage, "agentUKAddressYesNo", AgentUKAddressYesNoController.onPageLoad(CheckMode).url),
        bound.ukAddressQuestion(AgentUKAddressPage, "site.address.uk", AgentUKAddressController.onPageLoad(CheckMode).url),
        bound.internationalAddressQuestion(AgentInternationalAddressPage, "site.address.international", AgentInternationalAddressController.onPageLoad(CheckMode).url),
        bound.stringQuestion(AgentTelephoneNumberPage, "agentTelephoneNumber", AgentTelephoneNumberController.onPageLoad(CheckMode).url)
      ).flatten
    )

  }

}
