import sbtwelcome._
import scala.Console._

/**
 * Logo generated on [[https://patorjk.com]]
 */

logo :=
  s"""
     |      ████████╗██╗   ██╗██████╗ ██╗ █████╗ ███╗   ██╗
     |      ╚══██╔══╝╚██╗ ██╔╝██╔══██╗██║██╔══██╗████╗  ██║
     |         ██║    ╚████╔╝ ██████╔╝██║███████║██╔██╗ ██║
     |         ██║     ╚██╔╝  ██╔══██╗██║██╔══██║██║╚██╗██║
     |         ██║      ██║   ██║  ██║██║██║  ██║██║ ╚████║
     |         ╚═╝      ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝
     |  
     |Version : ${version.value}
     |
     |$YELLOW Scala ${scalaVersion.value}$RESET
     | 
     |""".stripMargin

usefulTasks := Seq(
  UsefulTask("~fastLinkJS", "Auto re-compile when source code changes detected").alias("watch"),
  UsefulTask("redblackTreeCoreJVM/test", s"test red black tree core").alias("rtest"),
  UsefulTask("webpackDevServer", s"Start Webpack dev server").alias("dev"),
  UsefulTask("publishDist", "Build static web artifact").alias("pd"),
  UsefulTask("Docker/publishLocal", "Publish locally web app as a docker image").alias("dpl"),
  UsefulTask("Docker/publish", "Publish web app as a docker image to remote container registry").alias("dp"),
  UsefulTask("welcome", "Menu").alias("h")
)

logoColor := MAGENTA
