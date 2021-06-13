import java.nio.file.{Files, StandardCopyOption}

inThisBuild(
  Seq(
    organization := "com.malliina",
    version := "1.0.0",
    scalaVersion := "2.13.6"
  )
)

val Dev = config("dev")
val Prod = config("prod")
val build = taskKey[Unit]("builds app")

val scalatagsVersion = GeneratorClientPlugin.scalatagsVersion

val frontend = project
  .in(file("frontend"))
  .enablePlugins(GeneratorClientPlugin)
  .settings(
    Compile / fullOptJS / build := (Compile / fullOptJS / webpack).value.map { af =>
      val destDir = (ThisBuild / baseDirectory).value / "target" / "site"
      Files.createDirectories(destDir.toPath)
      val dest = (destDir / af.data.name).toPath
      sLog.value.info(s"Write  $dest ${af.metadata}")
      Files.copy(af.data.toPath, dest, StandardCopyOption.REPLACE_EXISTING).toFile
    },
    Compile / fastOptJS / build := (Compile / fastOptJS / webpack).value.map { af =>
      val destDir = (ThisBuild / baseDirectory).value / "target" / "site"
      Files.createDirectories(destDir.toPath)
      val name = af.metadata.get(BundlerFileTypeAttr) match {
        case Some(BundlerFileType.Application) => "app.js"
        case Some(BundlerFileType.Library)     => "library.js"
        case Some(BundlerFileType.Loader)      => "loader.js"
        case _                                 => af.data.name
      }
      val dest = (destDir / name).toPath
      sLog.value.info(
        s"Write $dest from ${af.data.name} ${af.metadata} ${af.metadata.get(BundlerFileTypeAttr)}"
      )
      Files.copy(af.data.toPath, dest, StandardCopyOption.REPLACE_EXISTING).toFile
    },
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % scalatagsVersion
    ),
    watchSources += WatchSource(baseDirectory.value / "src", "*.scala", HiddenFileFilter),
    webpack / version := "4.39.1",
    startWebpackDevServer / version := "3.7.2",
    Compile / fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(),
    Compile / fullOptJS / webpackBundlingMode := BundlingMode.Application,
    webpackEmitSourceMaps := false,
    Compile / npmDependencies ++= Seq(
      "swiper" -> "6.7.0"
    ),
    Compile / npmDevDependencies ++= Seq(
      "autoprefixer" -> "9.6.1",
      "cssnano" -> "4.1.10",
      "css-loader" -> "3.2.0",
      "file-loader" -> "4.2.0",
      "less" -> "3.9.0",
      "less-loader" -> "5.0.0",
      "mini-css-extract-plugin" -> "0.8.0",
      "postcss-import" -> "12.0.1",
      "postcss-loader" -> "3.0.0",
      "postcss-preset-env" -> "6.7.0",
      "style-loader" -> "1.0.0",
      "url-loader" -> "2.1.0",
      "webpack-merge" -> "4.2.1"
    )
  )

val generator = project
  .in(file("generator"))
  .settings(
    libraryDependencies ++= Seq(
      "com.malliina" %% "primitives" % "1.19.0",
      "com.lihaoyi" %% "scalatags" % scalatagsVersion,
      "org.slf4j" % "slf4j-api" % "1.7.30",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "ch.qos.logback" % "logback-core" % "1.2.3"
    ),
    watchSources := watchSources.value ++ Def.taskDyn(frontend / watchSources).value,
    Prod / build := (Compile / run)
      .toTask(" prod")
      .dependsOn(frontend / Compile / fullOptJS / build)
      .value,
    Dev / build := (Compile / run)
      .toTask(" dev")
      .dependsOn(frontend / Compile / fastOptJS / build)
      .value
  )

val meny = project
  .in(file("."))
  .aggregate(frontend, generator)
  .settings(
    build := (generator / Dev / build).value
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
