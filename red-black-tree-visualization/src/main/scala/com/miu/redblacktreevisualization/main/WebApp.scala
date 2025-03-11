package com.miu.redblacktreevisualization.main

import tyrian.Html.*
import tyrian.*
import cats.effect.IO
import com.softwaremill.quicklens.*
import scala.scalajs.js
import scala.scalajs.js.annotation.*
import tyrian.CSS.*
import tyrian.Routing
import com.miu.redblacktreevisualization.model.*
import com.miu.redblacktreevisualization.util.Flowbite
import com.miu.redblacktreevisualization.view.MainContainer
import com.miu.redblacktreevisualization.route.*
import com.miu.redblacktreevisualization.util.*
import com.miu.redblacktreevisualization.page.*
import tyrian.cmds.Logger
import cats.syntax.*
import com.miu.redblacktreevisualization.view.interopjs.TreeRenderer
import scala.util.Random
import cats.effect.kernel.Sync

@JSImport("resources/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

@JSExportTopLevel("TyrianApp")
object WebApp extends TyrianIOApp[Msg, Model]:

  private val css = IndexCSS // Webpack will use this css when bundling

  def main(args: Array[String]): Unit = launch("app") // mount the app to div with id="app"

  def router: Location => Msg =
    case loc: Location.Internal =>
      loc.pathName match
        case Route.Home(_) =>
          Msg.NavigateTo(Page.Home)
        case path @ _ =>
          Msg.UnhandledRoute(path)

    case loc: Location.External => Msg.GoToInternet(loc)

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    val initState = Model.init
    (initState, Cmd.None)

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
    case Msg.NoOp => (model, Cmd.None)

    case Msg.CreateTreeView =>
      (model.copy(treeView = Some(new TreeRenderer("tuesday"))), Cmd.None)

    case Msg.Insert(value) =>
      val updatedTree = model.bst.insert(value)
      val updatedModel = model.copy(bst = updatedTree).copy(violation = updatedTree.violation(value))
      (updatedModel, updatedModel.refreshTreeCmd |+| PrettyLogger.info(s"$updatedTree") )

    case Msg.ResolveViolation =>
      val updatedModel = model.resolveViolation
      (updatedModel, updatedModel.refreshTreeCmd)

    case Msg.InsertRandom =>
      (model, Cmd.Run(Sync[IO].delay(Random.nextInt(1000)))(Msg.Insert(_)))

    case Msg.ToggleDarkMode =>
      (model.toggleDarkMode, Cmd.None)

    case Msg.GoToInternet(loc) =>
      (model, Nav.loadUrl(loc.url))

    case Msg.NavigateTo(page) =>
      if (page.isSecured) // Tips: check browser console to see the mocked authentication
      then (model, Authentication.authenticate("scala3", "fpLov3rs") |+| page.doNavigate(model))
      else (model, page.doNavigate(model))

    case Msg.DoNavigate(page) =>
      (model.modify(_.currentPage).setTo(page), Nav.pushUrl[IO](page.path) |+| Flowbite.initCmd)

    case Msg.UnhandledRoute(path) =>
      (model, PrettyLogger.error(s"Unhandled route: $path"))

  def view(model: Model): Html[Msg] =
    val pageContent = model.currentPage.render(model)
    MainContainer(pageContent, model.isDarkMode)

  def subscriptions(model: Model): Sub[IO, Msg] = Sub.None
