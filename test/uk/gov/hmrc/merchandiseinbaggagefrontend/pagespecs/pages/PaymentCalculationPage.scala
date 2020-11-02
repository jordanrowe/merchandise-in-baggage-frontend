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

import org.openqa.selenium.{By, WebDriver}
import org.scalatestplus.selenium.WebBrowser
import uk.gov.hmrc.merchandiseinbaggagefrontend.model.core.AmountInPence

import scala.collection.JavaConverters._

class PaymentCalculationPage(implicit webDriver: WebDriver) extends BasePage {

  import WebBrowser._

  def clickOnCTA(): String = {
    val button = find(ClassNameQuery("govuk-button")).get
    click on button

    readPath()
  }

  def summaryHeaders: Seq[String] = {
    val tableHead = find(ClassNameQuery("govuk-table__head")).head.underlying
    tableHead.findElements(By.className("govuk-table__header")).asScala.map(_.getText)
  }

  def summaryRow(index: Int): Seq[String] = {
    val tableBody = find(ClassNameQuery("govuk-table__body")).head.underlying
    val tableRows = tableBody.findElements(By.className("govuk-table__row")).asScala
    tableRows(index).findElements(By.className("govuk-table__cell")).asScala.map(_.getText)
  }
}

object PaymentCalculationPage {
  val path: String = "/merchandise-in-baggage/payment-calculation"

  def title(amountInPence: AmountInPence) = s"Payment due on these goods ${amountInPence.formattedInPounds}"
}