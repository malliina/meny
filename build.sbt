import java.nio.file.{Files, StandardCopyOption}

inThisBuild(
  Seq(
    organization := "com.malliina",
    version := "1.0.0",
    scalaVersion := "2.13.3"
  )
)

val build = taskKey[Seq[File]]("build frontend assets")

val frontend = project
  .in(file("frontend"))
  .enablePlugins(GeneratorClientPlugin)
  .settings(
    build := webpack.in(Compile, fastOptJS).value.map { af =>
      val dest = ((baseDirectory in ThisBuild).value / "dist" / af.data.name).toPath
      Files.copy(af.data.toPath, dest, StandardCopyOption.REPLACE_EXISTING).toFile
    },
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % "0.9.1"
    ),
//    crossTarget in (Compile, fastOptJS) := (baseDirectory in ThisBuild).value / "dist",
    watchSources += WatchSource(baseDirectory.value / "src", "*.scala", HiddenFileFilter),
    version in webpack := "4.39.1",
    version in startWebpackDevServer := "3.7.2",
    //    webpackBundlingMode := BundlingMode.LibraryOnly(),
    webpackEmitSourceMaps := false,
    npmDependencies in Compile ++= Seq(
      "@fortawesome/fontawesome-free" -> "5.10.1",
      "bootstrap" -> "4.3.1",
      "jquery" -> "3.4.1",
      "popper.js" -> "1.15.0"
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
    run in Compile := (run in Compile).dependsOn(fastOptJS in Compile in frontend).evaluated
  )

val meny = project.in(file(".")).aggregate(frontend, generator)
