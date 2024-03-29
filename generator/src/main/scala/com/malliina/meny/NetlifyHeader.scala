package com.malliina.meny

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.IteratorHasAsScala

case class NetlifyHeader(path: String, headers: Map[String, String]):
  // https://docs.netlify.com/routing/headers/#syntax-for-the-headers-file
  def asString: String =
    val headerList = headers
      .map: (k, v) =>
        s"$k: $v"
      .mkString("\n  ", "\n  ", "\n")
    s"$path$headerList"

object NetlifyHeader:
  def forall(headers: Map[String, String]) = apply("/*", headers)

  def security = forall(
    Map("X-Frame-Options" -> "DENY", "X-XSS-Protection" -> "1; mode=block")
  )

case class WebsiteFile(file: Path, cacheControl: CacheControl):
  def uri = file.toString.replace("\\", "/")

case class RedirectEntry(from: String, to: String, status: Int):
  val asString = s"$from $to $status"

object NetlifyClient extends NetlifyClient

class NetlifyClient:
  def writeHeaders(dir: Path): Path =
    writeHeadersFile(cached(dir), dir.resolve("_headers"))

  def writeRedirects(rs: Seq[RedirectEntry], dir: Path) =
    writeLines(rs.map(_.asString), dir.resolve("_redirects"))

  private def cached(dir: Path): Seq[WebsiteFile] =
    Files
      .walk(dir)
      .iterator()
      .asScala
      .toList
      .map: p =>
        val relative = dir.relativize(p)
        val cache =
          if isCacheable(relative) then CacheControls.eternalCache
          else CacheControls.defaultCacheControl
        WebsiteFile(relative, cache)

  private def isCacheable(p: Path) = p.getFileName.toString.count(_ == '.') == 2

  private def writeHeadersFile(files: Seq[WebsiteFile], to: Path): Path =
    val netlifyHeaders = NetlifyHeader.security +: files.map: file =>
      NetlifyHeader(
        s"/${file.uri}",
        Map(CacheControl.headerName -> file.cacheControl.value)
      )
    writeLines(netlifyHeaders.map(_.asString), to)

  private def writeLines(lines: Seq[String], to: Path) =
    FileIO.write(lines.mkString.getBytes(StandardCharsets.UTF_8), to)
