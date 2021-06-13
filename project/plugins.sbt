scalaVersion := "2.12.14"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.2.3",
  "org.scala-js" % "sbt-scalajs" % "1.6.0",
  "ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0",
  "org.scalameta" % "sbt-scalafmt" % "2.4.2"
) map addSbtPlugin
