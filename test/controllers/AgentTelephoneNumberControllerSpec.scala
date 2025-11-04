/*
 * Copyright 2025 HM Revenue & Customs
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
import config.annotations.EstateRegistration
import forms.AgentTelephoneNumberFormProvider
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import org.scalatestplus.mockito.MockitoSugar
import pages.{AgentNamePage, AgentTelephoneNumberPage}
import play.api.Application
import play.api.inject.bind
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AgentTelephoneNumberView

import scala.concurrent.Future

class AgentTelephoneNumberControllerSpec extends RegistrationSpecBase with MockitoSugar {

  private val formProvider = new AgentTelephoneNumberFormProvider()
  private val form = formProvider()
  private val agencyName = "FirstName LastName"

  private lazy val agentTelephoneNumberRoute = routes.AgentTelephoneNumberController.onPageLoad(NormalMode).url

  "AgentTelephoneNumber Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(AgentNamePage, "FirstName LastName").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, agentTelephoneNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentTelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, agencyName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(AgentNamePage, "FirstName LastName").success.value
        .set(AgentTelephoneNumberPage, "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, agentTelephoneNumberRoute)

      val view = application.injector.instanceOf[AgentTelephoneNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"), NormalMode, agencyName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(AgentNamePage, "FirstName LastName").success.value
        .set(AgentTelephoneNumberPage, "answer").success.value

      val application: Application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[Navigator].qualifiedWith(classOf[EstateRegistration]).toInstance(fakeNavigator))
          .build()

      val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, agentTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", "0191 1111111"))

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(AgentNamePage, "FirstName LastName").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, agentTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AgentTelephoneNumberView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, agencyName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, agentTelephoneNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, agentTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to AgentNamePage when agency name is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, agentTelephoneNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.AgentNameController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to AgentName page when AgentName is not answered" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(AgentTelephoneNumberPage, "answer").success.value

      val application: Application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .build()

      val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, agentTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", "0191 1111111"))

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.AgentNameController.onPageLoad(NormalMode).url

      application.stop()
    }

  }
}
