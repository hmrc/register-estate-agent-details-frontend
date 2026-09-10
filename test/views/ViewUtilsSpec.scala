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

package views

import base.RegistrationSpecBase
import forms.AgentNameFormProvider
import play.api.data.{Form, FormError}
import uk.gov.hmrc.govukfrontend.views.html.components.Text
import viewmodels.RadioOption

class ViewUtilsSpec extends RegistrationSpecBase {

  private val form: Form[String] = new AgentNameFormProvider()()

  "errorPrefix" must {

    "prefix the title when the form has errors" in {
      val withError = form.bind(Map("value" -> ""))
      withError.hasErrors              mustBe true
      ViewUtils.errorPrefix(withError) mustBe s"${messages("error.browser.title.prefix")} "
    }

    "leave the title alone when the form is clean" in {
      ViewUtils.errorPrefix(form) mustBe ""
    }
  }

  "breadcrumbTitle" must {

    "append the service name and GOV.UK" in {
      ViewUtils.breadcrumbTitle("Agent name") mustBe
        s"Agent name - ${messages("service.name")} - GOV.UK"
    }
  }

  "isDateError" must {

    "spot date and when keys, whatever the case" in {
      ViewUtils.isDateError("startDate")    mustBe true
      ViewUtils.isDateError("WhenDidItEnd") mustBe true
      ViewUtils.isDateError("agentName")    mustBe false
    }
  }

  "errorHref" when {

    "the error carries a date part" must {
      "point at that part of the date input" in {
        ViewUtils.errorHref(FormError("startDate", "error.required", Seq("month"))) mustBe "startDate.month"
      }
    }

    "the field is a yes/no question" must {
      "point at the yes radio" in {
        ViewUtils.errorHref(FormError("value", "error.required"), isYesNo = true) mustBe "value-yes"
      }
    }

    "the field is a radio group" must {
      "point at the first option" in {
        val options = Seq(RadioOption("prefix", "first"), RadioOption("prefix", "second"))
        ViewUtils.errorHref(FormError("value", "error.required"), radioOptions = options) mustBe "prefix.first"
      }
    }

    "the field is a date" must {

      "point at the day when the key names a date" in {
        ViewUtils.errorHref(FormError("startDate", "error.required")) mustBe "startDate.day"
      }

      "point at the day when the message names a date" in {
        ViewUtils.errorHref(FormError("value", "error.date.required")) mustBe "value.day"
      }

      "point at the field itself when the message is a yes/no date message" in {
        ViewUtils.errorHref(FormError("value", "error.dateYesNo.required")) mustBe "value"
      }
    }

    "the field is an ordinary input" must {
      "point at the field itself" in {
        ViewUtils.errorHref(FormError("value", "error.required")) mustBe "value"
      }
    }
  }

  "mapRadioOptionsToRadioItems" must {

    "carry id, value and message across, checking the selected option" in {

      val options = Seq(RadioOption("prefix", "first"), RadioOption("prefix", "second"))
      val field   = form.fill("second")("value")

      val items = ViewUtils.mapRadioOptionsToRadioItems(field, options)

      items.map(_.id)      mustBe Seq(Some("prefix.first"), Some("prefix.second"))
      items.map(_.value)   mustBe Seq(Some("first"), Some("second"))
      items.map(_.checked) mustBe Seq(false, true)
      items.head.content   mustBe Text(messages("prefix.first"))
    }

    "produce nothing when there are no options" in {
      ViewUtils.mapRadioOptionsToRadioItems(form("value"), Nil) mustBe empty
    }
  }

}
