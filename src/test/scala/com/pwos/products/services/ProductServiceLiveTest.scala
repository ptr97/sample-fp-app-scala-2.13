package com.pwos.products.services

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import com.pwos.products.helpers.ProductGenerator.generateProduct
import com.pwos.products.helpers.ProductGenerator.generateProductCode
import com.pwos.products.helpers.ProductGenerator.unsafePrice
import com.pwos.products.db.{OfferDao, OfferDaoInMemory, ProductDao, ProductDaoInMemory}
import com.pwos.products.domain.{Offer, ProductAggregate, ProductCode, ProductEntity, ProductState}
import com.pwos.products.domain.ProductState.Closed
import com.pwos.products.domain.error.{AddOfferError, ModifyProductError}
import eu.timepit.refined.types.all.NonNegInt
import org.scalatest.EitherValues
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class ProductServiceLiveTest extends AnyFlatSpec with Matchers with OptionValues with EitherValues {

  implicit private val ioRuntime: IORuntime = IORuntime.global

  private val productDao: ProductDao[IO]  = new ProductDaoInMemory()
  private val offerDao: OfferDao[IO]      = new OfferDaoInMemory()
  private val service: ProductService[IO] = new ProductServiceLive[IO](productDao, offerDao)

  it should "find product by product code without any offers" in new TestProductEnvironment {
    val productAggregate: ProductAggregate = service.find(productCode).unsafeRunSync().value

    productAggregate.offersCount shouldBe NonNegInt(0)
  }

  it should "find product by product code with proper offers details" in new TestProductEnvironment {
    addOffer(price = 100)
    addOffer(price = 200)

    val productAggregate: ProductAggregate = service.find(product.code).unsafeRunSync().value

    productAggregate.offersCount shouldBe NonNegInt(2)
    productAggregate.maximumPrice shouldBe unsafePrice(200)
    productAggregate.minimumPrice shouldBe unsafePrice(100)
    productAggregate.averagePrice shouldBe unsafePrice(150)
  }

  it should "allow modify product state to 'closed' if there is enough offers for the product" in new TestProductEnvironment(
    modifications = List(_.copy(minOffersToClose = NonNegInt(1)))
  ) {
    addOffer(100)

    service.modifyState(productCode, Closed).unsafeRunSync().value

    val updated: ProductEntity = productDao.findProduct(productCode).unsafeRunSync().value
    updated.state shouldBe ProductState.Closed
  }

  it should "modify product state to 'open'" in new TestProductEnvironment(
    modifications = List(_.copy(state = ProductState.Closed))
  ) {

    service.modifyState(productCode, ProductState.Open).unsafeRunSync().value

    val updated: ProductEntity = productDao.findProduct(productCode).unsafeRunSync().value
    updated.state shouldBe ProductState.Open
  }

  it should "fail to modify product state to 'closed' if there is not enough offers for the product" in new TestProductEnvironment {
    assume(product.minOffersToClose == NonNegInt(4))

    val error: ModifyProductError = service.modifyState(productCode, ProductState.Closed).unsafeRunSync().left.value
    error shouldBe ModifyProductError.NotEnoughOffersToCloseAggregation

    productDao.findProduct(productCode).unsafeRunSync().value.state shouldBe ProductState.Open
  }

  it should "fail to modify product state if product with provided code does not exist" in new TestProductEnvironment {
    val nonExistingCode: ProductCode = generateProductCode()

    val error: ModifyProductError = service.modifyState(nonExistingCode, ProductState.Closed).unsafeRunSync().left.value
    error shouldBe ModifyProductError.ProductDoesNotExist
  }

  it should "allow to add an offer for the product if product is in 'open' state" in new TestProductEnvironment(
    modifications = List(_.copy(minOffersToClose = NonNegInt(1)))
  ) {
    service.addOffer(productCode, unsafePrice(200)).unsafeRunSync().value

    offerDao.offersCount(productCode).unsafeRunSync() shouldBe NonNegInt(1)
  }

  it should "fail to add an offer for the product if product with provided code does not exist" in new TestProductEnvironment {
    val nonExistingCode: ProductCode = generateProductCode()

    val error: AddOfferError = service.addOffer(nonExistingCode, unsafePrice(100)).unsafeRunSync().left.value

    error shouldBe AddOfferError.ProductDoesNotExist
  }

  it should "fail to add an offer for the product if product is in 'closed' state" in new TestProductEnvironment(
    modifications = List(_.copy(state = ProductState.Closed))
  ) {
    val error: AddOfferError = service.addOffer(productCode, unsafePrice(100)).unsafeRunSync().left.value
    error shouldBe AddOfferError.ProductIsClosed
  }

  abstract private class TestProductEnvironment(modifications: List[ProductEntity => ProductEntity] = List.empty) {
    private val productEntity: ProductEntity = generateProduct()

    val productCode: ProductCode = productEntity.code

    val product: ProductEntity = modifications.foldLeft(productEntity) { (product, modification) =>
      modification(product)
    }

    productDao.saveProduct(product).unsafeRunSync()

    def addOffer(price: Double): Unit = {
      offerDao
        .saveOffer(Offer(productCode, unsafePrice(price)))
        .unsafeRunSync()
    }

  }

}
