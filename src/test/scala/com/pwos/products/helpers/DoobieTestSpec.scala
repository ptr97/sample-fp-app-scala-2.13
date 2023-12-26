package com.pwos.products.helpers

import cats.effect.IO
import com.pwos.products.config.AppConfig
import doobie.util.transactor.Transactor
import org.scalatest.EitherValues
import org.scalatest.Suite
import pureconfig.ConfigSource

trait DoobieTestSpec { this: Suite & EitherValues =>

  private val config: AppConfig.DatabaseConfig = ConfigSource
    .resources("test-application.conf")
    .at("products")
    .load[AppConfig]
    .value
    .database

  val transactor: Transactor[IO] =
    Transactor.fromDriverManager[IO](config.driver, config.url, config.user, config.password)

}
