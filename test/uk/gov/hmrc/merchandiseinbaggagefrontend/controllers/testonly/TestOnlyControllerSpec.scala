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

package uk.gov.hmrc.merchandiseinbaggagefrontend.controllers.testonly

import play.api.libs.json.Json.{prettyPrint, toJson}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.merchandiseinbaggagefrontend._
import uk.gov.hmrc.merchandiseinbaggagefrontend.controllers.DeclarationJourneyControllerSpec

class TestOnlyControllerSpec extends DeclarationJourneyControllerSpec{
  private lazy val controller = app.injector.instanceOf[TestOnlyController]

  override def beforeEach(): Unit = declarationJourneyRepository.deleteAll().futureValue

  "displayDeclarationJourneyPage" should {
    "render the page with a json representation of a sample declaration journey" in {
      val onPageLoadUrl: String = routes.TestOnlyController.displayDeclarationJourneyPage().url
      val request = buildGet(onPageLoadUrl)

      val eventualResponse = controller.displayDeclarationJourneyPage(request)
      val content = contentAsString(eventualResponse)

      status(eventualResponse) mustBe OK

      content must include("Create a test declaration journey")
      content must include("Declaration journey json")
      content must include("TerrysEori")
    }
  }

  "submitDeclarationJourneyPage" should {
    "persist the declaration journey and redirect the user to the start page" in {
      def maybePersistedDeclarationJourney = declarationJourneyRepository.findBySessionId(completedDeclarationJourney.sessionId).futureValue

      maybePersistedDeclarationJourney mustBe None

      val request = FakeRequest(POST, routes.TestOnlyController.submitDeclarationJourneyPage().url).withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
        .withFormUrlEncodedBody(("declarationJourney", prettyPrint(toJson(completedDeclarationJourney))))

      val eventualResponse = controller.submitDeclarationJourneyPage(request)

      status(eventualResponse) mustBe SEE_OTHER
      header("Location", eventualResponse) mustBe Some(controllers.routes.StartController.onStartImport().url)

      maybePersistedDeclarationJourney mustBe Some(completedDeclarationJourney)
    }
  }
}