package com.pwos.products.db

import com.pwos.products.domain.ProductCode
import com.pwos.products.domain.ProductEntity

trait ProductDao[F[_]] {

  def findProduct(productCode: ProductCode): F[Option[ProductEntity]]

  def updateProduct(productEntity: ProductEntity): F[Unit]

  def saveProduct(productEntity: ProductEntity): F[Unit]

}
