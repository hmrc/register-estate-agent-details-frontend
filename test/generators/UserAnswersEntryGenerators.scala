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

package generators

import models.pages._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryAgentUKAddressYesNoUserAnswersEntry: Arbitrary[(AgentUKAddressYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentUKAddressYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentUKAddressUserAnswersEntry: Arbitrary[(AgentUKAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentUKAddressPage.type]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentNameUserAnswersEntry: Arbitrary[(AgentNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentInternationalAddressUserAnswersEntry: Arbitrary[(AgentInternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentInternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentTelephoneNumberUserAnswersEntry: Arbitrary[(AgentTelephoneNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentTelephoneNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentInternalReferenceUserAnswersEntry: Arbitrary[(AgentInternalReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentInternalReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
}
