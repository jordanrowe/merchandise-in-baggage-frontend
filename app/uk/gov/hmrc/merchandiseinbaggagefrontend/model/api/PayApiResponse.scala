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

package uk.gov.hmrc.merchandiseinbaggagefrontend.model.api

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.merchandiseinbaggagefrontend.model.core.URL
import uk.gov.hmrc.merchandiseinbaggagefrontend.utils.ValueClassFormat

case class JourneyId(value: String)
object JourneyId {
  implicit val format: Format[JourneyId] = ValueClassFormat.format(value => JourneyId.apply(value))(_.value)
}

case class PayApiResponse(journeyId: JourneyId, nextUrl: URL)

object PayApiResponse {
  implicit val format: Format[PayApiResponse] = Json.format[PayApiResponse]
}

