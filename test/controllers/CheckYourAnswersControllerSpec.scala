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

package controllers

import base.RegistrationSpecBase
import connector.EstateConnector
import models.mappers.AgentDetails
import models.pages.UKAddress
import org.mockito.ArgumentMatchers.any
import org.scalatest.concurrent.ScalaFutures
import org.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsError, JsSuccess}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import utils.mappers.AgentDetailsMapper
import utils.print.AgentDetailsPrinter
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends RegistrationSpecBase with MockitoSugar with ScalaFutures {

  private lazy val submitRoute: String = controllers.routes.CheckYourAnswersController.onSubmit.url
  private lazy val completedRoute = "http://localhost:8822/register-an-estate/registration-progress"

  "Check Your Answers Controller" must {

    "return OK and the correct view for a GET" in {

      val mockPrintHelper: AgentDetailsPrinter = mock[AgentDetailsPrinter]
      val fakeAnswerSection: AnswerSection = AnswerSection(None, Nil, None)
      when(mockPrintHelper.apply(any())(any())).thenReturn(fakeAnswerSection)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AgentDetailsPrinter].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(fakeAnswerSection))(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }


    "redirect to the estates progress when submitted" in {

      val mockMapper = mock[AgentDetailsMapper]
      val mockEstateConnector = mock[EstateConnector]

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AgentDetailsMapper].toInstance(mockMapper))
        .overrides(bind[EstateConnector].toInstance(mockEstateConnector))
        .build()

      when(mockMapper.apply(any())).thenReturn(JsSuccess(AgentDetails("arn", "name", UKAddress("Line 1", "Line 2", None, None, "AB1 1AB"), "tel", "red")))
      when(mockEstateConnector.addAgentDetails(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "Success response")))

      val request = FakeRequest(POST, submitRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual completedRoute

      application.stop()
    }

    "return internal server error template when mapper fails" in {

      val mockMapper = mock[AgentDetailsMapper]

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AgentDetailsMapper].toInstance(mockMapper))
        .build()

      when(mockMapper.apply(any())).thenReturn(JsError())

      val request = FakeRequest(POST, submitRoute)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      contentAsString(result) mustEqual
        errorHandler.internalServerErrorTemplate(request).toString

      application.stop()
    }

  }
}
