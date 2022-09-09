import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.toPlatformDepsGroupID
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.{
  fastOptJS,
  fullOptJS,
  scalaJSUseMainModuleInitializer
}
import sbt.Keys._
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.{
  webpackConfigFile,
  webpackMonitoredDirectories,
  webpackMonitoredFiles
}

object GeneratorClientPlugin extends AutoPlugin {
  val scalatagsVersion = "0.11.1"
  override def requires = ScalaJSBundlerPlugin

  override def projectSettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.2.0",
      "com.lihaoyi" %%% "scalatags" % scalatagsVersion
    ),
    scalaJSUseMainModuleInitializer := true,
    fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.dev.config.js"),
    fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.prod.config.js"),
    // Enables hot-reload of CSS
    webpackMonitoredDirectories ++= (Compile / resourceDirectories).value.map { dir =>
      dir / "css"
    },
    webpackMonitoredFiles / includeFilter := "*.less",
    watchSources ++= (Compile / resourceDirectories).value.map { dir =>
      WatchSource(dir / "css", "*.less", HiddenFileFilter)
    }
  )
}
