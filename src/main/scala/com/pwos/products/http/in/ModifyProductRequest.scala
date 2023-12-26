package com.pwos.products.http.in

import com.pwos.products.domain.ProductState
import com.pwos.products.http.json.Codecs.*
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class ModifyProductRequest(state: ProductState)

object ModifyProductRequest {
  implicit val modifyProductRequestCodec: Codec[ModifyProductRequest] = deriveCodec
}
