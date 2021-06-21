/*
 * Copyright 2021 HM Revenue & Customs
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

import models.UserAnswers
import models.pages.{InternationalAddress, UKAddress}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.{Html, HtmlFormat}
import queries.Gettable
import viewmodels.AnswerRow

import javax.inject.Inject

class CheckYourAnswersHelper @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def bind(userAnswers: UserAnswers, name: String)(implicit messages: Messages): Bound = new Bound(userAnswers, name)

  class Bound(userAnswers: UserAnswers, agencyName: String)
             (implicit messages: Messages) {

    def stringQuestion(query: Gettable[String], labelKey: String, changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, HtmlFormat.escape, changeUrl)
    }

    def yesNoQuestion(query: Gettable[Boolean], labelKey: String, changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.yesOrNo, changeUrl)
    }

    def ukAddressQuestion(query: Gettable[UKAddress], labelKey: String, changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.ukAddress, changeUrl)
    }

    def internationalAddressQuestion(query: Gettable[InternationalAddress], labelKey: String, changeUrl: String): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.internationalAddress, changeUrl)
    }

    private def question[T](query: Gettable[T],
                            labelKey: String,
                            format: T => Html,
                            changeUrl: String)
                           (implicit rds: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          label = s"$labelKey.checkYourAnswersLabel",
          answer = format(x),
          changeUrl = Some(changeUrl),
          labelArg = agencyName
        )
      }
    }
  }
}
