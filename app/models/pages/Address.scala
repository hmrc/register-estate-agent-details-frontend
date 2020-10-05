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

package models.pages

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Reads, Writes, _}

sealed trait Address

case class UKAddress(
                      line1: String,
                      line2: String,
                      line3: Option[String] = None,
                      line4: Option[String] = None,
                      postcode: String
                    ) extends Address

object UKAddress {

  implicit val reads: Reads[UKAddress] =
    ((__ \ 'line1).read[String] and
      (__ \ 'line2).read[String] and
      (__ \ 'line3).readNullable[String] and
      (__ \ 'line4).readNullable[String] and
      (__ \ 'postCode).read[String]).apply(UKAddress.apply _)

  implicit val writes: Writes[UKAddress] =
    ((__ \ 'line1).write[String] and
      (__ \ 'line2).write[String] and
      (__ \ 'line3).writeNullable[String] and
      (__ \ 'line4).writeNullable[String] and
      (__ \ 'postCode).write[String] and
      (__ \ 'country).write[String]
      ).apply(address => (
      address.line1,
      address.line2,
      address.line3,
      address.line4,
      address.postcode,
      "GB"
    ))

  implicit val format = Format[UKAddress](reads, writes)
}

final case class InternationalAddress(
                                       line1: String,
                                       line2: String,
                                       line3: Option[String] = None,
                                       country: String
                                     ) extends Address

object InternationalAddress {
  implicit val format = Json.format[InternationalAddress]
}


object Address {
  implicit val reads: Reads[Address] =
    __.read[UKAddress](UKAddress.reads).widen[Address] orElse
      __.read[InternationalAddress](InternationalAddress.format).widen[Address]

  implicit val writes: Writes[Address] = Writes {
    case a:UKAddress => Json.toJson(a)(UKAddress.writes)
    case a:InternationalAddress => Json.toJson(a)(InternationalAddress.format)
  }
}
