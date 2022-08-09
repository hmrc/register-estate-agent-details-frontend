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

package models.mappers

import models.pages.Address
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class AgentDetails(arn: String,
                        agentName: String,
                        agentAddress: Address,
                        agentTelephoneNumber: String,
                        clientReference: String)

object AgentDetails {
  implicit val agentDetailsFormat: Format[AgentDetails] = Json.format[AgentDetails]

  implicit val reads: Reads[AgentDetails] =
    ((__ \ 'arn).read[String] and
      (__ \ 'agentName).read[String] and
      (__ \ 'agentAddress).read[Address] and
      (__ \ 'agentTelephoneNumber).read[String] and
      (__ \ 'clientReference).read[String]).tupled.map{

      case (arn, name, address, phoneNumber, clientRef) =>
        AgentDetails(arn, name, address, phoneNumber, clientRef)
    }

  implicit val writes: Writes[AgentDetails] =
    ((__ \ 'arn).write[String] and
      (__ \ 'agentName).write[String] and
      (__ \ 'agentAddress).write[Address] and
      (__ \ 'agentTelephoneNumber).write[String] and
      (__ \ "clientReference").write[String]
      ).apply(unlift(AgentDetails.unapply))

}
