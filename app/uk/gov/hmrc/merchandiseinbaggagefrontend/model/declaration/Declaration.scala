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

package uk.gov.hmrc.merchandiseinbaggagefrontend.model.declaration

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID.randomUUID

import play.api.i18n.Messages
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryList, Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.merchandiseinbaggagefrontend.model.core.{GoodsDestination, GoodsVatRate, PurchaseDetailsInput}
import uk.gov.hmrc.merchandiseinbaggagefrontend.model.currencyconversion.Currency

case class SessionId(value: String)

object SessionId {
  implicit val format: Format[SessionId] = implicitly[Format[String]].inmap(SessionId(_), _.value)

  def apply(): SessionId = SessionId(randomUUID().toString)
}

case class PriceOfGoods(amount: BigDecimal, currency: Currency) {
  override val toString = s"${amount.formatted(s"%.${amount.scale}f")}, ${currency.displayName}"

  def toPurchaseDetailsInput = PurchaseDetailsInput(amount, currency.currencyCode)
}

object PriceOfGoods {
  implicit val format: OFormat[PriceOfGoods] = Json.format[PriceOfGoods]
}

case class CategoryQuantityOfGoods(category: String, quantity: String)

object CategoryQuantityOfGoods {
  implicit val format: OFormat[CategoryQuantityOfGoods] = Json.format[CategoryQuantityOfGoods]
}

case class GoodsEntry(categoryQuantityOfGoods: CategoryQuantityOfGoods,
                      maybeGoodsVatRate: Option[GoodsVatRate] = None,
                      maybeCountryOfPurchase: Option[String] = None,
                      maybePriceOfGoods: Option[PriceOfGoods] = None,
                      maybeInvoiceNumber: Option[String] = None,
                      maybeTaxDue: Option[BigDecimal] = None)

object GoodsEntry {
  implicit val format: OFormat[GoodsEntry] = Json.format[GoodsEntry]
}

case class Name(firstName: String, lastName: String) {
  override val toString: String = s"$firstName $lastName"
}

object Name {
  implicit val format: OFormat[Name] = Json.format[Name]
}

case class Address(maybeLine1: Option[String],
                   maybeLine2: Option[String] = None,
                   maybeTown: Option[String] = None,
                   maybeCounty: Option[String] = None,
                   postCode: String) {
  val populatedAddressLines: Seq[String] = Seq(maybeLine1, maybeLine2, maybeTown, maybeCounty).flatten
}

object Address {
  implicit val format: OFormat[Address] = Json.format[Address]

  def apply(line1: String, line2: String, postcode: String): Address = Address(Some(line1), Some(line2), None, None, postcode)
}

case class Eori(value: String) {
  override val toString: String = value
}

object Eori {
  implicit val format: OFormat[Eori] = Json.format[Eori]
}

case class JourneyDetails(placeOfArrival: String, dateOfArrival: LocalDate) {
  val formattedDateOfArrival: String = DateTimeFormatter.ofPattern("dd MMM yyyy").format(dateOfArrival)
}

object JourneyDetails {
  implicit val format: OFormat[JourneyDetails] = Json.format[JourneyDetails]
}

case class DeclarationJourney(sessionId: SessionId,
                              maybeExciseOrRestrictedGoods: Option[Boolean] = None,
                              goodsEntries: Seq[GoodsEntry] = Seq.empty,
                              maybeName: Option[Name] = None,
                              maybeAddress: Option[Address] = None,
                              maybeEori: Option[Eori] = None,
                              maybeJourneyDetails: Option[JourneyDetails] = None,
                              maybeGoodsDestination: Option[GoodsDestination] = None,
                              maybeValueWeightOfGoodsExceedsThreshold: Option[Boolean] = None) {
  def toDeclarationIfComplete: Option[Declaration] =
    for {
      name <- maybeName
      address <- maybeAddress
      eori <- maybeEori
      journeyDetails <- maybeJourneyDetails
    } yield
      Declaration(
        sessionId,
        goodsEntries.map(entry => Goods(entry)),
        name,
        address,
        eori,
        journeyDetails
      )
}

object DeclarationJourney {
  implicit val format: OFormat[DeclarationJourney] = Json.format[DeclarationJourney]

  val id = "sessionId"
}

case class Goods(categoryQuantityOfGoods: CategoryQuantityOfGoods,
                 goodsVatRate: GoodsVatRate,
                 countryOfPurchase: String,
                 priceOfGoods: PriceOfGoods,
                 invoiceNumber: String,
                 taxDue: BigDecimal) {
  def toSummaryList(implicit messages: Messages): SummaryList = {
    val price =
      s"${priceOfGoods.amount}, ${priceOfGoods.currency.displayName})"

    SummaryList(Seq(
      SummaryListRow(
        Key(Text(messages("reviewGoods.list.item"))),
        Value(Text(categoryQuantityOfGoods.category))
      ),
      SummaryListRow(
        Key(Text(messages("reviewGoods.list.quantity"))),
        Value(Text(categoryQuantityOfGoods.quantity))
      ),
      SummaryListRow(
        Key(Text(messages("reviewGoods.list.country"))),
        Value(Text(countryOfPurchase))
      ),
      SummaryListRow(
        Key(Text(messages("reviewGoods.list.price"))),
        Value(Text(price))
      ),
      SummaryListRow(
        Key(Text(messages("reviewGoods.list.invoice"))),
        Value(Text(invoiceNumber))
      )
    ))
  }
}

object Goods {
  implicit val format: OFormat[Goods] = Json.format[Goods]

  def apply(goodsEntry: GoodsEntry): Goods = (
    for {
      goodsVatRate <- goodsEntry.maybeGoodsVatRate
      countryOfPurchase <- goodsEntry.maybeCountryOfPurchase
      priceOfGoods <- goodsEntry.maybePriceOfGoods
      invoiceNumber <- goodsEntry.maybeInvoiceNumber
      taxDue <- goodsEntry.maybeTaxDue
    } yield Goods(goodsEntry.categoryQuantityOfGoods, goodsVatRate, countryOfPurchase, priceOfGoods, invoiceNumber, taxDue)
    ).getOrElse(throw new RuntimeException(s"incomplete goods entry: [$goodsEntry]"))
}

case class Declaration(sessionId: SessionId,
                       goods: Seq[Goods],
                       name: Name,
                       address: Address,
                       eori: Eori,
                       journeyDetails: JourneyDetails)

object Declaration {
  implicit val format: OFormat[Declaration] = Json.format[Declaration]
}