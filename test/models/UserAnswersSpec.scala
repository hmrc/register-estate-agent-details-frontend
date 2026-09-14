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

package models

import base.RegistrationSpecBase
import _root_.pages.AgentNamePage
import play.api.libs.json.{JsPath, JsResultException, Json}
import queries.Settable

class UserAnswersSpec extends RegistrationSpecBase {

  private object NestedPage extends Settable[String] {
    override def path: JsPath = JsPath \ "agent" \ "name"
  }

  "UserAnswers" when {

    "setting an answer" must {

      "store the value at the page's path" in {

        val result = UserAnswers("id").set(AgentNamePage, "Agency Ltd").success.value

        result.get(AgentNamePage).value mustBe "Agency Ltd"
      }

      "fail when the path cannot be written to" in {

        val answers = UserAnswers("id", Json.obj("agent" -> "not-an-object"))

        val result = answers.set(NestedPage, "Agency Ltd")

        result.failure.exception mustBe a[JsResultException]
      }
    }

    "removing an answer" must {

      "drop the value at the page's path" in {

        val answers = UserAnswers("id").set(AgentNamePage, "Agency Ltd").success.value

        answers.remove(AgentNamePage).success.value.get(AgentNamePage) mustBe None
      }
    }

    "reading an answer that was never set" must {

      "return nothing" in {
        UserAnswers("id").get(AgentNamePage) mustBe None
      }
    }
  }

}
