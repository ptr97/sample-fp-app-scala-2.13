package com.pwos.products.config

import AppConfig.DatabaseConfig
import AppConfig.HttpConfig
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource

final class AppConfigTest extends AnyFlatSpec with Matchers with EitherValues {

  it should "parse application config" in {
    val appConfig: AppConfig = ConfigSource
      .resources("test-application.conf")
      .at("products")
      .load[AppConfig]
      .value

    appConfig shouldBe AppConfig(
      database = DatabaseConfig(
        url      = "jdbc:postgresql:test_products",
        user     = "postgres",
        password = "just-password",
        driver   = "org.postgresql.Driver"
      ),
      http = HttpConfig(host = "localhost", port = 9999)
    )
  }

}
