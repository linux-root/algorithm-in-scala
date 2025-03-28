package com.miu.redblacktreevisualization

import com.miu.redblacktreevisualization.page.*
import tyrian.Location
import com.miu.redblacktreevisualization.view.interopjs.TreeRenderer
import tyrian.Cmd
import cats.effect.IO
import scala.collection.View.Empty
import com.miu.redblacktreevisualization.core.BST

object model {
  enum Msg {
    case NoOp
    case NavigateTo(page: Page)
    case DoNavigate(page: Page)
    case CreateTreeView
    case Insert(value: Int)
    case ResetTree
    case InsertInput
    case InsertRandom
    case UpdateInputValue(value: String)
    case ViolationDetected(violation: BST.Violation)
    case ResolveViolation
    case UnhandledRoute(path: String)
    case GoToInternet(loc: Location.External)
    case ToggleDarkMode
  }

  case class Model(
    currentPage: Page,
    input: String,
    bst: Option[BST],
    treeView: Option[TreeRenderer],
    violation: Option[BST.Violation],
    isDarkMode: Boolean
  ) {
    def resolveViolation: Model =
      violation match {
        case None =>
          this
        case Some(v) =>
          copy(bst = bst.map(_.resolve(v)), violation = None)
      }

    def toggleDarkMode: Model =
      copy(isDarkMode = !isDarkMode)

    def refreshTreeCmd: Cmd[IO, Nothing] =
      this.treeView match {
        case None => Cmd.None
        case Some(r) =>
          r.renderCmd(bst)
      }
  }

  object Model {
    val init: Model = Model(Page.Home, "", None, None, None, isDarkMode = false)

  }
}
