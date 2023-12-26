package com.pwos.products.db

import scala.collection.concurrent.TrieMap

import cats.effect.IO
import com.pwos.products.domain.{Offer, Price, ProductCode}
import eu.timepit.refined.types.numeric.NonNegInt
import fs2.Stream

final class OfferDaoInMemory extends OfferDao[IO] {

  private val offers: TrieMap[ProductCode, List[Price]] = TrieMap.empty

  override def saveOffer(offer: Offer): IO[Unit] = {
    IO {
      val code           = offer.productCode
      val existingPrices = offers.getOrElse(code, List.empty)
      offers.update(code, offer.price +: existingPrices)
    }
  }

  override def offersCount(productCode: ProductCode): IO[NonNegInt] = {
    IO {
      NonNegInt.unsafeFrom {
        offers.getOrElse(productCode, List.empty).size
      }
    }
  }

  override def offersStream(productCode: ProductCode): Stream[IO, Offer] = {
    Stream.emits[IO, Offer] {
      offers
        .getOrElse(productCode, List.empty)
        .map(Offer(productCode, _))
    }

  }

}
