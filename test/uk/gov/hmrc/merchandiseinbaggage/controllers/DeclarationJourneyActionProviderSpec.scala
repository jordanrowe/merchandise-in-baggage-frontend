/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{EssentialAction, Result}
import play.api.test.Helpers._
import uk.gov.hmrc.merchandiseinbaggage.BaseSpec
import uk.gov.hmrc.merchandiseinbaggage.wiremock.MockStrideAuth._
import uk.gov.hmrc.merchandiseinbaggage.wiremock.WireMockSupport

import scala.concurrent.Future

class DeclarationJourneyActionProviderSpec extends BaseSpec {

  "need to be stride authenticated if internal FE flag is set" in new DeclarationJourneyControllerSpec {
    override def fakeApplication(): Application          =
      new GuiceApplicationBuilder()
        .configure(
          Map(
            "microservice.services.auth.port" -> WireMockSupport.port,
            "assistedDigital"                 -> true
          )
        )
        .build()
    val actionProvider: DeclarationJourneyActionProvider = injector.instanceOf[DeclarationJourneyActionProvider]

    givenTheUserIsAuthenticatedAndAuthorised()

    val action: EssentialAction = actionProvider.journeyAction { _ =>
      play.api.mvc.Results.Ok("authenticated")
    }

    val result: Future[Result] = call(action, buildGet("/", aSessionId))
    status(result) mustBe SEE_OTHER
  }

  "need not to be stride authenticated if internal FE flag is not set" in new DeclarationJourneyControllerSpec {
    val actionProvider: DeclarationJourneyActionProvider = injector.instanceOf[DeclarationJourneyActionProvider]

    val ess: EssentialAction = actionProvider.journeyAction { _ =>
      play.api.mvc.Results.Ok("authenticated")
    }

    val result: Future[Result] = call(ess, buildGet("/", aSessionId))
    status(result) mustBe SEE_OTHER
  }
}
