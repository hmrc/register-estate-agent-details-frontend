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

package utils.print

import models.pages.{InternationalAddress, UKAddress}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import utils.countryOptions.CountryOptions

import javax.inject.Inject

class CheckAnswersFormatters @Inject() (countryOptions: CountryOptions) {

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html =
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }

  private def country(code: String)(implicit messages: Messages): Html =
    HtmlFormat.escape(countryOptions.options().find(_.value.equals(code)).map(_.label).getOrElse(""))

  def ukAddress(address: UKAddress): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        Some(HtmlFormat.escape(address.line2)),
        address.line3.map(HtmlFormat.escape),
        address.line4.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(address.postcode))
      ).flatten

    breakLines(lines)
  }

  def internationalAddress(address: InternationalAddress)(implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        Some(HtmlFormat.escape(address.line2)),
        address.line3.map(HtmlFormat.escape),
        Some(country(address.country))
      ).flatten

    breakLines(lines)
  }

  private def breakLines(lines: Seq[Html]): Html =
    Html(lines.mkString("<br />"))

}
