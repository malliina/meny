scalaVersion := "2.12.12"

Seq(
  "org.scala-js" % "sbt-scalajs" % "1.1.1",
  "ch.epfl.scala" % "sbt-scalajs-bundler" % "0.18.0",
  "ch.epfl.scala" % "sbt-bloop" % "1.4.4",
  "org.scalameta" % "sbt-scalafmt" % "2.4.2"
) map addSbtPlugin
