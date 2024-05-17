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

package views

import forms.AgentInternationalAddressFormProvider
import models.NormalMode
import models.pages.InternationalAddress
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.countryOptions.{CountryOptionsNonUK, InputOption}
import views.behaviours.QuestionViewBehaviours
import views.html.AgentInternationalAddressView

class AgentInternationalAddressViewSpec extends QuestionViewBehaviours[InternationalAddress] {

  val messageKeyPrefix = "site.address.international"
  val agencyName = "Hadrian"

  override val form = new AgentInternationalAddressFormProvider()()

  "AgentInternationalAddressView" must {

    val view = viewFor[AgentInternationalAddressView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options()

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, NormalMode, agencyName)(fakeRequest, messages)


    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, agencyName)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      Seq(("line1",None), ("line2",None), ("line3", None)),
      agencyName
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
