import sbt.*

object Dependencies {

  object Ver {
    val catsCore         = "2.9.0"
    val catsEffect       = "3.5.1"
    val circe            = "0.14.3"
    val circeTagged      = "0.10.0"
    val doobie           = "1.0.0-RC2"
    val enumeratum       = "1.7.2"
    val enumeratumDoobie = "1.7.3"
    val fs2              = "3.7.0"
    val http4s           = "0.23.18"
    val logbackClassic   = "1.4.7"
    val pureconfig       = "0.17.4"
    val refined          = "0.10.3"
    val scalaLogging     = "3.9.5"
    val scalatest        = "3.2.15"
    val tapir            = "1.4.0"
  }

  object Lib {
    val catsCore         = "org.typelevel"               %% "cats-core"              % Ver.catsCore
    val catsEffect       = "org.typelevel"               %% "cats-effect"            % Ver.catsEffect
    val circeCore        = "io.circe"                    %% "circe-core"             % Ver.circe
    val circeExtras      = "io.circe"                    %% "circe-generic-extras"   % Ver.circe
    val circeGeneric     = "io.circe"                    %% "circe-generic"          % Ver.circe
    val circeParser      = "io.circe"                    %% "circe-parser"           % Ver.circe
    val circeRefined     = "io.circe"                    %% "circe-refined"          % Ver.circe
    val circeLiteral     = "io.circe"                    %% "circe-literal"          % Ver.circe
    val circeTagged      = "org.latestbit"               %% "circe-tagged-adt-codec" % Ver.circeTagged
    val doobieCore       = "org.tpolecat"                %% "doobie-core"            % Ver.doobie
    val doobiePostgres   = "org.tpolecat"                %% "doobie-postgres"        % Ver.doobie
    val doobieRefined    = "org.tpolecat"                %% "doobie-refined"         % Ver.doobie
    val enumeratum       = "com.beachape"                %% "enumeratum"             % Ver.enumeratum
    val enumeratumCirce  = "com.beachape"                %% "enumeratum-circe"       % Ver.enumeratum
    val enumeratumDoobie = "com.beachape"                %% "enumeratum-doobie"      % Ver.enumeratumDoobie
    val fs2              = "co.fs2"                      %% "fs2-core"               % Ver.fs2
    val http4sCore       = "org.http4s"                  %% "http4s-core"            % Ver.http4s
    val http4sEmber      = "org.http4s"                  %% "http4s-ember-server"    % Ver.http4s
    val logbackClassic   = "ch.qos.logback"               % "logback-classic"        % Ver.logbackClassic
    val pureconfig       = "com.github.pureconfig"       %% "pureconfig"             % Ver.pureconfig
    val refined          = "eu.timepit"                  %% "refined"                % Ver.refined
    val scalaLogging     = "com.typesafe.scala-logging"  %% "scala-logging"          % Ver.scalaLogging
    val scalatest        = "org.scalatest"               %% "scalatest"              % Ver.scalatest
    val tapir            = "com.softwaremill.sttp.tapir" %% "tapir-core"             % Ver.tapir
    val tapirCirce       = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"       % Ver.tapir
    val tapirEnumeratum  = "com.softwaremill.sttp.tapir" %% "tapir-enumeratum"       % Ver.tapir
    val tapirRefined     = "com.softwaremill.sttp.tapir" %% "tapir-refined"          % Ver.tapir
    val tapirHttp4s      = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"    % Ver.tapir

    object Tests {
      val scalatest = Lib.scalatest % Test
    }

  }

  object Plugin {
    val organizeImports = "com.github.liancheng" %% "organize-imports" % "0.6.0"
    val betterFor       = compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  }

}
