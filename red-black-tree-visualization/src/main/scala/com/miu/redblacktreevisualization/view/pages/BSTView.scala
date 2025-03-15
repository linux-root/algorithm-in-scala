package com.miu.redblacktreevisualization.view.pages

import tyrian.Html
import tyrian.Html.*
import com.miu.redblacktreevisualization.model.Msg
import com.miu.redblacktreevisualization.model.Msg.InsertRandom
import com.miu.redblacktreevisualization.core.BST
import com.miu.redblacktreevisualization.model.Model

object BSTView:

  def apply(state: Model): Html[Msg] = {
    val t = "Red Black Tree Visualization"
    div(cls := "flex flex-col")(
      h1()(t),
      controlPanel(state),
      div(id := "tuesday")()
    )
  }

  def controlPanel(state: Model): Html[Msg] =
    val violation         = state.violation
    val currentInputValue = state.input
    div(cls := "flex flex-col gap-4")(
      div(cls := "p-4 bg-blue-500 text-white")(violation match {
        case Some(n) => button(cls := "bg-orange-500", onClick(Msg.ResolveViolation))(s"Resolve ${n}(${n.node.value})")
        case None    => text("No violation")
      }),
      button(cls := "m-8 bg-green-500 text-white", onClick(Msg.InsertRandom))("Insert random"),
      // button(cls := "m-8 bg-green-500 text-white", onClick(Msg.ResetTree))("reset tree"),
      div(cls := "flex gap-4 bg-yellow-400")(
        input(value := currentInputValue, onChange(Msg.UpdateInputValue(_))),
        button(cls := "m-8 bg-green-500 text-white", onClick(Msg.InsertInput))("Insert")
      )
    )
