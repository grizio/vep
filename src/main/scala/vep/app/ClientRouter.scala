package vep.app

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

class ClientRouter {
  private val classLoader = classOf[ClientRouter].getClassLoader

  def route: Route = encodeResponse {
    index ~ assets ~ fallbackIndex
  }

  private def index = {
    pathEndOrSingleSlash {
      getFromResource("front/index.html")
    }
  }


  private def assets = {
    path(Segments) { segments =>
      val resourceName = s"front/${segments.mkString("/")}"
      Option(classLoader.getResource(resourceName)) match {
        case Some(_) => getFromResource(resourceName)
        case _ => reject
      }
    }
  }

  private def fallbackIndex = {
    path(Segments) { _ =>
      getFromResource("front/index.html")
    }
  }
}
