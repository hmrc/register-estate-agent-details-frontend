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

package utils

import base.RegistrationSpecBase
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.{AnswerRow, AnswerSection, RepeaterAnswerRow, RepeaterAnswerSection}

class SectionFormatterSpec extends RegistrationSpecBase {

  private val row = AnswerRow(
    label = "agentName.checkYourAnswersLabel",
    answer = Html("Agency Ltd"),
    changeUrl = Some("/change-agent-name")
  )

  "SectionFormatter" when {

    "given an answer section" must {

      "map each answer row onto a summary list row" in {

        val result = SectionFormatter.formatSections(Seq(AnswerSection(rows = Seq(row))))

        result.size mustBe 1

        val summaryRow = result.head

        summaryRow.key.content   mustBe Text(messages("agentName.checkYourAnswersLabel"))
        summaryRow.key.classes   mustBe "govuk-!-width-two-thirds"
        summaryRow.value.content mustBe HtmlContent(Html("Agency Ltd"))
        summaryRow.value.classes mustBe "govuk-!-width-one-half"
      }

      "give each row a change action indexed by its position" in {

        val secondRow = row.copy(label = "agentTelephoneNumber.checkYourAnswersLabel")

        val result = SectionFormatter.formatSections(Seq(AnswerSection(rows = Seq(row, secondRow))))

        val actions = result.map(_.actions.value.items.head)

        actions.map(_.classes)                mustBe Seq("change-link-0", "change-link-1")
        actions.head.href                     mustBe "/change-agent-name"
        actions.head.content                  mustBe Text(messages("site.edit"))
        actions.head.visuallyHiddenText.value mustBe messages("agentName.checkYourAnswersLabel")
      }

      "fall back to an empty href when a row has no change URL" in {

        val result = SectionFormatter.formatSections(
          Seq(AnswerSection(rows = Seq(row.copy(changeUrl = None))))
        )

        result.head.actions.value.items.head.href mustBe ""
      }

      "flatten rows across several sections" in {

        val sections = Seq(
          AnswerSection(rows = Seq(row)),
          AnswerSection(rows = Seq(row, row))
        )

        SectionFormatter.formatSections(sections).size mustBe 3
      }

      "return nothing when there are no sections" in {
        SectionFormatter.formatSections(Nil) mustBe empty
      }
    }

    "given a repeater answer section" must {

      "throw, because the repeater pattern is not used by this service" in {

        val repeater = RepeaterAnswerSection(
          headingKey = "heading",
          relevanceRow = row,
          rows = Seq(RepeaterAnswerRow("answer", "/change", "/delete")),
          addLinkKey = "add",
          addLinkUrl = "/add"
        )

        intercept[NotImplementedError] {
          SectionFormatter.formatSections(Seq(repeater))
        }
      }
    }
  }

}
