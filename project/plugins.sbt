scalaVersion := "2.12.14"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.2.5",
  "com.malliina" % "live-reload" % "0.2.3",
  "org.scala-js" % "sbt-scalajs" % "1.7.0",
  "ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0",
  "org.scalameta" % "sbt-scalafmt" % "2.4.3"
) map addSbtPlugin
