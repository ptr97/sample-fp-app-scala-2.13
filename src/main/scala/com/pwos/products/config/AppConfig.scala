package com.pwos.products.config

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import pureconfig.ConfigReader
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.semiauto.deriveReader

import AppConfig.DatabaseConfig
import AppConfig.HttpConfig

final case class AppConfig(database: DatabaseConfig, http: HttpConfig)

object AppConfig extends LazyLogging {

  final case class HttpConfig(host: String, port: Int) {
    override def toString: String = s"$host:$port"
  }

  final case class DatabaseConfig(url: String, user: String, password: String, driver: String)

  implicit val databaseConfigReader: ConfigReader[DatabaseConfig] = deriveReader
  implicit val httpConfigReader: ConfigReader[HttpConfig]         = deriveReader
  implicit val appConfigReader: ConfigReader[AppConfig]           = deriveReader

  def load(namespace: String): IO[AppConfig] = {
    ConfigSource.default.at(namespace).load[AppConfig] match {
      case Right(config) => IO.pure(config)
      case Left(failures) =>
        logger.error(s"Failed to load config at [$namespace]. Failures: ${failures.prettyPrint(2)}")
        IO.raiseError(ConfigReaderException(failures))
    }
  }

}
