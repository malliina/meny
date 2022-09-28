import GeneratorKeys.build
import GeneratorPlugin.autoImport.isProd
import sbt.Keys.{baseDirectory, streams}
import sbt.{AutoPlugin, Plugins, Setting, ThisBuild, taskKey}

object NetlifyPlugin extends AutoPlugin {
  override def requires: Plugins = GeneratorPlugin

  object autoImport {
    val deploy = taskKey[Unit]("Deploys the site")
  }
  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    deploy := {
      val cmd =
        if (isProd.value) "netlify deploy --prod"
        else "netlify deploy"
      CommandLine.runProcessSync(
        cmd,
        (ThisBuild / baseDirectory).value,
        streams.value.log
      )
    },
    deploy := deploy.dependsOn(build).value
  )
}
