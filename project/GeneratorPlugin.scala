import com.malliina.live.LiveReloadPlugin
import com.malliina.live.LiveReloadPlugin.autoImport.{liveReloadRoot, refreshBrowsers, reloader}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.{FullOptStage, fastOptJS, fullOptJS, scalaJSStage}
import sbt._
import sbt.Keys.{run, watchSources}
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport.{BuildInfoKey, buildInfoKeys}

object GeneratorPlugin extends AutoPlugin {
  override def requires = BuildInfoPlugin && LiveReloadPlugin

  object autoImport {
    val Dev = config("dev")
    val Prod = config("prod")
    val clientProject = settingKey[Project]("Scala.js project")
  }
  import autoImport._
  import GeneratorKeys._

  override def projectSettings: Seq[Setting[_]] = Seq(
    siteDir := Def.settingDyn { clientProject.value / siteDir }.value,
    liveReloadRoot := siteDir.value.toPath,
    buildInfoKeys ++= Seq[BuildInfoKey](
      "siteDir" -> siteDir.value,
      "isProd" -> ((Global / scalaJSStage).value == FullOptStage)
    ),
    refreshBrowsers := refreshBrowsers.triggeredBy(Dev / build).value,
    Dev / build := Def.taskDyn {
      (Compile / run).toTask(" ")
        .dependsOn(Def.task(reloader.value.start()))
        .dependsOn(clientProject.value / Compile / fastOptJS / build)
    }.value,
    Prod / build := Def.taskDyn {
      (Compile / run)
        .toTask(s" ")
        .dependsOn(clientProject.value / Compile / fullOptJS / build)
    }.value,
    watchSources := watchSources.value ++ Def.taskDyn(clientProject.value / watchSources).value
  )
}
