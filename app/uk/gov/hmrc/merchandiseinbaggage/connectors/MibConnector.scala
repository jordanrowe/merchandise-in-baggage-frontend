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

package uk.gov.hmrc.merchandiseinbaggage.connectors

import cats.data.EitherT

import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.http.Status
import uk.gov.hmrc.http.HttpReads.Implicits.{readFromJson, readRaw}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.merchandiseinbaggage.config.AppConfig
import uk.gov.hmrc.merchandiseinbaggage.model.api.calculation.{CalculationAmendRequest, CalculationRequest, CalculationResponse}
import uk.gov.hmrc.merchandiseinbaggage.model.api.checkeori.CheckResponse
import uk.gov.hmrc.merchandiseinbaggage.model.api.{Declaration, DeclarationId, Eori, MibReference}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MibConnector @Inject() (appConfig: AppConfig, httpClient: HttpClient)(implicit
  ec: ExecutionContext
) extends Logging {

  private val baseUrl = appConfig.merchandiseInBaggageUrl

  def persistDeclaration(declaration: Declaration)(implicit hc: HeaderCarrier): Future[DeclarationId] =
    httpClient.POST[Declaration, DeclarationId](s"$baseUrl${appConfig.mibDeclarationsUrl}", declaration)

  def amendDeclaration(declaration: Declaration)(implicit hc: HeaderCarrier): Future[DeclarationId] =
    httpClient.PUT[Declaration, DeclarationId](s"$baseUrl${appConfig.mibDeclarationsUrl}", declaration)

  def findDeclaration(declarationId: DeclarationId)(implicit hc: HeaderCarrier): Future[Option[Declaration]] =
    httpClient.GET[HttpResponse](s"$baseUrl${appConfig.mibDeclarationsUrl}/${declarationId.value}").map { response =>
      response.status match {
        case Status.OK => response.json.asOpt[Declaration]
        case other     =>
          logger.warn(s"unexpected status for findDeclaration, status:$other")
          None
      }
    }

  def findBy(mibReference: MibReference, eori: Eori)(implicit
    hc: HeaderCarrier
  ): EitherT[Future, String, Option[Declaration]] =
    EitherT(
      httpClient
        .GET[HttpResponse](
          s"$baseUrl${appConfig.mibDeclarationsUrl}?mibReference=${mibReference.value}&eori=${eori.value}"
        )
        .map { response =>
          response.status match {
            case Status.OK        => Right(response.json.asOpt[Declaration])
            case Status.NOT_FOUND => Right(None)
            case other            =>
              logger.warn(
                s"unexpected status for findBy for mibReference:${mibReference.value}, and eori:${eori.value}, status:$other"
              )
              Left(s"unexpected status for findBy, status:$other")
          }
        }
    )

  def calculatePayments(calculationRequests: Seq[CalculationRequest])(implicit
    hc: HeaderCarrier
  ): Future[CalculationResponse] =
    httpClient
      .POST[Seq[CalculationRequest], CalculationResponse](
        s"$baseUrl${appConfig.mibCalculationsUrl}",
        calculationRequests
      )

  def calculatePaymentsAmendPlusExisting(
    amendRequest: CalculationAmendRequest
  )(implicit hc: HeaderCarrier): Future[CalculationResponse] =
    httpClient
      .POST[CalculationAmendRequest, CalculationResponse](
        s"$baseUrl${appConfig.mibAmendsPlusExistingCalculationsUrl}",
        amendRequest
      )

  def checkEoriNumber(eori: String)(implicit hc: HeaderCarrier): Future[CheckResponse] =
    httpClient.GET[CheckResponse](s"$baseUrl${appConfig.mibCheckEoriUrl}$eori")
}
