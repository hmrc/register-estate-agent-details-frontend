/*
 * Copyright 2020 HM Revenue & Customs
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

package utils

import java.time.format.DateTimeFormatter
import javax.inject.Inject
import controllers.routes
import models.{CheckMode, NormalMode, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import viewmodels.AnswerRow
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions


class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)
                                      (userAnswers: UserAnswers)(implicit messages: Messages) {

  def agentUKAddress: Option[AnswerRow] = userAnswers.get(AgentUKAddressPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("site.address.uk.checkYourAnswersLabel")),
        ukAddress(x),
        routes.AgentUKAddressController.onPageLoad(NormalMode).url,
        agencyName(userAnswers)
      )
  }

  def agentName: Option[AnswerRow] = userAnswers.get(AgentNamePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("agentName.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        routes.AgentNameController.onPageLoad(CheckMode).url
      )
  }

  def agentInternationalAddress: Option[AnswerRow] = userAnswers.get(AgentInternationalAddressPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("agentInternationalAddress.checkYourAnswersLabel")),
        internationalAddress(x, countryOptions),
        routes.AgentInternationalAddressController.onPageLoad(CheckMode).url,
        agencyName(userAnswers)
      )
  }

  def agentTelephoneNumber: Option[AnswerRow] = userAnswers.get(AgentTelephoneNumberPage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("agentTelephoneNumber.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        routes.AgentTelephoneNumberController.onPageLoad(CheckMode).url
      )
  }

  def agentInternalReference: Option[AnswerRow] = userAnswers.get(AgentInternalReferencePage) map {
    x =>
      AnswerRow(
        HtmlFormat.escape(messages("agentInternalReference.checkYourAnswersLabel")),
        HtmlFormat.escape(x),
        routes.AgentInternalReferenceController.onPageLoad(CheckMode).url
      )
  }

  private def yesOrNo(answer: Boolean)(implicit messages: Messages): Html =
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }
}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
