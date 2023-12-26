package com.pwos.products

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import com.pwos.products.config.AppConfig
import com.pwos.products.config.AppConfig.DatabaseConfig
import com.pwos.products.config.AppConfig.HttpConfig
import com.pwos.products.exceptions.AppInitializationException
import com.typesafe.scalalogging.LazyLogging
import doobie.util.transactor.Transactor
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Main extends IOApp.Simple with LazyLogging {

  private val AppName: String = "products"

  override def run: IO[Unit] = {
    val resource: Resource[IO, Server] = for {
      config <- Resource.eval(AppConfig.load(AppName))
      transactor = createTransactor(config.database)
      module     = ProductsModule.make(transactor)
      server <- serverResource(config.http, module)
      _ = logger.info(s"Server started at ${config.http}. Waiting for requests...")
    } yield server

    resource.use(_ => IO.never)
  }

  private def serverResource(httpConfig: HttpConfig, module: ProductsModule): Resource[IO, Server] = {
    val httpRoutes: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(module.routes.all)
    val HttpConfig(host, port)     = httpConfig

    for {
      host <- Resource.eval(IO.fromOption(Host.fromString(host))(AppInitializationException(s"Invalid host [$host]")))
      port <- Resource.eval(IO.fromOption(Port.fromInt(port))(AppInitializationException(s"Invalid port [$port]")))
      server <- EmberServerBuilder
                  .default[IO]
                  .withHost(host)
                  .withPort(port)
                  .withHttpApp(httpRoutes.orNotFound)
                  .build
    } yield server
  }

  private def createTransactor(config: DatabaseConfig): Transactor[IO] = {
    Transactor.fromDriverManager[IO](config.driver, config.url, config.user, config.password)
  }

}
