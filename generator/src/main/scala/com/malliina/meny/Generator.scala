package com.malliina.meny

import java.nio.file.{Files, Paths}

object Generator {
  def main(args: Array[String]): Unit = {
    generate()
  }

  def generate() = {
    val dist = Paths.get("dist")
    Files.createDirectories(dist)
    Pages().index("Meny")().write(dist.resolve("index.html"))
  }
}
