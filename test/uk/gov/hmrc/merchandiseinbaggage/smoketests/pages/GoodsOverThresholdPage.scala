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

package uk.gov.hmrc.merchandiseinbaggage.smoketests.pages
import org.scalatest.{Assertion, Suite}
import uk.gov.hmrc.merchandiseinbaggage.smoketests.BaseUiSpec
import uk.gov.hmrc.merchandiseinbaggage.smoketests.pages.GoodsOverThresholdPage.{path, title}

object GoodsOverThresholdPage {
  def path = s"/declare-commercial-goods/goods-over-threshold"
  val title = "The total value of your goods is over"
}

trait GoodsOverThresholdPage extends BaseUiSpec { this: Suite =>

  def goToGoodsOverThresholdPage(): Assertion = {
    goto(path)
    pageTitle must startWith(title)
  }
}
