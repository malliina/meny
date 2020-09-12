import java.nio.file.{Files, StandardCopyOption}

inThisBuild(
  Seq(
    organization := "com.malliina",
    version := "1.0.0",
    scalaVersion := "2.13.3"
  )
)

val Dev = config("dev")
val Prod = config("prod")
val build = taskKey[Unit]("builds app")

val frontend = project
  .in(file("frontend"))
  .enablePlugins(GeneratorClientPlugin)
  .settings(
    build in (Compile, fullOptJS) := webpack.in(Compile, fullOptJS).value.map { af =>
      val destDir = (baseDirectory in ThisBuild).value / "target" / "site"
      Files.createDirectories(destDir.toPath)
      val dest = (destDir / af.data.name).toPath
      sLog.value.info(s"Write  $dest ${af.metadata}")
      Files.copy(af.data.toPath, dest, StandardCopyOption.REPLACE_EXISTING).toFile
    },
    build in (Compile, fastOptJS) := webpack.in(Compile, fastOptJS).value.map { af =>
      val destDir = (baseDirectory in ThisBuild).value / "target" / "site"
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
      "com.lihaoyi" %%% "scalatags" % "0.9.1"
    ),
    watchSources += WatchSource(baseDirectory.value / "src", "*.scala", HiddenFileFilter),
    version in webpack := "4.39.1",
    version in startWebpackDevServer := "3.7.2",
    webpackBundlingMode in (Compile, fastOptJS) := BundlingMode.LibraryOnly(),
    webpackBundlingMode in (Compile, fullOptJS) := BundlingMode.Application,
    webpackEmitSourceMaps := false,
    npmDependencies in Compile ++= Seq(
      "swiper" -> "6.2.0"
    ),
    npmDevDependencies in Compile ++= Seq(
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
      "com.malliina" %% "primitives" % "1.17.0",
      "com.lihaoyi" %% "scalatags" % "0.9.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "ch.qos.logback" % "logback-core" % "1.2.3"
    ),
    watchSources := watchSources.value ++ Def.taskDyn(watchSources in frontend).value,
    build in Prod := (run in Compile)
      .toTask(" prod")
      .dependsOn(build in (Compile, fullOptJS) in frontend)
      .value,
    build in Dev := (run in Compile)
      .toTask(" dev")
      .dependsOn(build in (Compile, fastOptJS) in frontend)
      .value
  )

val meny = project
  .in(file("."))
  .aggregate(frontend, generator)
  .settings(
    build := build.in(generator, Dev).value
  )
