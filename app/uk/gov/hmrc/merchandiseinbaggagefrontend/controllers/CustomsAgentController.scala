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

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.merchandiseinbaggagefrontend.config.AppConfig
import uk.gov.hmrc.merchandiseinbaggagefrontend.forms.CustomsAgentForm.form
import uk.gov.hmrc.merchandiseinbaggagefrontend.model.core.YesNo.Yes
import uk.gov.hmrc.merchandiseinbaggagefrontend.repositories.DeclarationJourneyRepository
import uk.gov.hmrc.merchandiseinbaggagefrontend.views.html.CustomsAgentView

import scala.concurrent.{ExecutionContext, Future}

class CustomsAgentController @Inject()(
                                        override val controllerComponents: MessagesControllerComponents,
                                        actionProvider: DeclarationJourneyActionProvider,
                                        override val repo: DeclarationJourneyRepository,
                                        view: CustomsAgentView,
                                      )(implicit ec: ExecutionContext, appConf: AppConfig) extends DeclarationJourneyUpdateController {

  private val backButtonUrl: Call = routes.PaymentCalculationController.onPageLoad()

  val onPageLoad: Action[AnyContent] = actionProvider.journeyAction { implicit request =>
    Ok(view(request.declarationJourney.maybeIsACustomsAgent.fold(form)(form.fill), backButtonUrl))
  }

  val onSubmit: Action[AnyContent] = actionProvider.journeyAction.async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future successful BadRequest(view(formWithErrors, backButtonUrl)),
        isCustomsAgent =>
          persistAndRedirect(
            request.declarationJourney.copy(maybeIsACustomsAgent = Some(isCustomsAgent)),
            if (isCustomsAgent == Yes) routes.AgentDetailsController.onPageLoad()
            else routes.EoriNumberController.onPageLoad()
          )
      )
  }
}
