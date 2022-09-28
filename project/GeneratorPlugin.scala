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
    val mode = settingKey[Mode]("Build mode, dev or prod")
    val isProd = settingKey[Boolean]("true if in prod mode, false otherwise")

    val DevMode = Mode.Dev
    val ProdMode = Mode.Prod

    val clientProject = settingKey[Project]("Scala.js project")
  }
  import autoImport._
  import GeneratorKeys._

  override def projectSettings: Seq[Setting[_]] = Seq(
    isProd := ((Global / mode).value == Mode.Prod),
    siteDir := Def.settingDyn { clientProject.value / siteDir }.value,
    liveReloadRoot := siteDir.value.toPath,
    buildInfoKeys ++= Seq[BuildInfoKey](
      "siteDir" -> siteDir.value,
      "isProd" -> ((Global / scalaJSStage).value == FullOptStage)
    ),
    refreshBrowsers := refreshBrowsers.triggeredBy(build).value,
    build := Def.taskDyn {
      val jsTask = if (isProd.value) fullOptJS else fastOptJS
      (Compile / run).toTask(" ")
        .dependsOn(Def.task(if(isProd.value) () else reloader.value.start()))
        .dependsOn(clientProject.value / Compile / jsTask / build)
    }.value,
    watchSources := watchSources.value ++ Def.taskDyn(clientProject.value / watchSources).value
  )

  override def globalSettings: Seq[Setting[_]] = Seq(
    mode := Mode.Dev
  )
}
