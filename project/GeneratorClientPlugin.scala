import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.toPlatformDepsGroupID
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.{fastOptJS, fullOptJS, scalaJSUseMainModuleInitializer}
import sbt.Keys._
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.{BundlerFileType, BundlerFileTypeAttr, BundlingMode, startWebpackDevServer, webpack, webpackBundlingMode, webpackConfigFile, webpackEmitSourceMaps, webpackMonitoredDirectories, webpackMonitoredFiles}

import java.nio.file.{Files, StandardCopyOption}

object GeneratorClientPlugin extends AutoPlugin {
  val scalatagsVersion = "0.11.1"
  override def requires = ScalaJSBundlerPlugin

  import GeneratorKeys._

  override def projectSettings: Seq[Setting[_]] = Seq(
    siteDir := baseDirectory.value / "target" / "site",
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
    },
    watchSources += WatchSource(baseDirectory.value / "src", "*.scala", HiddenFileFilter),
    webpack / version := "5.74.0",
    startWebpackDevServer / version := "4.11.0",
    Compile / fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(),
    Compile / fullOptJS / webpackBundlingMode := BundlingMode.Application,
    webpackEmitSourceMaps := false,
    Compile / fullOptJS / build := (Compile / fullOptJS / webpack).value.map { af =>
      val destDir = siteDir.value
      Files.createDirectories(destDir.toPath)
      val dest = (destDir / af.data.name).toPath
      sLog.value.info(s"Write $dest ${af.metadata}")
      Files.copy(af.data.toPath, dest, StandardCopyOption.REPLACE_EXISTING).toFile
    },
    Compile / fastOptJS / build := (Compile / fastOptJS / webpack).value.map { af =>
      val destDir = siteDir.value
      Files.createDirectories(destDir.toPath)
      val name = af.metadata.get(BundlerFileTypeAttr) match {
        case Some(BundlerFileType.Application) => "app.js"
        case Some(BundlerFileType.Library) => "library.js"
        case Some(BundlerFileType.Loader) => "loader.js"
        case _ => af.data.name
      }
      val dest = (destDir / name).toPath
      sLog.value.info(
        s"Write $dest from ${af.data.name} ${af.metadata}"
      )
      Files.copy(af.data.toPath, dest, StandardCopyOption.REPLACE_EXISTING).toFile
    }
  )
}

