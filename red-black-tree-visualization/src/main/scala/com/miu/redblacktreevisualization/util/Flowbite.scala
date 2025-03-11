package com.miu.redblacktreevisualization.util

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import tyrian.Cmd
import scala.concurrent.duration.*
import cats.effect.*
import cats.effect.Sync

object Flowbite:
  @JSImport("js/flowbite.js", JSImport.Namespace)
  @js.native
  private object FlowbiteJS extends js.Object:
    def init(): Unit = js.native

  val initCmd: Cmd[IO, Nothing] =
    val effect = Temporal[IO].sleep(100.millis) *> Sync[IO].delay(FlowbiteJS.init())
    Cmd.SideEffect(effect)
