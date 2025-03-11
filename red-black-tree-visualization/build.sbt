import sbt.io.Path.relativeTo
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

lazy val webpackDevServer = taskKey[Unit]("Start the dev server. It should be opened in a separate terminal")
lazy val publishDist      = taskKey[Unit]("Build a static web artifact")

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin, DockerPlugin)
  .settings(
    organization := "com.miu",
    name         := "red-black-tree-visualization",
    version      := "0.1.0",
    scalaVersion := "3.6.2",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    scalaJSUseMainModuleInitializer := true,
    // Source maps seem to be broken with bundler
    Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    libraryDependencies ++= Seq(
      "io.indigoengine"            %%% "tyrian-io"                 % Dependencies.Tyrian,
      "com.softwaremill.quicklens" %%% "quicklens"                 % Dependencies.Quicklens,
      ("org.scala-js"              %%% "scalajs-java-securerandom" % Dependencies.JavaSecureRandom).cross(CrossVersion.for3Use2_13)
    )
  )
  .settings(
    dockerBaseImage       := "nginx:stable-perl",
    Docker / publish      := (Docker / publish).dependsOn(Compile / fullLinkJS).value,
    Docker / publishLocal := (Docker / publishLocal).dependsOn(Compile / fullLinkJS).value,
    dockerExposedPorts    := Seq(80),
    dockerRepository      := Some(DockerSettings.repository),
    DockerSettings.x86ArchSetting,
    Docker / defaultLinuxInstallLocation := "/usr/share/nginx/html",
    dockerCommands := dockerCommands.value.filter {
      case ExecCmd(cmd, _) => cmd != "ENTRYPOINT" && cmd != "CMD" && cmd != "USER"
      case Cmd(cmd, _)     => cmd != "USER" && cmd != "RUN"
      case _               => true

    } ++ Seq(Cmd("COPY", "nginx.conf", "/etc/nginx/nginx.conf"), Cmd("CMD", """["nginx", "-g", "daemon off;"]""")),
    Docker / mappings ++= {
      publishDist.value
      val frontendDist    = baseDirectory.value / "dist"
      val nginxConfigFile = baseDirectory.value / "nginx.conf"

      (frontendDist ** "*").get.map { file =>
        file -> s"/usr/share/nginx/html/${frontendDist.relativize(file).get.getPath}"
      } :+ (nginxConfigFile -> "/nginx.conf")

    }
  )
  .settings(
    webpackDevServer := {
      CLIUtils.startFrontendDevServer("red-black-tree-visualization", scalaVersion.value)
    },
    publishDist := {
      (Compile / fullLinkJS).value
      CLIUtils.buildFrontend()
    }
  )
  .dependsOn(redblackTreeCore.js)

lazy val redblackTreeCore = (crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure) in file("redblack-tree-core"))
  .enablePlugins(ScalaJSPlugin, DockerPlugin)
  .settings(
    name                                    := "red-black-tree-core",
    scalaVersion                            := "3.6.2",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.19" % Test
  )
