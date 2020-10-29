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

package utils.mappers

import models.UserAnswers
import models.mappers.AgentDetails
import models.pages.{Address, InternationalAddress, UKAddress}
import pages._
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Session

class AgentDetailsMapper {

  private val logger: Logger = Logger(getClass)

  def apply(answers: UserAnswers)(implicit hc: HeaderCarrier): Option[AgentDetails] = {
    val readFromUserAnswers: Reads[AgentDetails] =
      (
          AgentARNPage.path.read[String] and
          AgentNamePage.path.read[String] and
          readAddress and
          AgentTelephoneNumberPage.path.read[String] and
          AgentInternalReferencePage.path.read[String]
        ) (AgentDetails.apply _)

    answers.data.validate[AgentDetails](readFromUserAnswers) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error(s"[Session ID: ${Session.id(hc)}] Failed to rehydrate AgentDetails from UserAnswers due to $errors")
        None
    }
  }

  private def readAddress: Reads[Address] = {
    AgentUKAddressYesNoPage.path.read[Boolean].flatMap[Address] {
      case true => AgentUKAddressPage.path.read[UKAddress].widen[Address]
      case false => AgentInternationalAddressPage.path.read[InternationalAddress].widen[Address]
    }
  }

}
