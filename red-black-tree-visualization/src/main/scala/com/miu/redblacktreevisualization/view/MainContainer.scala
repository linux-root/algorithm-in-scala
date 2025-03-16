package com.miu.redblacktreevisualization.view

import tyrian.Html.*
import tyrian.Html
import com.miu.redblacktreevisualization.model.Msg
import com.miu.redblacktreevisualization.view.components.Icons
import tyrian.Elem

object MainContainer:

  def navbar(isDark: Boolean): Html[Msg] =
    nav(cls := "bg-green-600 dark:bg-green-800 fixed w-full z-20 top-0 start-0 border-b border-gray-200 dark:border-gray-700")(
      div(cls := "flex flex-wrap items-center justify-between p-4")(
        a(href := "/", cls := "flex items-center")(
          img(
            src := "/miu.png", 
            cls := "h-12 opacity-90 hover:opacity-100 transition-opacity"
          )
        ),
        darkModeSwitchButton(isDark)
      )
    )

  private val theFooter =
    footer(cls := "w-full bg-green-600 dark:bg-green-800 border-t dark:border-gray-700 text-white py-4")(
      div(cls := "container mx-auto text-center")(
        p(cls := "text-sm")(
          "Created by Scala with Love © 2025. All rights reserved."
        ),
        br(),
        a(
          href := "https://github.com/linux-root/tyrian-flowbite.g8",
          cls  := "text-green-200 hover:underline ml-2 flex items-center justify-center space-x-1 inline-flex"
        )(Icons.github)
      )
    )

  private def controlButton(clickMsg: Msg, child: Elem[Msg]) =
    val dark   = "dark:text-white dark:hover:bg-green-700 dark:focus:ring-green-200"
    val normal = "text-white hover:bg-green-700 focus:ring-green-100"
    button(
      onClick(clickMsg),
      cls := s"transition-transform duration-300 ease-in-out hover:scale-105 p-4 me-2 mb-2 rounded-full focus:outline-none $normal $dark"
    )(child)

  private def darkModeSwitchButton(isDark: Boolean) =
    val icon = if isDark then Icons.sun else Icons.moon
    controlButton(Msg.ToggleDarkMode, icon)

  def apply(content: Html[Msg], isDark: Boolean): Html[Msg] =
    div(cls := (if isDark then "dark" else "not-dark-mode"))(
      navbar(isDark),
      main(cls := "flex flex-col min-h-screen mt-20 bg-gray-200 dark:bg-gray-900")(content),
      theFooter
    )
