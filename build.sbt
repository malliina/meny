inThisBuild(
  Seq(
    organization := "com.malliina",
    version := "1.0.0",
    scalaVersion := "2.13.3"
  )
)

val frontend = project
  .in(file("frontend"))
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % "0.9.1"
    ),
    crossTarget in (Compile, fastOptJS) := (baseDirectory in ThisBuild).value / "dist"
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
    )
  )

val meny = project.in(file(".")).aggregate(frontend, generator)
