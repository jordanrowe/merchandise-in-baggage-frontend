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
import uk.gov.hmrc.merchandiseinbaggagefrontend.model.core.GoodsEntry
import uk.gov.hmrc.merchandiseinbaggagefrontend.pagespecs.pages.InvoiceNumberPage
import uk.gov.hmrc.merchandiseinbaggagefrontend.pagespecs.pages.InvoiceNumberPage._

class InvoiceNumberPageSpec extends GoodsEntryPageSpec[String, InvoiceNumberPage] with ScalaFutures {
  override lazy val page: InvoiceNumberPage = wire[InvoiceNumberPage]

  "the invoice number page" should {
    behave like aGoodsEntryPage(path, title, "Invoice123", None)
  }

  override def extractFormDataFrom(goodsEntry: GoodsEntry): Option[String] = goodsEntry.maybeInvoiceNumber
}
