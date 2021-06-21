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

package utils.mappers

import base.SpecBase
import generators.Generators
import models.mappers.AgentDetails
import models.pages.{InternationalAddress, UKAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages._

class AgentDetailsMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  private val agentMapper = injector.instanceOf[AgentDetailsMapper]

  "AgentDetailsMapper" when {

    "user answers is empty" must {

      "not be able to create AgentDetails" in {

        val userAnswers = emptyUserAnswers

        agentMapper(userAnswers).isError mustBe true
      }
    }
    "when user answers is not empty " must {

      "able to create AgentDetails for a UK address" in {

        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentUKAddressPage, UKAddress("Line1", "Line2", None, Some("Newcastle"), "ab1 1ab")).success.value
            .set(AgentTelephoneNumberPage, "+1234567890").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentUKAddressYesNoPage, true).success.value

        agentMapper(userAnswers).get mustBe AgentDetails(
          arn = "SARN123456",
          agentName = "Agency Name",
          agentAddress = UKAddress("Line1", "Line2", None, Some("Newcastle"), "ab1 1ab"),
          agentTelephoneNumber = "+1234567890",
          clientReference = "1234-5678"
        )
      }

      "must able to create AgentDetails for a UK address with only required fields" in {

        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentUKAddressPage, UKAddress("Line1", "Newcastle", None, None, "NE62RT")).success.value
            .set(AgentTelephoneNumberPage, "+1234567890").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentUKAddressYesNoPage, true).success.value

        agentMapper(userAnswers).get mustBe AgentDetails(
          arn = "SARN123456",
          agentName = "Agency Name",
          agentAddress = UKAddress("Line1", "Newcastle", None, None, "NE62RT"),
          agentTelephoneNumber = "+1234567890",
          clientReference = "1234-5678"
        )
      }

      "must able to create AgentDetails for a international address" in {

        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentInternationalAddressPage,InternationalAddress("line1","line2",Some("line3"), "IN")).success.value
            .set(AgentTelephoneNumberPage, "+1234567890").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentUKAddressYesNoPage, false).success.value

        agentMapper(userAnswers).get mustBe AgentDetails(
          arn = "SARN123456",
          agentName = "Agency Name",
          agentAddress = InternationalAddress("line1", "line2", Some("line3"), "IN"),
          agentTelephoneNumber = "+1234567890",
          clientReference = "1234-5678"
        )
      }

      "must able to create AgentDetails for a international address with minimum data" in {

        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentInternationalAddressPage,InternationalAddress("line1","line2",None, "IN")).success.value
            .set(AgentTelephoneNumberPage, "+1234567890").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentUKAddressYesNoPage, false).success.value

        agentMapper(userAnswers).get mustBe AgentDetails(
          arn = "SARN123456",
          agentName = "Agency Name",
          agentAddress = InternationalAddress("line1", "line2", None, "IN"),
          agentTelephoneNumber = "+1234567890",
          clientReference = "1234-5678"
        )
      }

      "must not be able to create AgentDetails when no address available." in {
        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
            .set(AgentInternalReferencePage, "1234-5678").success.value
            .set(AgentUKAddressYesNoPage, true).success.value
        agentMapper(userAnswers).isError mustBe true

      }

      "must not be able to create AgentDetails when no only agent name available." in {
        val userAnswers =
          emptyUserAnswers
            .set(AgentARNPage, "SARN123456").success.value
            .set(AgentNamePage, "Agency Name").success.value
        agentMapper(userAnswers).isError mustBe true

      }
    }


  }

}
