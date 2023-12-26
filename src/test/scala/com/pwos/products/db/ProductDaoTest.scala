package com.pwos.products.db

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import com.pwos.products.domain.ProductState.{Closed, Open}
import com.pwos.products.helpers.DoobieTestSpec
import com.pwos.products.helpers.ProductGenerator.generateProduct
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait ProductDaoTest { this: AnyFlatSpec & Matchers & OptionValues =>

  implicit private val ioRuntime: IORuntime = IORuntime.global

  def runAllTests(productDao: ProductDao[IO]): Unit = {

    it should "save, find and update the product" in {
      val product = generateProduct()
      assume(product.state == Open)

      productDao.saveProduct(product).unsafeRunSync()
      productDao.findProduct(product.code).unsafeRunSync() shouldBe defined
      productDao.updateProduct(product.close).unsafeRunSync()
      productDao.findProduct(product.code).unsafeRunSync().value.state shouldBe Closed
    }

  }

}

final class ProductDaoInMemoryTest extends AnyFlatSpec with Matchers with OptionValues with ProductDaoTest {
  val productDao: ProductDao[IO] = new ProductDaoInMemory()
  runAllTests(productDao)
}

final class ProductDaoDoobieTest
    extends AnyFlatSpec
    with Matchers
    with EitherValues
    with OptionValues
    with DoobieTestSpec
    with ProductDaoTest {
  val productDao: ProductDao[IO] = new ProductDaoDoobie(transactor)
  runAllTests(productDao)
}
