scalaVersion := "2.12.16"

libraryDependencies ++= Seq(
  "com.malliina" %% "primitives" % "3.4.0"
)

val utilsVersion = "1.6.12"

Seq(
  "com.malliina" % "sbt-utils-maven" % utilsVersion,
  "com.malliina" % "sbt-revolver-rollup" % utilsVersion,
  "com.malliina" % "sbt-nodejs" % utilsVersion,
  "com.malliina" % "live-reload" % "0.5.0",
  "org.scala-js" % "sbt-scalajs" % "1.13.0",
  "org.scalameta" % "sbt-scalafmt" % "2.5.0",
  "com.eed3si9n" % "sbt-buildinfo" % "0.11.0"
) map addSbtPlugin
