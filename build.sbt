import com.malliina.sbtutils.SbtUtils

import complete.DefaultParsers.spaceDelimited
import java.nio.file.{Path => JPath}
import GeneratorKeys._

inThisBuild(
  Seq(
    organization := "com.malliina",
    version := "1.0.1",
    scalaVersion := "3.1.1"
  )
)

val deploy = inputKey[Unit]("Deploys the site")

val scalatagsVersion = GeneratorClientPlugin.scalatagsVersion

val frontend = project
  .in(file("frontend"))
  .enablePlugins(GeneratorClientPlugin)
  .settings(
    Compile / npmDependencies ++= Seq(
      "swiper" -> "8.4.2"
    ),
    Compile / npmDevDependencies ++= Seq(
      "autoprefixer" -> "10.4.12",
      "cssnano" -> "4.1.10",
      "css-loader" -> "6.7.1",
      "less" -> "4.1.3",
      "less-loader" -> "11.0.0",
      "mini-css-extract-plugin" -> "2.6.1",
      "postcss" -> "8.4.16",
      "postcss-import" -> "14.1.0",
      "postcss-loader" -> "7.0.1",
      "postcss-preset-env" -> "7.8.2",
      "webpack-merge" -> "5.8.0"
    )
  )

val generator = project
  .in(file("generator"))
  .enablePlugins(GeneratorPlugin)
  .settings(
    clientProject := frontend,
    libraryDependencies ++= SbtUtils.loggingDeps ++ Seq(
      "com.malliina" %% "primitives" % "3.2.0",
      "com.lihaoyi" %% "scalatags" % scalatagsVersion
    ),
    deploy := {
      val args = spaceDelimited("<arg>").parsed
      CommandLine.runProcessSync(
        args.mkString(" "),
        (ThisBuild / baseDirectory).value,
        streams.value.log
      )
    },
    Prod / deploy := deploy.toTask(" netlify deploy --prod").dependsOn(Prod / build).value,
    Dev / deploy := deploy.toTask(" netlify deploy").dependsOn(Dev / build).value
  )

val meny = project
  .in(file("."))
  .aggregate(frontend, generator)
  .settings(
    build := (generator / Dev / build).value
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
