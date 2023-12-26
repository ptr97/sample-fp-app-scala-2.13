package com.pwos.products.http.in

import com.pwos.products.domain.Price
import com.pwos.products.http.json.Codecs.*
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class AddOfferRequest(price: Price)

object AddOfferRequest {
  implicit val addOfferRequestCodec: Codec[AddOfferRequest] = deriveCodec
}
