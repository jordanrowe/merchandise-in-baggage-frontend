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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import reactivemongo.api.commands.UpdateWriteResult
import uk.gov.hmrc.merchandiseinbaggage.config.AppConfig
import uk.gov.hmrc.merchandiseinbaggage.connectors.MibConnector
import uk.gov.hmrc.merchandiseinbaggage.model.api.Declaration
import uk.gov.hmrc.merchandiseinbaggage.model.core.DeclarationJourney
import uk.gov.hmrc.merchandiseinbaggage.model.api.DeclarationType.{Export, Import}
import uk.gov.hmrc.merchandiseinbaggage.repositories.DeclarationJourneyRepository
import uk.gov.hmrc.merchandiseinbaggage.views.html.DeclarationConfirmationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationConfirmationController @Inject()(
  override val controllerComponents: MessagesControllerComponents,
  actionProvider: DeclarationJourneyActionProvider,
  view: DeclarationConfirmationView,
  connector: MibConnector,
  val repo: DeclarationJourneyRepository,
)(implicit ec: ExecutionContext, appConf: AppConfig)
    extends DeclarationJourneyController {

  val onPageLoad: Action[AnyContent] = actionProvider.journeyAction.async { implicit request =>
    val declarationId = request.declarationJourney.declarationId
    connector.findDeclaration(declarationId).map {
      case Some(declaration) if showConfirmation(declaration) =>
        clearAnswers()
        Ok(view(declaration))
      case _ => actionProvider.invalidRequest(s"declaration not found for id:${declarationId.value}")
    }
  }

  val makeAnotherDeclaration: Action[AnyContent] = actionProvider.journeyAction.async { implicit request =>
    import request.declarationJourney._
    repo.upsert(DeclarationJourney(sessionId, declarationType)) map { _ =>
      Redirect(routes.GoodsDestinationController.onPageLoad())
    }
  }

  private def clearAnswers()(implicit request: DeclarationJourneyRequest[AnyContent]): Future[UpdateWriteResult] = {
    import request.declarationJourney._
    repo.upsert(DeclarationJourney(sessionId, declarationType, declarationId = declarationId))
  }

  private def showConfirmation(declaration: Declaration): Boolean = {

    def paymentNotRequired =
      declaration.maybeTotalCalculationResult.map(_.totalTaxDue.value).getOrElse(0L) == 0L

    def paymentSuccess = declaration.paymentSuccess.contains(true)

    declaration.declarationType == Export || (declaration.declarationType == Import && (paymentNotRequired || paymentSuccess))
  }
}
