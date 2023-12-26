package com.pwos.products.domain

import com.pwos.products.helpers.ProductGenerator.unsafePrice
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class PriceTest extends AnyFlatSpec with Matchers {

  private val first: Price  = unsafePrice(10.35)
  private val second: Price = unsafePrice(15.25)

  it should "properly add two prices" in {
    first + second shouldBe unsafePrice(25.60)
  }

  it should "find minimum of two prices" in {
    first.min(second) shouldBe first
  }

  it should "find maximum of two prices" in {
    first.max(second) shouldBe second
  }

}
