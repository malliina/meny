package com.malliina.meny

import buildinfo.BuildInfo

import java.nio.file.{Files, Path}

object Generator:
  val log = AppLogger(getClass)

  def main(args: Array[String]): Unit =
    generate(BuildInfo.isProd, BuildInfo.siteDir.toPath)

  private def generate(isProd: Boolean, dist: Path): Unit =
    val mode = if isProd then "prod" else "dev"
    log.info(s"Generating $mode build to $dist...")
    Files.createDirectories(dist)
    val pages = Pages(isProd, dist)
    val pageMap = Map(
      "index.html" -> pages.meny2021,
      "2020.html" -> pages.meny2020,
      "404.html" -> pages.notFound
    )
    pageMap.foreach: (file, page) =>
      page.write(dist.resolve(file))
    NetlifyClient.writeHeaders(dist)
