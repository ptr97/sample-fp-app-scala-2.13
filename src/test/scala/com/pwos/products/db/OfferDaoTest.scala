package com.pwos.products.db

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import com.pwos.products.domain.Offer
import com.pwos.products.helpers.ProductGenerator.generateProduct
import com.pwos.products.helpers.ProductGenerator.unsafePrice
import com.pwos.products.helpers.DoobieTestSpec
import eu.timepit.refined.types.numeric.NonNegInt
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait OfferDaoTest { this: AnyFlatSpec & Matchers =>

  implicit private val ioRuntime: IORuntime = IORuntime.global

  def runAllTests(offerDao: OfferDao[IO], productDao: ProductDao[IO]): Unit = {

    it should "properly save offer, return offers count and stream saved offers" in {
      val product     = generateProduct()
      val productCode = product.code
      val price       = unsafePrice(100)
      val offer       = Offer(productCode, price)

      productDao.saveProduct(product).unsafeRunSync()

      offerDao.saveOffer(offer).unsafeRunSync()
      offerDao.offersCount(productCode).unsafeRunSync() shouldBe NonNegInt(1)
      offerDao.offersStream(productCode).compile.toList.unsafeRunSync() should contain(offer)
    }

  }

}

final class OfferDaoInMemoryTest extends AnyFlatSpec with Matchers with OfferDaoTest {
  val offerDao: OfferDao[IO]     = new OfferDaoInMemory()
  val productDao: ProductDao[IO] = new ProductDaoInMemory()
  runAllTests(offerDao, productDao)
}

final class OfferDaoDoobieTest
    extends AnyFlatSpec
    with Matchers
    with EitherValues
    with DoobieTestSpec
    with OfferDaoTest {
  val offerDao: OfferDao[IO]     = new OfferDaoDoobie(transactor)
  val productDao: ProductDao[IO] = new ProductDaoDoobie(transactor)
  runAllTests(offerDao, productDao)
}
