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

package uk.gov.hmrc.merchandiseinbaggagefrontend.pagespecs.pages

import org.openqa.selenium.WebDriver

class StartExportPage(implicit webDriver: WebDriver) extends PageWithCTA {
  override val ctaName: String = "startNow"
}

object StartExportPage {
  val path = "/merchandise-in-baggage/start-export"
  val title: String = "Declare commercial goods you’re taking out of the UK in accompanied baggage or small vehicles"
}
