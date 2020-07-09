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

package connectors

import base.RegistrationSpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import connector.EstateConnector
import generators.Generators
import models.mappers.AgentDetails
import models.pages.UKAddress
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global

class EstateConnectorSpec extends RegistrationSpecBase with Generators with WireMockHelper with ScalaFutures
  with Inside with BeforeAndAfterAll with BeforeAndAfterEach with IntegrationPatience {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "estate connector" when {

    "add agent details" must {

      def addAgentDetailsUrl = "/estates/agent-details"

      val agentDetails = AgentDetails(
        arn = "SARN123456",
        agentName = "Agency Name",
        agentAddress = UKAddress("Line1", "Line2", None, Some("Newcastle"), "ab1 1ab"),
        agentTelephoneNumber = "+1234567890",
        clientReference = "1234-5678"
      )

      "Return OK when the request is successful" in {

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstateConnector]

        server.stubFor(
          post(urlEqualTo(addAgentDetailsUrl))
            .willReturn(ok)
        )

        val result = connector.addAgentDetails(agentDetails)

        result.futureValue.status mustBe OK

        application.stop()
      }

      "return Bad Request when the request is unsuccessful" in {

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.estates.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[EstateConnector]

        server.stubFor(
          post(urlEqualTo(addAgentDetailsUrl))
            .willReturn(badRequest)
        )

        val result = connector.addAgentDetails(agentDetails)

        result.map(response => response.status mustBe BAD_REQUEST)

        application.stop()
      }

    }
  }
}
