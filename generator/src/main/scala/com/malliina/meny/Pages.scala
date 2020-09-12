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
  def apply(isProd: Boolean): Pages = new Pages(isProd)

  implicit val fullUrl: AttrValue[FullUrl] = attrType[FullUrl](_.url)

  val time = tag("time")
  val titleTag = tag("title")

  val datetime = attr("datetime")
  val property = attr("property")

  def attrType[T](stringify: T => String): AttrValue[T] = (t: Builder, a: Attr, v: T) =>
    t.setAttr(a.name, Builder.GenericAttrValueSource(stringify(v)))
}

class Pages(isProd: Boolean) {
  val listFile = "list.html"
  val remoteListUri = "list"

  val globalDescription = "Meny."

  val scripts =
    if (isProd) {
      scriptAt("frontend-opt.js", defer)
    } else {
      modifier(
        scriptAt("library.js"),
        scriptAt("loader.js"),
        scriptAt("app.js")
      )
    }

  def swiper = index("Johannas meny")(
    div(`class` := "swiper-container")(
      div(`class` := "swiper-wrapper meny-wrapper")(
        menuItem(
          1,
          p("Välkomstdrink"),
          p("Vitlöksbröd med tzatziki"),
          dish("Hönsfrikassé", "Iskall Vichy"),
          dish("Blåbärspaj med vaniljsås", "Cappuccino")
        ),
        menuItem(
          2,
          p("Välkomstdrink"),
          p("Brieost med vitlöksbatong"),
          dish("Pizza bolognese med ananas", "Coca-Cola med is & lime"),
          dish("Äppelpaj med glass", "Dessertvin")
        ),
        menuItem(
          3,
          p("Välkomstdrink"),
          p("Karelsk pirog & äggsmör"),
          dish("Currywurst med pommes frites", "Pilsner Urquell -öl"),
          dish("Banana split", "Dessertvin")
        )
      ),
      div(`class` := "swiper-button swiper-button-next"),
      div(`class` := "swiper-button swiper-button-prev")
    ),
    footer(`class` := "meny-footer")
  )

  def dish(food: String, drink: String) = modifier(p(food), separator, p(drink))

  def separator = hr

  def menuItem(num: Int, init: Modifier, one: Modifier, two: Modifier, three: Modifier) =
    div(`class` := "swiper-slide meny")(
      h1(`class` := "meny-title")(s"Meny $num"),
      init,
      menuSeparator,
      one,
      menuSeparator,
      two,
      menuSeparator,
      three
    )

  def menuSeparator = div(`class` := "meny-hr")("**************************")

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
        meta(property := "og:description", content := globalDescription),
        //styleAt("styles-fonts.css"),
        //styleAt("styles-main.css")
        styleAt("styles.css"),
        styleAt("vendors.css"),
        styleAt("fonts.css")
      ),
      body(
        contents :+ scripts
      )
    )
  )

  def format(date: LocalDate) = {
    val localDate = DateTimeFormatter.ISO_LOCAL_DATE.format(date)
    time(datetime := localDate)(localDate)
  }

  def styleAt(file: String) = link(rel := "stylesheet", href := findAsset(file))

  def scriptAt(file: String, modifiers: Modifier*) = script(src := findAsset(file), modifiers)

  def findAsset(file: String): String = {
    val root = Paths.get("target").resolve("site")
    val path = root.resolve(file)
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
