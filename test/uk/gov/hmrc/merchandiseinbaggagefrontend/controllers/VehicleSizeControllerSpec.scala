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

package uk.gov.hmrc.merchandiseinbaggagefrontend.controllers

import play.api.test.Helpers._
import uk.gov.hmrc.merchandiseinbaggagefrontend.model.core.YesNo._
import uk.gov.hmrc.merchandiseinbaggagefrontend.views.html.VehicleSizeView

import scala.concurrent.ExecutionContext.Implicits.global

class VehicleSizeControllerSpec extends DeclarationJourneyControllerSpec {

  private lazy val controller =
    new VehicleSizeController(
      controllerComponents, actionBuilder, declarationJourneyRepository, injector.instanceOf[VehicleSizeView])

  "onPageLoad" must {
    val url = routes.VehicleSizeController.onPageLoad().url
    val request = buildGet(url, sessionId)

    behave like anEndpointRequiringASessionIdAndLinkedDeclarationJourneyToLoad(controller, url)

    "return OK and render the view" when {

      "a declaration has been started" in {
        givenADeclarationJourneyIsPersisted(startedDeclarationJourney)

        val result = controller.onPageLoad()(request)

        status(result) mustEqual OK
      }

      "a declaration has been started and a value saved" in {
        givenADeclarationJourneyIsPersisted(startedDeclarationJourney
          .copy(maybeTravellingBySmallVehicle = Some(Yes)))

        val result = controller.onPageLoad()(request)

        status(result) mustEqual OK
      }
    }
  }

  "onSubmit" must {
    val url = routes.VehicleSizeController.onSubmit().url
    val postRequest = buildPost(url, sessionId)

    behave like anEndpointRequiringASessionIdAndLinkedDeclarationJourneyToUpdate(controller, url)

    "Redirect to /cannot-use-service" when {
      "a declaration is started and No is submitted" in {
        givenADeclarationJourneyIsPersisted(startedDeclarationJourney)

        val request = postRequest.withFormUrlEncodedBody(("value", "No"))
        val result = controller.onSubmit()(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get mustEqual routes.CannotUseServiceController.onPageLoad().toString

        startedDeclarationJourney.maybeTravellingBySmallVehicle mustBe None
        declarationJourneyRepository.findBySessionId(sessionId).futureValue.get.maybeTravellingBySmallVehicle mustBe Some(No)
      }
    }

    "Redirect to /vehicle-registration-number" when {
      "a declaration is started and Yes is submitted" in {
        givenADeclarationJourneyIsPersisted(startedDeclarationJourney)

        val request = postRequest.withFormUrlEncodedBody(("value", "Yes"))
        val result = controller.onSubmit()(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get mustEqual routes.VehicleRegistrationNumberController.onPageLoad().toString

        startedDeclarationJourney.maybeTravellingBySmallVehicle mustBe None
        declarationJourneyRepository.findBySessionId(sessionId).futureValue.get.maybeTravellingBySmallVehicle mustBe Some(Yes)
      }
    }

    "return BAD_REQUEST and errors" when {
      "no selection is made" in {
        givenADeclarationJourneyIsPersisted(startedDeclarationJourney)

        val result = controller.onSubmit()(postRequest)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) must include("Select one of the options below")
      }
    }
  }
}