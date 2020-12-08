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

package uk.gov.hmrc.merchandiseinbaggage.model.api

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.merchandiseinbaggage.model.core.YesNo.{No, Yes}
import uk.gov.hmrc.merchandiseinbaggage.model.core.{Port, YesNo}

sealed trait JourneyDetails {
  val port: Port
  val dateOfTravel: LocalDate
  val formattedDateOfArrival: String = DateTimeFormatter.ofPattern("dd MMM yyyy").format(dateOfTravel)
  val travellingByVehicle: YesNo = No
  val maybeRegistrationNumber: Option[String] = None
}

case class JourneyOnFoot(port: Port, dateOfTravel: LocalDate) extends JourneyDetails

case class JourneyInSmallVehicle(port: Port, dateOfTravel: LocalDate, registrationNumber: String) extends JourneyDetails {
  override val travellingByVehicle: YesNo = Yes
  override val maybeRegistrationNumber: Option[String] = Some(registrationNumber)
}

object JourneyDetails {
  implicit val format: OFormat[JourneyDetails] = Json.format[JourneyDetails]
}

object JourneyOnFoot {
  implicit val format: OFormat[JourneyOnFoot] = Json.format[JourneyOnFoot]
}

object JourneyInSmallVehicle {
  implicit val format: OFormat[JourneyInSmallVehicle] = Json.format[JourneyInSmallVehicle]
}
