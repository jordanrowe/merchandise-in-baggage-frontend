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

package uk.gov.hmrc.merchandiseinbaggage.controllers

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.merchandiseinbaggage.config.AppConfig
import uk.gov.hmrc.merchandiseinbaggage.views.html.SessionExpiredView

import scala.concurrent.ExecutionContext

class SessionExpiredController @Inject()(override val controllerComponents: MessagesControllerComponents,
                                         view: SessionExpiredView
                                        )
                                        (implicit ec: ExecutionContext, appConfig: AppConfig)
  extends DeclarationJourneyController {

  override val onPageLoad: Action[AnyContent] = Action { implicit request =>
    removeSession(request)(Ok(view()))
  }

  def removeSession(implicit request: Request[_]): Result => Result = result =>
    result.removingFromSession(SessionKeys.sessionId)
}