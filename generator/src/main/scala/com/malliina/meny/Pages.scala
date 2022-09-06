package com.malliina.meny

import com.malliina.http.FullUrl
import com.malliina.live.LiveReload
import com.malliina.meny.Pages._
import scalatags.Text.all._
import scalatags.text.Builder

import java.nio.file.{Files, Path}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters.IteratorHasAsScala

object Pages {
  implicit val fullUrl: AttrValue[FullUrl] = attrType[FullUrl](_.url)

  val time = tag("time")
  val titleTag = tag("title")

  val datetime = attr("datetime")
  val property = attr("property")

  def attrType[T](stringify: T => String): AttrValue[T] = (t: Builder, a: Attr, v: T) =>
    t.setAttr(a.name, Builder.GenericAttrValueSource(stringify(v)))
}

class Pages(isProd: Boolean, root: Path) {
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
        scriptAt("app.js"),
        script(src := LiveReload.script)
      )
    }

  def meny2020 = index("Johannas meny")(
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

  def meny2021 = index("Johannas meny 2021")(
    div(`class` := "swiper-container")(
      div(`class` := "swiper-wrapper meny-wrapper")(
        menuItem(
          1,
          p("Kombucha med bär"),
          p("Fruktsallad"),
          dish("Friterad höna och potatis med gourmet-såser", "Leffe Blond"),
          dish("Ostkaka med jordgubbar", "Cafe Latte")
        ),
        menuItem(
          2,
          p("Tropisk saft med krossad is och ingefära-shot"),
          p("Smörgåstårta"),
          dish("Fitness-sallad", "Vichy on the rocks"),
          dish("Nougat-tryffel chokladdröm", "Sött dessertvin")
        ),
        menuItem(
          3,
          p("Prosecco"),
          p("Jordgubbar, hallon och blåbärsmix"),
          dish("Osthamburgare med bacon", "Iskall Coca-Cola"),
          dish("Toscakaka med vispgrädde", "Cloudy Apple med is")
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
