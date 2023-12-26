package com.pwos.products.http.in

import com.pwos.products.helpers.ProductGenerator.unsafePrice
import io.circe.Json
import io.circe.literal.*
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class AddOfferRequestTest extends AnyFlatSpec with Matchers with EitherValues {

  it should "parse JSON with for adding new offer" in {
    val json: Json               = json"""{"price": 300}"""
    val request: AddOfferRequest = json.as[AddOfferRequest].value

    request shouldBe AddOfferRequest(unsafePrice(300))
  }

}
