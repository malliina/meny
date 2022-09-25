package com.malliina.meny

import buildinfo.BuildInfo

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

object Generator:
  val log = AppLogger(getClass)

  def main(args: Array[String]): Unit =
    generate(BuildInfo.isProd, BuildInfo.siteDir.toPath)

  def generate(isProd: Boolean, dist: Path) =
    val mode = if isProd then "prod" else "dev"
    log.info(s"Generating $mode build to $dist...")
    Files.createDirectories(dist)
    val pages = Pages(isProd, dist)
    val pageMap = Map(
      pages.meny2020 -> "2020.html",
      pages.meny2021 -> "index.html"
    )
    pageMap.foreach { case (page, file) => page.write(dist.resolve(file)) }
    NetlifyClient.writeHeaders(dist)
