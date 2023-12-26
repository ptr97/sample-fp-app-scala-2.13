package com.pwos.products.helpers

import java.util.UUID

import com.pwos.products.domain.{Price, ProductCode, ProductEntity}
import com.pwos.products.domain.ProductState.Open
import eu.timepit.refined.types.all.NonNegBigDecimal
import eu.timepit.refined.types.string.NonEmptyString

object ProductGenerator {

  def generateProduct(): ProductEntity = {
    val generatedCode = generateProductCode()

    ProductEntity(code = generatedCode, name = s"Product ${generatedCode.value}", state = Open)
  }

  def unsafePrice(value: Double): Price = Price(NonNegBigDecimal.unsafeFrom(BigDecimal.double2bigDecimal(value)))

  def generateProductCode(): ProductCode = {
    val generatedCode = UUID.randomUUID()
    ProductCode(NonEmptyString.unsafeFrom(s"product-$generatedCode"))
  }

}
