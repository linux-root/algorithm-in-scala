package com.miu.redblacktreevisualization.view.interopjs

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import scala.scalajs.js.*
import tyrian.Cmd
import cats.effect.*
import cats.effect.Sync
import scala.concurrent.duration.*
import com.miu.redblacktreevisualization.core.BST

@js.native
trait TreeRendererOptions extends js.Object {
  var width: Int = js.native
  var height: Int = js.native
  var nodeRadius: Int = js.native
  var margin: MarginOptions = js.native
}

@js.native
trait MarginOptions extends js.Object {
  var top: Int = js.native
  var right: Int = js.native
  var bottom: Int = js.native
  var left: Int = js.native
}

object TreeRendererOptions {
  def apply(
    width: Int = 1000,
    height: Int = 500,
    nodeRadius: Int = 20,
    margin: MarginOptions = MarginOptions()
  ): TreeRendererOptions = {
    val options = js.Object().asInstanceOf[TreeRendererOptions]
    options.width = width
    options.height = height
    options.nodeRadius = nodeRadius
    options.margin = margin
    options
  }
}

object MarginOptions {
  def apply(
    top: Int = 40,
    right: Int = 90,
    bottom: Int = 50,
    left: Int = 90
  ): MarginOptions = {
    val margin = js.Object().asInstanceOf[MarginOptions]
    margin.top = top
    margin.right = right
    margin.bottom = bottom
    margin.left = left
    margin
  }
}

//TODO: note Never use import namespace when import class. Add it to your note
@js.native
@JSImport("js/tree-renderer.js", "TreeRenderer")
class JsTreeRenderer(containerId: String, options: TreeRendererOptions = null) extends js.Object {
  def render(bst: js.Object): Unit = js.native //FIXME:
  def resize(width: Int, height: Int): Unit = js.native
}


case class TreeRenderer(containerId: String) {
  private val renderer = new JsTreeRenderer(containerId, null)

  private def toJs(tree: BST): js.Object = tree match {
    case BST.Empty(_) => 
      js.Dynamic.literal(
        $type = "Empty"
      )
    case BST.Node(_, value, color, left, right) => 
      js.Dynamic.literal(
        $type = "Node",
        value = value,
        color = color.toString,
        left = toJs(left),
        right = toJs(right)
      )
  }

  def renderCmd(tree: BST): Cmd[IO, Nothing] = {
    Cmd.SideEffect(renderer.render(toJs(tree)))
  }
}
