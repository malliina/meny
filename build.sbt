import com.malliina.sbtutils.SbtUtils

inThisBuild(
  Seq(
    organization := "com.malliina",
    version := "1.0.1",
    scalaVersion := "3.3.1"
  )
)

val frontend = project
  .in(file("frontend"))
  .enablePlugins(RollupPlugin)

val generator = project
  .in(file("generator"))
  .enablePlugins(NetlifyPlugin)
  .settings(
    scalajsProject := frontend,
    copyFolders += ((Compile / resourceDirectory).value / "public").toPath,
    hashPackage := "com.malliina.meny",
    libraryDependencies ++= SbtUtils.loggingDeps ++ Seq(
      "com.malliina" %% "primitives" % "3.4.5",
      "com.lihaoyi" %% "scalatags" % "0.12.0"
    ),
    Compile / compile := (Compile / compile).dependsOn(frontend / build).value
  )

val meny = project
  .in(file("."))
  .aggregate(frontend, generator)

Global / onChangedBuildSource := ReloadOnSourceChanges
