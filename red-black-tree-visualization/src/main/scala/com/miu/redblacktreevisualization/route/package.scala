package com.miu.redblacktreevisualization

import com.miu.redblacktreevisualization.page.*
import scala.util.Try

package object route {
  object Route {
    object Home:
      def unapply(path: String): Option[Unit] = if path == "/" then Some(()) else None
  }

}
