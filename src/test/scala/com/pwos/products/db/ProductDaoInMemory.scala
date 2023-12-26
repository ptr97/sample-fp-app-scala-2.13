package com.pwos.products.db

import scala.collection.concurrent.TrieMap

import cats.effect.IO
import com.pwos.products.domain.{ProductCode, ProductEntity}

final class ProductDaoInMemory extends ProductDao[IO] {

  private val products: TrieMap[ProductCode, ProductEntity] = TrieMap.empty

  override def findProduct(productCode: ProductCode): IO[Option[ProductEntity]] = {
    IO {
      products.get(productCode)
    }
  }

  override def updateProduct(productEntity: ProductEntity): IO[Unit] = {
    IO {
      products.update(productEntity.code, productEntity)
    }
  }

  override def saveProduct(productEntity: ProductEntity): IO[Unit] = {
    IO {
      products.addOne((productEntity.code, productEntity))
    }
  }

}
