import org.slf4j.{Logger, LoggerFactory}

object AppLogger {
  def apply(cls: Class[_]): Logger = {
    val name = cls.getName.reverse.dropWhile(_ == '$').reverse
    LoggerFactory.getLogger(name)
  }
}

sealed abstract class Mode(val name: String)

object Mode {
  case object Prod extends Mode("prod")
  case object Dev extends Mode("dev")
}
