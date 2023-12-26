import Dependencies.*
import com.typesafe.sbt.packager.SettingsHelper.makeDeploymentSettings
import sbt.Keys.scalacOptions

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.10"

ThisBuild / scalafixDependencies += Plugin.organizeImports
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

addCommandAlias("refresh", "reload; update")
addCommandAlias("compileAll", "compile; Test / compile")

addCommandAlias("organizeImports", "scalafix OrganizeImports")
addCommandAlias("testOrganizeImports", "Test / scalafix OrganizeImports")
addCommandAlias("organizeImportsAll", "scalafix OrganizeImports; Test / scalafix OrganizeImports")

addCommandAlias("format", "organizeImports; scalafmt")
addCommandAlias("testFormat", "testOrganizeImports; Test / scalafmt")
addCommandAlias("formatAll", "organizeImportsAll; scalafmtAll")

lazy val root = (project in file("."))
  .settings(
    name := "product-offer-aggregation",
    Test / fork := true,
    Test / scalacOptions ~= (_.filterNot(_ == "-Wnonunit-statement")),
    libraryDependencies += Plugin.betterFor,
    scalacOptions ++= compileOptions,
    libraryDependencies ++= Seq(
      Lib.catsCore,
      Lib.catsEffect,
      Lib.circeCore,
      Lib.circeExtras,
      Lib.circeGeneric,
      Lib.circeParser,
      Lib.circeRefined,
      Lib.circeLiteral,
      Lib.circeTagged,
      Lib.doobieCore,
      Lib.doobiePostgres,
      Lib.doobieRefined,
      Lib.enumeratum,
      Lib.enumeratumCirce,
      Lib.enumeratumDoobie,
      Lib.fs2,
      Lib.http4sCore,
      Lib.http4sEmber,
      Lib.logbackClassic,
      Lib.pureconfig,
      Lib.refined,
      Lib.scalaLogging,
      Lib.tapir,
      Lib.tapirCirce,
      Lib.tapirEnumeratum,
      Lib.tapirRefined,
      Lib.tapirHttp4s,
      Lib.Tests.scalatest
    ),
    Compile / mainClass := Some("com.pwos.products.Main"),
    deploymentSettings
  )
  .enablePlugins(JavaAppPackaging, UniversalDeployPlugin)

lazy val deploymentSettings =
  makeDeploymentSettings(Universal, Universal / packageBin, "zip") ++ Seq(maintainer := "Piotr Wos")

lazy val compileOptions = Seq(
  "-deprecation",
  "-encoding",
  "utf-8",
  "-explaintypes",
  "-feature",
  "-language:higherKinds",
  "-Wdead-code",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Woctal-literal",
  "-Wunused:explicits",
  "-Wunused:implicits",
  "-Wunused:imports",
  "-Wunused:linted",
  "-Wunused:locals",
  "-Wunused:privates",
  "-Wvalue-discard",
  "-Xlint:adapted-args",
  "-Xlint:constant",
  "-Xlint:delayedinit-select",
  "-Xlint:deprecation",
  "-Xlint:implicit-not-found",
  "-Xlint:implicit-recursion",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:nonlocal-return",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:serial",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Xlint:valpattern",
  "-Xsource:3",
  "-Wnonunit-statement"
)

addCommandAlias("debugImplicits", """set ThisBuild/scalacOptions ++= Seq("-Vtype-diffs", "-Vimplicits")""")

val fatalWarnings            = taskKey[Seq[String]]("Fatal warnings flag")
val disableFatalWarningsFile = new File(".disableFatalWarnings")
val enableFatalWarnings      = taskKey[Unit]("Enable fatal warning")
val disableFatalWarnings     = taskKey[Unit]("Disable fatal warnings")

disableFatalWarnings := {
  disableFatalWarningsFile.createNewFile()
}

enableFatalWarnings := {
  disableFatalWarningsFile.delete()
}

ThisBuild / fatalWarnings := {
  val log = sbt.Keys.streams.value.log
  if (disableFatalWarningsFile.exists()) {
    log.warn("Fatal warnings disabled")
    Seq()
  } else {
    Seq("-Xfatal-warnings")
  }
}
