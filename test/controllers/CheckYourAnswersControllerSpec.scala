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

package controllers

import base.RegistrationSpecBase
import connector.EstateConnector
import models.UserAnswers
import models.pages.{InternationalAddress, UKAddress}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends RegistrationSpecBase with MockitoSugar with ScalaFutures {

  lazy val submitRoute : String = controllers.routes.CheckYourAnswersController.onSubmit().url
  private lazy val completedRoute = "http://localhost:8822/register-an-estate/registration-progress"

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
        view(expectedSections)(request, messages).toString

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
        view(expectedSections)(request, messages).toString

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


    "redirect to the estates progress when submitted" in {

      val mockEstateConnector = mock[EstateConnector]

      val userAnswers = emptyUserAnswers
        .set(AgentARNPage, "SARN123456").success.value
        .set(AgentTelephoneNumberPage, "123456789").success.value
        .set(AgentUKAddressYesNoPage, false).success.value
        .set(AgentInternationalAddressPage, InternationalAddress("Line1", "Line2", None, "Country")).success.value
        .set(AgentNamePage, "Sam Curran Trust").success.value
        .set(AgentInternalReferencePage, "123456789").success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[EstateConnector].toInstance(mockEstateConnector))
          .build()

      when(mockEstateConnector.addAgentDetails(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "Success response")))

      val request = FakeRequest(POST, submitRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual completedRoute

      application.stop()
    }

  }
}
