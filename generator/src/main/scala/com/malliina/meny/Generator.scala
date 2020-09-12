package com.malliina.meny

import java.nio.file.{Files, Paths}

object Generator {
  val log = AppLogger(getClass)

  def main(args: Array[String]): Unit = {
    log.info(s"Generating ${args.mkString(", ")}")
    generate(args.contains("prod"))
  }

  def generate(isProd: Boolean) = {
    val dist = Paths.get("target/site")
    Files.createDirectories(dist)
    val pages = Pages(isProd)
    pages.swiper.write(dist.resolve("index.html"))
  }
}
