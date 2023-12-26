package com.pwos.products.http

import com.pwos.products.domain.ProductAggregate
import com.pwos.products.domain.ProductCode
import com.pwos.products.domain.error.AddOfferError
import com.pwos.products.domain.error.ModifyProductError
import com.pwos.products.http.in.AddOfferRequest
import com.pwos.products.http.in.ModifyProductRequest
import com.pwos.products.http.json.Codecs.*
import com.pwos.products.services.ProductService
import eu.timepit.refined.types.string.NonEmptyString
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.*
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

final class ProductRoutes[F[_]](service: ProductService[F]) {

  implicit val productCodeTapirCodec: Codec[String, ProductCode, TextPlain] =
    Codec.parsedString(s => ProductCode(NonEmptyString.unsafeFrom(s)))

  implicit val modifyProductErrorSchema: Schema[ModifyProductError] = Schema.derived[ModifyProductError]
  implicit val addOfferErrorSchema: Schema[AddOfferError]           = Schema.derived[AddOfferError]

  import ProductRoutes.Products

  private val findProduct: ServerEndpoint[Any, F] =
    endpoint.get
      .in(Products)
      .in(path[ProductCode])
      .out(jsonBody[ProductAggregate])
      .serverLogicOption(service.find)

  private val modifyState: ServerEndpoint[Any, F] =
    endpoint.put
      .in(Products)
      .in(path[ProductCode])
      .in(jsonBody[ModifyProductRequest])
      .errorOut(jsonBody[ModifyProductError])
      .out(jsonBody[Unit])
      .serverLogic {
        case (productCode, request) =>
          service.modifyState(productCode, request.state)
      }

  private val addOffer: ServerEndpoint[Any, F] =
    endpoint.post
      .in(Products)
      .in(path[ProductCode])
      .in(jsonBody[AddOfferRequest])
      .errorOut(jsonBody[AddOfferError])
      .out(jsonBody[Unit])
      .serverLogic {
        case (productCode, request) =>
          service.addOffer(productCode, request.price)
      }

  val all: List[ServerEndpoint[Any, F]] = List(findProduct, modifyState, addOffer)

}

object ProductRoutes {

  private val Products: String = "products"

}
