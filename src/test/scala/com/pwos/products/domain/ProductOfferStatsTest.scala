package com.pwos.products.domain

import com.pwos.products.helpers.ProductGenerator.unsafePrice
import eu.timepit.refined.types.numeric.NonNegLong
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class ProductOfferStatsTest extends AnyFlatSpec with Matchers {

  private val productOfferStats: ProductOfferStats = ProductOfferStats(
    minimumPrice = unsafePrice(10),
    maximumPrice = unsafePrice(100),
    sumOfPrice   = unsafePrice(888),
    offersCount  = NonNegLong(7)
  )

  it should "calculate average price properly" in {
    productOfferStats.averagePrice shouldBe unsafePrice(126.8571)
  }

  it should "calculate average price even if there is no offers" in {
    val withoutOffers: ProductOfferStats = ProductOfferStats.emptyStats
    withoutOffers.averagePrice shouldBe unsafePrice(0)
  }

}
