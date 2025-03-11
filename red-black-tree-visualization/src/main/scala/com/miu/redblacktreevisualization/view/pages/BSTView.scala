package com.miu.redblacktreevisualization.view.pages

import tyrian.Html
import tyrian.Html.*
import com.miu.redblacktreevisualization.model.Msg
import com.miu.redblacktreevisualization.core.BST

object BSTView:
  def apply(bst: BST, violation: Option[BST.Violation]): Html[Msg] =
    val title = "Red Black Tree Visualization"
    val description = "This is a visualization of a Red Black Tree"
    val keywords = "Red Black Tree, Visualization, Scala, Scala 3, Dotty, Tyrian, Indigo, Flow"
    div()(h1(cls:="font-bold")(title), p()(description), 
      div(cls:="p-4 bg-blue-500 text-white")(s"Valid Red Black Tree ? ${bst.isValid}"),
      div(cls:="p-4 bg-blue-500 text-white")(violation match {
        case Some(n) => button(cls:="bg-orange-500",onClick(Msg.ResolveViolation))(s"Resolve ${n.node.value}")
        case None => text("No violation")
      }),
      pre(cls:="p-4 bg-gray-200")(bst.toString),
      div(id:="tuesday")(
        button(cls:="m-8 bg-green-500 text-white",onClick(Msg.InsertRandom))("Insert random"),
        )
    )

