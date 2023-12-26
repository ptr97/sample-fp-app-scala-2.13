package com.pwos.products.db

import cats.effect.IO
import com.pwos.products.domain.Offer
import com.pwos.products.domain.ProductCode
import doobie.implicits.*
import doobie.refined.implicits.*
import doobie.util.transactor.Transactor
import eu.timepit.refined.types.numeric.NonNegInt
import fs2.Stream

final class OfferDaoDoobie(xa: Transactor[IO]) extends OfferDao[IO] {

  override def saveOffer(offer: Offer): IO[Unit] = {
    sql"""
      INSERT INTO product_offers (product_code, price)
      VALUES (${offer.productCode}, ${offer.price})
    """.update.run
      .transact(xa)
      .void
  }

  override def offersCount(productCode: ProductCode): IO[NonNegInt] =
    sql"""
      SELECT 
        count(*)
      FROM product_offers
      WHERE product_offers.product_code = $productCode
    """
      .query[Int]
      .option
      .transact(xa)
      .map {
        _.flatMap(NonNegInt.from(_).toOption)
          .getOrElse(NonNegInt(0))
      }

  override def offersStream(productCode: ProductCode): Stream[IO, Offer] = {
    sql"""
      SELECT
        product_offers.product_code,
        product_offers.price
      FROM product_offers
      WHERE product_offers.product_code = $productCode
    """
      .query[Offer]
      .stream
      .transact(xa)
  }

}
