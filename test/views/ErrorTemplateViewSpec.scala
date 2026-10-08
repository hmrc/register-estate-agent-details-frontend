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

package views

import views.behaviours.ViewBehaviours
import views.html.ErrorTemplate

class ErrorTemplateViewSpec extends ViewBehaviours {

  "ErrorTemplate" must {

    val view      = viewFor[ErrorTemplate]()
    val applyView = view.apply("Title", "Heading", "Message")(using fakeRequest, messages)

    "show the heading and message" in {
      val doc = asDocument(applyView)
      doc.select("h1").text mustBe "Heading"
      doc.select("p.govuk-body").text must include("Message")
    }

    behave like pageRenderedViaRenderAndF(
      applyView,
      view.render(pageTitle = "Title", heading = "Heading", message = "Message", request = fakeRequest, messages = messages),
      view.ref.f("Title", "Heading", "Message")(fakeRequest, messages)
    )
  }
}