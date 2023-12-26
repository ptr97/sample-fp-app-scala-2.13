package com.pwos.products.db

import cats.effect.IO
import com.pwos.products.domain.ProductCode
import com.pwos.products.domain.ProductEntity
import doobie.implicits.*
import doobie.refined.implicits.*
import doobie.util.transactor.Transactor

final class ProductDaoDoobie(xa: Transactor[IO]) extends ProductDao[IO] {

  override def findProduct(productCode: ProductCode): IO[Option[ProductEntity]] = {
    sql"""
      SELECT
        products.code,
        products.name,
        products.state,
        products.min_offers_to_close
      FROM products
      WHERE products.code = $productCode
    """
      .query[ProductEntity]
      .option
      .transact(xa)
  }

  override def updateProduct(productEntity: ProductEntity): IO[Unit] = {
    sql"""
      UPDATE products
      SET
        name = ${productEntity.name},
        state = ${productEntity.state}::PRODUCT_STATE,
        min_offers_to_close = ${productEntity.minOffersToClose}
      WHERE code = ${productEntity.code}
    """.update.run
      .transact(xa)
      .void
  }

  override def saveProduct(productEntity: ProductEntity): IO[Unit] = {
    sql"""
      INSERT INTO products (code, name, state, min_offers_to_close)
      VALUES (
        ${productEntity.code}, 
        ${productEntity.name}, 
        ${productEntity.state}::PRODUCT_STATE, 
        ${productEntity.minOffersToClose}
      )
    """.update.run
      .transact(xa)
      .void
  }

}
