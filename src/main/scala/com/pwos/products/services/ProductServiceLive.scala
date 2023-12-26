package com.pwos.products.services

import cats.data.EitherT
import cats.data.OptionT
import cats.effect.Concurrent
import cats.syntax.applicative.*
import cats.syntax.either.*
import cats.syntax.functor.*
import com.pwos.products.db.OfferDao
import com.pwos.products.db.ProductDao
import com.pwos.products.domain.Offer
import com.pwos.products.domain.Price
import com.pwos.products.domain.ProductAggregate
import com.pwos.products.domain.ProductCode
import com.pwos.products.domain.ProductEntity
import com.pwos.products.domain.ProductOfferStats
import com.pwos.products.domain.ProductState
import com.pwos.products.domain.error.AddOfferError
import com.pwos.products.domain.error.ModifyProductError
import com.pwos.products.domain.error.ModifyProductError.NotEnoughOffersToCloseAggregation
import com.typesafe.scalalogging.LazyLogging

final class ProductServiceLive[F[_]: Concurrent](productDao: ProductDao[F], offerDao: OfferDao[F])
    extends ProductService[F]
    with LazyLogging {

  override def find(productCode: ProductCode): F[Option[ProductAggregate]] = {
    logger.info(s"Searching for product with code [$productCode]")

    (for {
      product <- OptionT(productDao.findProduct(productCode))
      stats   <- OptionT.liftF(productOfferStats(product.code))
    } yield {
      ProductAggregate(
        productCode  = product.code,
        minimumPrice = stats.minimumPrice,
        maximumPrice = stats.maximumPrice,
        averagePrice = stats.averagePrice,
        offersCount  = stats.offersCount,
        state        = product.state
      )
    }).value
  }

  private def productOfferStats(productCode: ProductCode): F[ProductOfferStats] = {
    offerDao
      .offersStream(productCode)
      .fmap(_.price)
      .fmap(ProductOfferStats.fromPrice)
      .reduceSemigroup
      .compile
      .toList
      .map(_.headOption.getOrElse(ProductOfferStats.emptyStats))
  }

  override def modifyState(
    productCode: ProductCode,
    newProductState: ProductState
  ): F[Either[ModifyProductError, Unit]] = {

    type Err = ModifyProductError

    logger.info(s"Modifying state of product with code [$productCode] to [$newProductState]")

    def modifyAction(productEntity: ProductEntity): EitherT[F, Err, Unit] = {

      val errorOrModifiedEntity: F[Either[Err, ProductEntity]] = newProductState match {
        case ProductState.Open => productEntity.open.asRight[Err].pure[F]
        case ProductState.Closed =>
          offerDao
            .offersCount(productEntity.code)
            .map { offersCount =>
              if (productEntity.canBeClosed(offersCount)) productEntity.close.asRight
              else NotEnoughOffersToCloseAggregation.asLeft
            }
      }

      for {
        modifiedEntity <- EitherT(errorOrModifiedEntity)
        _              <- EitherT.right[Err](productDao.updateProduct(modifiedEntity))
      } yield ()
    }

    (for {
      product <-
        EitherT.fromOptionF(productDao.findProduct(productCode), ifNone = ModifyProductError.ProductDoesNotExist)
      _ = logger.info(s"Found product [$product] to state modification")
      _ <- if (product.state != newProductState) modifyAction(product)
           else EitherT.pure[F, Err](())
    } yield ()).value
  }

  override def addOffer(productCode: ProductCode, price: Price): F[Either[AddOfferError, Unit]] = {
    type Err = AddOfferError

    logger.info(s"Adding offer with price [$price] for product with code [$productCode]")

    (for {
      product <- EitherT.fromOptionF(productDao.findProduct(productCode), ifNone = AddOfferError.ProductDoesNotExist)
      _       <- EitherT.cond[F](product.acceptsOffers, right = (), left = AddOfferError.ProductIsClosed)
      _       <- EitherT.right[Err](offerDao.saveOffer(Offer(productCode, price)))
    } yield ()).value
  }

}
