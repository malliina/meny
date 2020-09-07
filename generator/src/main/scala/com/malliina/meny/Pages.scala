package com.malliina.meny

import java.nio.file.{Files, Paths}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.malliina.meny.Pages._
import com.malliina.http.FullUrl
import scalatags.Text.all._
import scalatags.text.Builder

import scala.collection.JavaConverters.asScalaIteratorConverter

object Pages {
  def apply(): Pages = new Pages()

  val domain = FullUrl.https("todo.com", "")
  implicit val fullUrl: AttrValue[FullUrl] = attrType[FullUrl](_.url)

  val time = tag("time")
  val titleTag = tag("title")

  val datetime = attr("datetime")
  val property = attr("property")

  def attrType[T](stringify: T => String): AttrValue[T] = (t: Builder, a: Attr, v: T) =>
    t.setAttr(a.name, Builder.GenericAttrValueSource(stringify(v)))
}

class Pages {
  val listFile = "list.html"
  val remoteListUri = "list"

  val globalDescription = "Meny."

  def one = index("Meny 1")(
    p("Hi!!!")
  )

  def index(titleText: String)(contents: Modifier*): TagPage = TagPage(
    html(lang := "en")(
      head(
        titleTag(titleText),
        meta(charset := "UTF-8"),
        meta(
          name := "viewport",
          content := "width=device-width, initial-scale=1.0, maximum-scale=1.0"
        ),
        meta(name := "description", content := globalDescription),
        meta(
          name := "keywords",
          content := "Meny"
        ),
        meta(name := "twitter:card", content := "summary"),
        meta(name := "twitter:site", content := "@kungmalle"),
        meta(name := "twitter:creator", content := "@kungmalle"),
        meta(property := "og:title", content := titleText),
        meta(property := "og:description", content := globalDescription)
        //styleAt("styles-fonts.css"),
        //styleAt("styles-main.css")
      ),
      body(
        contents :+ script(src := "frontend-fastopt.js", defer) // :+ scriptAt("main.js", defer)
      )
    )
  )

  def format(date: LocalDate) = {
    val localDate = DateTimeFormatter.ISO_LOCAL_DATE.format(date)
    time(datetime := localDate)(localDate)
  }

  def styleAt(file: String) =
    link(rel := "stylesheet", href := findAsset(s"css/$file"))

  def scriptAt(file: String, modifiers: Modifier*) = script(src := findAsset(file), modifiers)

  def findAsset(file: String): String = {
    val root = Paths.get("target").resolve("site")
    val path = root.resolve("assets").resolve(file)
    val dir = path.getParent
    val candidates = Files.list(dir).iterator().asScala.toList
    val lastSlash = file.lastIndexOf("/")
    val nameStart = if (lastSlash == -1) 0 else lastSlash + 1
    val name = file.substring(nameStart)
    val dotIdx = name.lastIndexOf(".")
    val noExt = name.substring(0, dotIdx)
    val ext = name.substring(dotIdx + 1)
    val result = candidates.filter { p =>
      val candidateName = p.getFileName.toString
      candidateName.startsWith(noExt) && candidateName.endsWith(ext)
    }.sortBy { p => Files.getLastModifiedTime(p) }.reverse.headOption
    val found = result.getOrElse(
      fail(s"Not found: '$file'. Found ${candidates.mkString(", ")}.")
    )
    root.relativize(found).toString.replace("\\", "/")
  }

  def fail(message: String) = throw new Exception(message)
}
