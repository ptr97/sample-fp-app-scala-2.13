package com.pwos.products

import cats.effect.IO
import com.pwos.products
import com.pwos.products.db.OfferDao
import com.pwos.products.db.OfferDaoDoobie
import com.pwos.products.db.ProductDao
import com.pwos.products.db.ProductDaoDoobie
import com.pwos.products.http.ProductRoutes
import com.pwos.products.services.ProductService
import com.pwos.products.services.ProductServiceLive
import doobie.util.transactor.Transactor

final case class ProductsModule(routes: ProductRoutes[IO])

object ProductsModule {

  def make(transactor: Transactor[IO]): ProductsModule = {
    val productDao: ProductDao[IO]         = new ProductDaoDoobie(transactor)
    val offerDao: OfferDao[IO]             = new OfferDaoDoobie(transactor)
    val productService: ProductService[IO] = new ProductServiceLive[IO](productDao, offerDao)
    val productRoutes                      = new ProductRoutes(productService)

    products.ProductsModule(productRoutes)
  }

}
