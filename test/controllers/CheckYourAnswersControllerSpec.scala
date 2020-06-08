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

package controllers

import base.SpecBase
import models.UserAnswers
import models.pages.{InternationalAddress, UKAddress}
import pages.{AgentInternalReferencePage, AgentInternationalAddressPage, AgentNamePage, AgentTelephoneNumberPage, AgentUKAddressPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase {

  "Check Your Answers Controller" must {

    "return OK and the correct view for a UK address GET" in {

      val answers: UserAnswers =
        emptyUserAnswers
          .set(AgentTelephoneNumberPage, "123456789").success.value
          .set(AgentUKAddressPage, UKAddress("Line1", "Line2", None, Some("TownOrCity"), "NE62RT")).success.value
          .set(AgentNamePage, "Sam Curran Trust").success.value
          .set(AgentInternalReferencePage, "123456789").success.value

      val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.agentInternalReference.value,
            checkYourAnswersHelper.agentName.value,
            checkYourAnswersHelper.agentUKAddress.value,
            checkYourAnswersHelper.agentTelephoneNumber.value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for a International address GET" in {

      val answers: UserAnswers =
        emptyUserAnswers
          .set(AgentTelephoneNumberPage, "123456789").success.value
          .set(AgentInternationalAddressPage, InternationalAddress("Line1", "Line2", None, "Country")).success.value
          .set(AgentNamePage, "Sam Curran Trust").success.value
          .set(AgentInternalReferencePage, "123456789").success.value

      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.agentInternalReference.value,
            checkYourAnswersHelper.agentName.value,
            checkYourAnswersHelper.agentInternationalAddress.value,
            checkYourAnswersHelper.agentTelephoneNumber.value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(expectedSections)(fakeRequest, messages).toString

      application.stop()
    }


    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
