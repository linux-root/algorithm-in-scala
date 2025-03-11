package com.miu.redblacktreevisualization.util

import org.scalajs.dom.console
import tyrian.Cmd
import cats.effect.*
import cats.effect.Sync

object PrettyLogger {
  def success(msg: String): Cmd[IO, Nothing] =
    Cmd.SideEffect(
      console.log(
        s"%c😇 $msg %c",
        "color: white; background: green; font-size: 16px; padding: 5px 10px; border-radius: 5px;",
        "color: green; font-size: 14px;"
      )
    )

  def info(msg: String): Cmd[IO, Nothing] =
    Cmd.SideEffect(
      console.log(
        s"%cℹ️ $msg %c",
        "color: white; background: blue; font-size: 16px; padding: 5px 10px; border-radius: 5px;",
        "color: blue; font-size: 14px;"
      )
    )

  def error(msg: String): Cmd[IO, Nothing] =
    Cmd.SideEffect(
      console.log(
        s"%cℹ️ $msg %c",
        "color: white; background: red; font-size: 16px; padding: 5px 10px; border-radius: 5px;",
        "color: red; font-size: 14px;"
      )
    )
}
