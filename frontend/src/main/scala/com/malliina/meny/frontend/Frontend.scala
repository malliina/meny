package com.malliina.meny.frontend

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.annotation.JSImport

object Frontend:
  def main(args: Array[String]): Unit =
    val swiper = new Swiper(
      ".swiper-container",
      SwiperOptions("horizontal", true, NavOptions(".swiper-button-next", ".swiper-button-prev"))
    )

@js.native
trait NavOptions extends js.Object:
  def nextEl: String = js.native
  def prevEl: String = js.native

object NavOptions:
  def apply(nextEl: String, prevEl: String): NavOptions =
    literal(nextEl = nextEl, prevEl = prevEl).asInstanceOf[NavOptions]

@js.native
trait SwiperOptions extends js.Object:
  def direction: String = js.native
  def loop: Boolean = js.native
  def navigation: NavOptions = js.native

object SwiperOptions:
  def apply(direction: String, loop: Boolean, navigation: NavOptions): SwiperOptions =
    literal(direction = direction, loop = loop, navigation = navigation).asInstanceOf[SwiperOptions]

@js.native
@JSImport("swiper/bundle", JSImport.Default)
class Swiper(container: String, opts: SwiperOptions) extends js.Object
