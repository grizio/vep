package vep.framework.router

import akka.http.scaladsl.server._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller

trait RouterComponent {
  def publicGet[L](pm: PathMatcher[L]): Route = ???

  def publicPost[L, T](pm: PathMatcher[L], um: FromRequestUnmarshaller[T]): Route = ???

  def publicPut[L, T](pm: PathMatcher[L], um: FromRequestUnmarshaller[T]): Route = ???

  def publicDelete[L, T](pm: PathMatcher[L]): Route = ???

  def userGet[L](pm: PathMatcher[L]): Route = ???

  def userPost[L, T](pm: PathMatcher[L], um: FromRequestUnmarshaller[T]): Route = ???

  def userPut[L, T](pm: PathMatcher[L], um: FromRequestUnmarshaller[T]): Route = ???

  def userDelete[L, T](pm: PathMatcher[L]): Route = ???

  def adminGet[L](pm: PathMatcher[L]): Route = ???

  def adminPost[L, T](pm: PathMatcher[L], um: FromRequestUnmarshaller[T]): Route = ???

  def adminPut[L, T](pm: PathMatcher[L], um: FromRequestUnmarshaller[T]): Route = ???

  def adminDelete[L, T](pm: PathMatcher[L]): Route = ???
}
