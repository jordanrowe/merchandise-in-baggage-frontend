/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.merchandiseinbaggage.controllers

import play.api.test.Helpers._
import uk.gov.hmrc.merchandiseinbaggage.model.core.{DeclarationJourney, GoodsEntries}
import uk.gov.hmrc.merchandiseinbaggage.views.html.GoodsOriginView

import scala.concurrent.ExecutionContext.Implicits.global

class GoodsOriginControllerSpec extends DeclarationJourneyControllerSpec {

  private val view = app.injector.instanceOf[GoodsOriginView]

  def controller(declarationJourney: DeclarationJourney) =
    new GoodsOriginController(controllerComponents, stubProvider(declarationJourney), stubRepo(declarationJourney), view)

  val journey = startedImportToGreatBritainJourney.copy(goodsEntries = GoodsEntries(completedImportGoods.copy(maybePurchaseDetails = None)))

  "onPageLoad" should {
    s"return 200 with radio buttons" in {
      val request = buildGet(routes.GoodsOriginController.onPageLoad(1).url, aSessionId)
      val eventualResult = controller(journey).onPageLoad(1)(request)
      val result = contentAsString(eventualResult)

      status(eventualResult) mustBe 200
      result must include(messageApi(s"goodsOrigin.title"))
      result must include(messageApi(s"goodsOrigin.heading"))
    }
  }

  "onSubmit" should {
    s"redirect to /purchase-details/1 after successful form submit with Yes" in {
      val request = buildPost(routes.GoodsOriginController.onSubmit(1).url, aSessionId)
        .withFormUrlEncodedBody("value" -> "Yes")

      val eventualResult = controller(journey).onSubmit(1)(request)

      status(eventualResult) mustBe 303
      redirectLocation(eventualResult) mustBe Some(routes.PurchaseDetailsController.onPageLoad(1).url)
    }
  }

  s"return 400 with any form errors" in {
    val request = buildPost(routes.GoodsOriginController.onSubmit(1).url, aSessionId)
      .withFormUrlEncodedBody("value" -> "in valid")

    val eventualResult = controller(journey).onSubmit(1)(request)
    val result = contentAsString(eventualResult)

    status(eventualResult) mustBe 400
    result must include(messageApi("error.summary.title"))
    result must include(messageApi(s"goodsOrigin.title"))
    result must include(messageApi(s"goodsOrigin.heading"))
  }
}