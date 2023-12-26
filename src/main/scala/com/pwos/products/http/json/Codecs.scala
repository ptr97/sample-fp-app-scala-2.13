package com.pwos.products.http.json

import cats.syntax.either.*
import com.pwos.products.domain.Price
import com.pwos.products.domain.ProductAggregate
import com.pwos.products.domain.ProductCode
import com.pwos.products.domain.ProductState
import com.pwos.products.domain.error.AddOfferError
import com.pwos.products.domain.error.DomainError
import com.pwos.products.domain.error.ModifyProductError
import eu.timepit.refined.types.numeric.NonNegBigDecimal
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Codec
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.Encoder
import io.circe.Json
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec
import io.circe.generic.semiauto.deriveCodec
import io.circe.refined.*
import io.circe.syntax.*

object Codecs {

  implicit val nonEmptyStringDecoder: Decoder[NonEmptyString] = refinedDecoder
  implicit val nonEmptyStringEncoder: Encoder[NonEmptyString] = refinedEncoder

  implicit val nonNegBigDecimalDecoder: Decoder[NonNegBigDecimal] = refinedDecoder
  implicit val nonNegBigDecimalEncoder: Encoder[NonNegBigDecimal] = refinedEncoder

  implicit val priceCodec: Codec[Price]                       = deriveUnwrappedCodec
  implicit val productCodeCodec: Codec[ProductCode]           = deriveUnwrappedCodec
  implicit val productAggregateCodec: Codec[ProductAggregate] = deriveCodec

  implicit val productStateEncoder: Encoder[ProductState] = Encoder.instance[ProductState](_.value.asJson)

  implicit val productStateDecoder: Decoder[ProductState] =
    Decoder.instance[ProductState] { cursor =>
      cursor.value
        .as[String]
        .flatMap { plain =>
          ProductState.withValueOpt(plain).toRight(DecodingFailure(s"Unknown product state [$plain]", cursor.history))
        }
    }

  private def domainErrorEncoder[T <: DomainError]: Encoder[T] = Encoder.instance[T] { error =>
    Json.obj("code" -> error.code.asJson, "message" -> error.message.asJson)
  }

  implicit val addOfferErrorEncoder: Encoder[AddOfferError]           = domainErrorEncoder[AddOfferError]
  implicit val modifyProductErrorEncoder: Encoder[ModifyProductError] = domainErrorEncoder[ModifyProductError]

  implicit val addOfferErrorDecoder: Decoder[AddOfferError] = Decoder.instance[AddOfferError] { cursor =>
    cursor.as[String].flatMap {
      case AddOfferError.ProductDoesNotExist.code => AddOfferError.ProductDoesNotExist.asRight
      case AddOfferError.ProductIsClosed.code     => AddOfferError.ProductIsClosed.asRight
      case other => DecodingFailure(s"Unknown code [$other] for AddOfferError", cursor.history).asLeft
    }
  }

  implicit val modifyProductErrorDecoder: Decoder[ModifyProductError] = Decoder.instance[ModifyProductError] { cursor =>
    cursor.as[String].flatMap {
      case ModifyProductError.ProductDoesNotExist.code => ModifyProductError.ProductDoesNotExist.asRight
      case ModifyProductError.NotEnoughOffersToCloseAggregation.code =>
        ModifyProductError.NotEnoughOffersToCloseAggregation.asRight
      case other => DecodingFailure(s"Unknown code [$other] for ModifyProductError", cursor.history).asLeft
    }
  }

}
