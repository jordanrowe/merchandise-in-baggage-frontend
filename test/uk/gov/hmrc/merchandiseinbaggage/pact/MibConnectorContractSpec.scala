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

package uk.gov.hmrc.merchandiseinbaggage.pact

import com.itv.scalapact.ScalaPactForger._
import com.itv.scalapact.circe13._
import com.itv.scalapact.model.{ScalaPactDescription, ScalaPactOptions}
import org.json4s.DefaultFormats
import play.api.libs.json.Json
import play.api.http.Status
import uk.gov.hmrc.merchandiseinbaggage.connectors.MibConnector
import uk.gov.hmrc.merchandiseinbaggage.model.api.GoodsDestinations.GreatBritain
import uk.gov.hmrc.merchandiseinbaggage.model.api._
import uk.gov.hmrc.merchandiseinbaggage.model.api.calculation._
import uk.gov.hmrc.merchandiseinbaggage.model.api.checkeori.CheckResponse
import uk.gov.hmrc.merchandiseinbaggage.utils.DataModelEnriched._
import uk.gov.hmrc.merchandiseinbaggage.{BaseSpecWithApplication, CoreTestData}

import java.io.File
import java.time.LocalDate

class MibConnectorContractSpec extends BaseSpecWithApplication with CoreTestData {

  implicit val formats: DefaultFormats.type = DefaultFormats

  val CONSUMER: String           = "merchandise-in-baggage-frontend"
  val PROVIDER: String           = "merchandise-in-baggage"
  val mibConnector: MibConnector = injector.instanceOf[MibConnector]

  val findByDeclaration: Declaration = declaration.copy(mibReference = mibReference, eori = eori)
  val today: LocalDate               = LocalDate.now
  val period: ConversionRatePeriod   = ConversionRatePeriod(today, today, "EUR", BigDecimal(1.1))

  val pact: ScalaPactDescription = forgePact
    .between(CONSUMER)
    .and(PROVIDER)
    .addInteraction(
      interaction
        .description("Persisting a declaration")
        .provided("persistDeclarationTest")
        .uponReceiving(
          POST,
          "/declare-commercial-goods/declarations",
          None,
          Map("Content-Type" -> "application/json"),
          Json.toJson(declaration).toString
        )
        .willRespondWith(Status.CREATED, s""""\\"${declaration.declarationId.value}\\""""")
    )
    .addInteraction(
      interaction
        .description("Amending a declaration")
        .provided("amendDeclarationTest")
        .uponReceiving(
          PUT,
          "/declare-commercial-goods/declarations",
          None,
          Map("Content-Type" -> "application/json"),
          Json.toJson(declarationWithAmendment).toString
        )
        .willRespondWith(Status.OK, s""""\\"${declarationWithAmendment.declarationId.value}\\""""")
    )
    .addInteraction(
      interaction
        .description("find a declaration")
        .provided(s"id1234XXX${Json.toJson(declaration.copy(declarationId = DeclarationId("56789"))).toString}")
        .uponReceiving(GET, "/declare-commercial-goods/declarations/56789")
        .willRespondWith(Status.OK, Json.toJson(declaration.copy(declarationId = DeclarationId("56789"))).toString)
    )
    .addInteraction(
      interaction
        .description("calculates total payments for amendment")
        .provided(s"id789")
        .uponReceiving(
          POST,
          "/declare-commercial-goods/amend-calculations",
          None,
          Map("Content-Type" -> "application/json"),
          Json
            .toJson(
              CalculationAmendRequest(
                Some(declarationWithAmendment.amendments.head),
                Some(declarationWithAmendment.goodsDestination),
                DeclarationId("id789")
              )
            )
            .toString
        )
        .willRespondWith(
          Status.OK,
          Json
            .toJson(
              CalculationResponse(
                CalculationResults(
                  Seq(
                    CalculationResult(
                      declarationWithAmendment.declarationGoods.goods.head.asInstanceOf[ImportGoods],
                      AmountInPence(9090),
                      AmountInPence(0),
                      AmountInPence(1818),
                      Some(period)
                    )
                  )
                ),
                WithinThreshold
              )
            )
            .toString
        )
    )
    .addInteraction(
      interaction
        .description("calculate payments")
        .provided(s"calculatePaymentsTest")
        .uponReceiving(
          POST,
          "/declare-commercial-goods/calculations",
          None,
          Map("Content-Type" -> "application/json"),
          Json.toJson(List(aGoods).map(_.calculationRequest(GreatBritain))).toString
        )
        .willRespondWith(
          Status.OK,
          Json
            .toJson(
              CalculationResponse(
                CalculationResults(
                  Seq(
                    CalculationResult(aGoods, AmountInPence(18181), AmountInPence(0), AmountInPence(3636), Some(period))
                  )
                ),
                WithinThreshold
              )
            )
            .toString
        )
    )
    .addInteraction(
      interaction
        .description("check EoriNumber")
        .provided(s"checkEoriNumberTest")
        .uponReceiving(GET, "/declare-commercial-goods/validate/eori/GB123")
        .willRespondWith(Status.OK, Json.toJson(CheckResponse("GB123", valid = true, None)).toString)
    )
    .addInteraction(
      interaction
        .description("findBy mib ref and eori")
        .provided(s"findByTestXXX${Json.toJson(findByDeclaration).toString}")
        .uponReceiving(
          GET,
          s"/declare-commercial-goods/declarations?mibReference=${mibReference.value}&eori=${eori.value}"
        )
        .willRespondWith(Status.OK, Json.toJson(findByDeclaration).toString)
    )

  implicit val options: ScalaPactOptions = ScalaPactOptions(writePactFiles = true, "./pact")

  private val pactDir = new File("./pact")
  override def beforeAll(): Unit = {
    super.beforeAll()
    if (pactDir.exists()) pactDir.listFiles().map(_.delete())
  }

  "generate contract files in ./pact" in {
    if (pactDir.exists()) pactDir.listFiles().map(_.delete())
    pact.writePactsToFile

    pactDir.exists() mustBe true
  }
}
