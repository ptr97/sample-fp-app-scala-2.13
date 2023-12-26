package com.pwos.products.domain

import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.types.numeric.NonNegInt

final case class ProductEntity(
  code: ProductCode,
  name: String,
  state: ProductState,
  minOffersToClose: NonNegInt = NonNegInt(4)
) {

  def acceptsOffers: Boolean = state == ProductState.Open

  def canBeClosed(offersCount: NonNegInt): Boolean = offersCount >= minOffersToClose

  def close: ProductEntity = copy(state = ProductState.Closed)

  def open: ProductEntity = copy(state = ProductState.Open)

}
