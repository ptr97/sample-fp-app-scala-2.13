package com.pwos.products.domain

import eu.timepit.refined.types.numeric.NonNegLong

final case class ProductAggregate(
  productCode: ProductCode,
  minimumPrice: Price,
  maximumPrice: Price,
  averagePrice: Price,
  offersCount: NonNegLong,
  state: ProductState
)
