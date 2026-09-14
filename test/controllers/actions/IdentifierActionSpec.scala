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

package controllers.actions

import base.SpecBase
import config.FrontendAppConfig
import models.requests.IdentifierRequest
import play.api.mvc.{AnyContent, BodyParsers, DefaultActionBuilder, Result, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AuthConnector

import scala.concurrent.{ExecutionContext, Future}

class IdentifierActionSpec extends SpecBase {

  implicit private val ec: ExecutionContext = injector.instanceOf[ExecutionContext]

  private val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  private val parser: BodyParsers.Default  = injector.instanceOf[BodyParsers.Default]
  private val mockAuthConnector            = mock[AuthConnector]

  private lazy val estatesAuth = new EstatesAuthorisedFunctions(mockAuthConnector, appConfig)

  private lazy val identifierAction = new IdentifierAction(parser, estatesAuth, appConfig)

  private def block: IdentifierRequest[AnyContent] => Future[Result] =
    request => Future.successful(Results.Ok(request.agentReferenceNumber))

  "IdentifierAction" when {

    "the request has already been identified" must {

      "hand the request straight to the block" in {

        val identified = IdentifierRequest(fakeRequest, "internalId", "SARN1234567")

        val result = identifierAction.invokeBlock(identified, block)

        status(result)          mustBe OK
        contentAsString(result) mustBe "SARN1234567"
      }
    }

    "the request has not been identified" must {

      "redirect to login, carrying the continue URL" in {

        val result = identifierAction.invokeBlock(fakeRequest, block)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).value must startWith(appConfig.loginUrl)
        redirectLocation(result).value must include("continue")
      }
    }

    "composing an action" must {

      "wrap it in an affinity group check" in {

        val action = injector.instanceOf[DefaultActionBuilder].apply(_ => Results.Ok)

        identifierAction.composeAction(action) mustBe a[AffinityGroupIdentifierAction[_]]
      }
    }
  }

}
