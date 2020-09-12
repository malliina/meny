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
    p("Hi!")
  )

  def meny = index("Johannas meny")(
    div(`class` := "slider")(
      a(href := "#menu-1")("1"),
      a(href := "#menu-2")("2"),
      a(href := "#menu-3")("3"),
      div(`class` := "slides")(
        div(id := "menu-1")("Meny 1"),
        div(id := "menu-2")("Meny 2"),
        div(id := "menu-3")("Meny 3")
      )
    )
  )

  def carousel = index("Johannas meny")(
    tag("section")(`class` := "carousel", aria.label := "Gallery")(
      ol(`class` := "carousel__viewport")(
        li(id := "carousel__slide1", tabindex := "0", `class` := "carousel__slide")(
          div(`class` := "carousel__snapper")(
            a(href := "#carousel__slide3", `class` := "caoursel__prev")("Go to last slide"),
            a(href := "#carousel__slide2", `class` := "caoursel__next")("Go to next slide")
          )
        ),
        li(id := "carousel__slide2", tabindex := "0", `class` := "carousel__slide")(
          div(`class` := "carousel__snapper")(
            a(href := "#carousel__slide1", `class` := "caoursel__prev")("Go to prev slide"),
            a(href := "#carousel__slide3", `class` := "caoursel__next")("Go to next slide")
          )
        ),
        li(id := "carousel__slide3", tabindex := "0", `class` := "carousel__slide")(
          div(`class` := "carousel__snapper")(
            a(href := "#carousel__slide2", `class` := "caoursel__prev")("Go to prev slide"),
            a(href := "#carousel__slide1", `class` := "caoursel__next")("Go to first slide")
          )
        )
      )
    )
  )

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
          "Välkomstdrink",
          "Brieost med vitlöksbatong",
          dish("Pizza bolognese med ananas", "Coca-Cola med is & lime"),
          dish("Äppelpaj med glass", "Dessertvin")
        ),
        menuItem(
          3,
          "Välkomstdrink",
          "Karelsk pirog & äggsmör",
          dish("Currywurst med pommes frites", "Pilsner Urquell -öl"),
          dish("Banana split", "Dessertvin")
        )
      ),
      div(`class` := "swiper-button swiper-button-next"),
      div(`class` := "swiper-button swiper-button-prev")
    )
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
        link(rel := "stylesheet", href := "styles.css"),
        link(rel := "stylesheet", href := "vendors.css")
      ),
      body(
        contents :+ modifier(
//          script(src := "frontend-fastopt-loader.js", defer),
          script(src := "frontend-fastopt.js", defer)
        )
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
