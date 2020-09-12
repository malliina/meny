package com.malliina.meny

import java.nio.file.{Files, Paths}

object Generator {
  def main(args: Array[String]): Unit = {
    generate()
  }

  def generate() = {
    val dist = Paths.get("dist")
    Files.createDirectories(dist)
    val pages = Pages()
    pages.one.write(dist.resolve("index.html"))
    pages.meny.write(dist.resolve("meny.html"))
    pages.carousel.write(dist.resolve("carousel.html"))
    pages.swiper.write(dist.resolve("swiper.html"))
  }
}
