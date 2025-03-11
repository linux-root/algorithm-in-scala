package com.miu.redblacktreevisualization

import tyrian.Cmd
import tyrian.Html
import tyrian.Html.div
import java.util.UUID
import com.miu.redblacktreevisualization.model.*
import com.miu.redblacktreevisualization.view.pages.*
import tyrian.cmds.Logger
import cats.effect.IO
import scala.concurrent.duration.*
import com.miu.redblacktreevisualization.util.PrettyLogger

package object page {
  enum Page(
    val path: String,
    val render: Model => Html[Msg],
    beforeEnter: Model => Cmd[IO, Msg] = _ => Cmd.None, // e.g: side effect for loading data
    val isSecured: Boolean = true
  ):
    def doNavigate(model: Model): Cmd[IO, Msg] = beforeEnter(model) |+| Cmd.emit(Msg.DoNavigate(this))


    case Home       extends Page("/", model => BSTView(model.bst, model.violation), _ => Cmd.emitAfterDelay(Msg.CreateTreeView, 100.millis))
}
