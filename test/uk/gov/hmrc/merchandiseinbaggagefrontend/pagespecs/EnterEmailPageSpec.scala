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

package uk.gov.hmrc.merchandiseinbaggagefrontend.pagespecs

import com.softwaremill.macwire.wire
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.merchandiseinbaggagefrontend.model.core.{DeclarationJourney, Email}
import uk.gov.hmrc.merchandiseinbaggagefrontend.pagespecs.pages.EnterEmailPage._
import uk.gov.hmrc.merchandiseinbaggagefrontend.pagespecs.pages.{EnterEmailPage, JourneyDetailsPage, TravellerDetailsPage}

class EnterEmailPageSpec extends DeclarationDataCapturePageSpec[Email, EnterEmailPage] with ScalaFutures {
  override lazy val page: EnterEmailPage = wire[EnterEmailPage]

  "the enter page" should {
    behave like aPageWhichRequiresADeclarationJourney(path)
    behave like aPageWhichRenders(path, givenAnImportJourneyIsStarted(), title)
    behave like aPageWhichDisplaysPreviouslyEnteredAnswers(path)
    behave like aDataCapturePageWithSimpleRouting(path, givenAnImportJourneyIsStarted(), Seq(Email("test@test.com", "test@test.com")), JourneyDetailsPage.path)
    behave like aPageWithABackButton(path, givenAnAgentJourney(), TravellerDetailsPage.path)
  }

  override def extractFormDataFrom(declarationJourney: DeclarationJourney): Option[Email] =
    declarationJourney.maybeEmailAddress
}