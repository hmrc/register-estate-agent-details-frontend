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

package utils.mappers

import models.UserAnswers
import models.mappers.AgentDetails
import models.pages.{Address, InternationalAddress, UKAddress}
import pages._
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsResult, Reads}

class AgentDetailsMapper extends Logging {

  def apply(answers: UserAnswers): JsResult[AgentDetails] = {
    val readFromUserAnswers: Reads[AgentDetails] = (
      AgentARNPage.path.read[String] and
        AgentNamePage.path.read[String] and
        readAddress and
        AgentTelephoneNumberPage.path.read[String] and
        AgentInternalReferencePage.path.read[String]
      )(AgentDetails.apply _)

    answers.data.validate[AgentDetails](readFromUserAnswers)
  }

  private def readAddress: Reads[Address] = {
    AgentUKAddressYesNoPage.path.read[Boolean].flatMap[Address] {
      case true => AgentUKAddressPage.path.read[UKAddress].widen[Address]
      case false => AgentInternationalAddressPage.path.read[InternationalAddress].widen[Address]
    }
  }

}
