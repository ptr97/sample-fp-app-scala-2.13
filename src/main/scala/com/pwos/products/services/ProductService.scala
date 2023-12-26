package com.pwos.products.services

import com.pwos.products.domain.Price
import com.pwos.products.domain.ProductAggregate
import com.pwos.products.domain.ProductCode
import com.pwos.products.domain.ProductState
import com.pwos.products.domain.error.AddOfferError
import com.pwos.products.domain.error.ModifyProductError

trait ProductService[F[_]] {

  def find(productCode: ProductCode): F[Option[ProductAggregate]]

  def modifyState(productCode: ProductCode, newProductState: ProductState): F[Either[ModifyProductError, Unit]]

  def addOffer(productCode: ProductCode, price: Price): F[Either[AddOfferError, Unit]]

}

object ProductService {

  def foo(
    productService1: String,
    productService2: String,
    productService3: String,
    productService4: String,
    productService5: String
  ): String =
    ""

}
