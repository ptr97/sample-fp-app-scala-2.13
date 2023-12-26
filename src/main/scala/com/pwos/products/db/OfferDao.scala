package com.pwos.products.db

import com.pwos.products.domain.Offer
import com.pwos.products.domain.ProductCode
import eu.timepit.refined.types.numeric.NonNegInt
import fs2.Stream

trait OfferDao[F[_]] {

  def saveOffer(offer: Offer): F[Unit]

  def offersCount(productCode: ProductCode): F[NonNegInt]

  def offersStream(productCode: ProductCode): Stream[F, Offer]

}
