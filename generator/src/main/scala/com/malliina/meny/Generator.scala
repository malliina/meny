package com.malliina.meny

import java.nio.file.{Files, Path, Paths}

object Generator {
  val log = AppLogger(getClass)

  def main(args: Array[String]): Unit =
    args.toList match {
      case mode :: root :: Nil =>
        val path = Paths.get(root)
        log.info(s"Generating $mode from '$path'...")
        generate(mode == "prod", path)
      case other =>
        throw new Exception(s"Invalid arguments: ${other.mkString(" ")}")
    }

  def generate(isProd: Boolean, dist: Path) = {
    Files.createDirectories(dist)
    val pages = Pages(isProd, dist)
    val pageMap = Map(
      pages.meny2020 -> "2020.html",
      pages.meny2021 -> "index.html"
    )
    pageMap.foreach { case (page, file) => page.write(dist.resolve(file)) }
    NetlifyClient.writeHeaders(dist)
  }
}
