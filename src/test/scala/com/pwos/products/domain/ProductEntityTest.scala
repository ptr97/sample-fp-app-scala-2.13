package com.pwos.products.domain

import com.pwos.products.helpers.ProductGenerator
import eu.timepit.refined.types.numeric.NonNegInt
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class ProductEntityTest extends AnyFlatSpec with Matchers {

  private val openProduct: ProductEntity   = ProductGenerator.generateProduct()
  private val closedProduct: ProductEntity = openProduct.close

  it should "accept offer when product is in 'open' state" in {
    openProduct.acceptsOffers shouldBe true
  }

  it should "not accept offer when product is in 'closed' state" in {
    closedProduct.acceptsOffers shouldBe false
  }

  it should "allow to close the product when offers count is bigger than minimum offers to close product" in {
    openProduct.canBeClosed(NonNegInt(2)) shouldBe false
    openProduct.canBeClosed(NonNegInt(3)) shouldBe false
    openProduct.canBeClosed(NonNegInt(4)) shouldBe true
  }

}
