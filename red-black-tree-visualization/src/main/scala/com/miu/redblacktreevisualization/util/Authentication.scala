package com.miu.redblacktreevisualization.util

import tyrian.Cmd
import tyrian.cmds.*
import cats.effect.IO

object Authentication {

  def authenticate(username: String, password: String): Cmd[IO, Nothing] =
    PrettyLogger.success(
      s"mocking authentication for username: $username, password: $password"
    )

}
