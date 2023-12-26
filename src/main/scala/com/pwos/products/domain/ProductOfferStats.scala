package com.pwos.products.domain

import java.math.MathContext

import cats.Semigroup
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.types.numeric.NonNegBigDecimal
import eu.timepit.refined.types.numeric.NonNegLong

final case class ProductOfferStats(
  minimumPrice: Price,
  maximumPrice: Price,
  sumOfPrice: Price,
  offersCount: NonNegLong
) {

  def averagePrice: Price = {
    if (offersCount == NonNegLong.unsafeFrom(0)) Price(NonNegBigDecimal.unsafeFrom(BigDecimal(0)))
    else
      Price(
        NonNegBigDecimal
          .unsafeFrom(
            (sumOfPrice.value / BigDecimal.long2bigDecimal(offersCount.value))
              .round(MathContext.DECIMAL32)
          )
      )
  }

}

object ProductOfferStats {

  def fromPrice(price: Price): ProductOfferStats =
    ProductOfferStats(
      minimumPrice = price,
      maximumPrice = price,
      sumOfPrice   = price,
      offersCount  = NonNegLong.unsafeFrom(1)
    )

  val emptyStats: ProductOfferStats = ProductOfferStats(Price.zero, Price.zero, Price.zero, NonNegLong.unsafeFrom(0))

  implicit val productOfferStatsMonoid: Semigroup[ProductOfferStats] = (x: ProductOfferStats, y: ProductOfferStats) => {
    ProductOfferStats(
      minimumPrice = x.minimumPrice.min(y.minimumPrice),
      maximumPrice = x.maximumPrice.max(y.maximumPrice),
      sumOfPrice   = x.sumOfPrice + y.sumOfPrice,
      offersCount  = NonNegLong.unsafeFrom(x.offersCount + y.offersCount)
    )
  }

}
