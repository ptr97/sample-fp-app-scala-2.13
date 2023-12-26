package com.pwos.products.http.in

import com.pwos.products.domain.ProductState.{Closed, Open}
import io.circe.Json
import io.circe.literal.*
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class ModifyProductRequestTest extends AnyFlatSpec with Matchers with EitherValues {

  it should "parse JSON for closing product action" in {
    val json: Json                    = json"""{"state": "closed"}"""
    val request: ModifyProductRequest = json.as[ModifyProductRequest].value

    request shouldBe ModifyProductRequest(Closed)
  }

  it should "parse JSON for opening product action" in {
    val json: Json                    = json"""{"state": "open"}"""
    val request: ModifyProductRequest = json.as[ModifyProductRequest].value

    request shouldBe ModifyProductRequest(Open)
  }

}
