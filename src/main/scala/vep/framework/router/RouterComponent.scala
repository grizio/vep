package vep.framework.router

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, HttpChallenge, `Content-Type`}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.AuthenticationDirective
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.util.ByteString
import org.mindrot.jbcrypt.BCrypt
import spray.json.{JsString, JsValue, JsonWriter}
import vep.app.common.CommonMessages
import vep.app.user.{User, UserRole, UserService}
import vep.framework.utils.JsonProtocol
import vep.framework.validation.{Invalid, Valid, Validation}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

trait RouterComponent extends JsonProtocol with SprayJsonSupport {
  def route: Route

  def userService: UserService

  implicit def executionContext: ExecutionContext

  protected def publicGet[L](pm: PathMatcher[L]): Directive[L] = {
    get & path(pm)
  }

  protected def publicPost[T](pm: PathMatcher0, um: FromRequestUnmarshaller[T]): Directive1[T] = {
    post & path(pm) & entity(um)
  }

  protected def publicPost[L, T](pm: PathMatcher1[L], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit): Directive[(L, T)] = {
    post & path(pm) & entity(um)
  }

  protected def publicPost[L1, L2, T](pm: PathMatcher[(L1, L2)], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, T)] = {
    post & path(pm) & entity(um)
  }

  protected def flatPublicPost[L1, L2](pm: PathMatcher[(L1, L2)]): Directive[(L1, L2)] = {
    post & path(pm)
  }

  protected def publicPut[T](pm: PathMatcher0, um: FromRequestUnmarshaller[T]): Directive1[T] = {
    put & path(pm) & entity(um)
  }

  protected def publicPut[L, T](pm: PathMatcher1[L], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit): Directive[(L, T)] = {
    put & path(pm) & entity(um)
  }

  protected def publicPut[L1, L2, T](pm: PathMatcher[(L1, L2)], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, T)] = {
    put & path(pm) & entity(um)
  }

  protected def publicPut[L1, L2, L3, T](pm: PathMatcher[(L1, L2, L3)], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit3): Directive[(L1, L2, L3, T)] = {
    put & path(pm) & entity(um)
  }

  protected def publicDelete[L, T](pm: PathMatcher[L]): Directive[L] = {
    delete & path(pm)
  }

  protected def userGet(pm: PathMatcher0): Directive1[User] = {
    publicGet(pm) & onAuthenticated
  }

  protected def userGet[L](pm: PathMatcher1[L])(implicit dummyImplicit: DummyImplicit): Directive[(L, User)] = {
    publicGet(pm) & onAuthenticated
  }

  protected def userGet[L1, L2](pm: PathMatcher[(L1, L2)])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, User)] = {
    publicGet(pm) & onAuthenticated
  }

  protected def userGet[L1, L2, L3](pm: PathMatcher[(L1, L2, L3)])(implicit dummyImplicit: DummyImplicit3): Directive[(L1, L2, L3, User)] = {
    publicGet(pm) & onAuthenticated
  }

  protected def userPost[T](pm: PathMatcher0, um: FromRequestUnmarshaller[T]): Directive[(T, User)] = {
    publicPost(pm, um) & onAuthenticated
  }

  protected def userPost[L, T](pm: PathMatcher1[L], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit): Directive[(L, T, User)] = {
    publicPost(pm, um) & onAuthenticated
  }

  protected def userPost[L1, L2, T](pm: PathMatcher[(L1, L2)], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, T, User)] = {
    publicPost(pm, um) & onAuthenticated
  }

  protected def flatUserPost[L1, L2](pm: PathMatcher[(L1, L2)]): Directive[(L1, L2, User)] = {
    flatPublicPost(pm) & onAuthenticated
  }

  protected def userPut[T](pm: PathMatcher0, um: FromRequestUnmarshaller[T]): Directive[(T, User)] = {
    publicPut(pm, um) & onAuthenticated
  }

  protected def userPut[L, T](pm: PathMatcher1[L], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit): Directive[(L, T, User)] = {
    publicPut(pm, um) & onAuthenticated
  }

  protected def userPut[L1, L2, T](pm: PathMatcher[(L1, L2)], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, T, User)] = {
    publicPut(pm, um) & onAuthenticated
  }

  protected def userPut[L1, L2, L3, T](pm: PathMatcher[(L1, L2, L3)], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit3): Directive[(L1, L2, L3, T, User)] = {
    publicPut(pm, um) & onAuthenticated
  }

  protected def userDelete[T](pm: PathMatcher0): Directive1[User] = {
    publicDelete(pm) & onAuthenticated
  }

  protected def userDelete[L, T](pm: PathMatcher1[L])(implicit dummyImplicit: DummyImplicit): Directive[(L, User)] = {
    publicDelete(pm) & onAuthenticated
  }

  protected def userDelete[L1, L2, T](pm: PathMatcher[(L1, L2)])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, User)] = {
    publicDelete(pm) & onAuthenticated
  }

  protected def userDelete[L1, L2, L3, T](pm: PathMatcher[(L1, L2, L3)])(implicit dummyImplicit: DummyImplicit3): Directive[(L1, L2, L3, User)] = {
    publicDelete(pm) & onAuthenticated
  }

  protected def adminGet(pm: PathMatcher0): Directive1[User] = {
    userGet(pm).filter(_.role == UserRole.admin)
  }

  protected def adminGet[L](pm: PathMatcher1[L])(implicit dummyImplicit: DummyImplicit): Directive[(L, User)] = {
    userGet(pm).tfilter { case (_, user) => user.role == UserRole.admin }
  }

  protected def adminGet[L1, L2](pm: PathMatcher[(L1, L2)])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, User)] = {
    userGet(pm).tfilter { case (_, _, user) => user.role == UserRole.admin }
  }

  protected def adminGet[L1, L2, L3](pm: PathMatcher[(L1, L2, L3)])(implicit dummyImplicit: DummyImplicit3): Directive[(L1, L2, L3, User)] = {
    userGet(pm).tfilter { case (_, _, _, user) => user.role == UserRole.admin }
  }

  protected def adminPost[T](pm: PathMatcher0, um: FromRequestUnmarshaller[T]): Directive[(T, User)] = {
    userPost(pm, um).tfilter { case (_, user) => user.role == UserRole.admin }
  }

  protected def adminPost[L, T](pm: PathMatcher1[L], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit): Directive[(L, T, User)] = {
    userPost(pm, um).tfilter { case (_, _, user) => user.role == UserRole.admin }
  }

  protected def adminPost[L1, L2, T](pm: PathMatcher[(L1, L2)], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, T, User)] = {
    userPost(pm, um).tfilter { case (_, _, _, user) => user.role == UserRole.admin }
  }

  protected def flatAdminPost[L1, L2](pm: PathMatcher[(L1, L2)]): Directive[(L1, L2, User)] = {
    flatUserPost(pm).tfilter { case (_, _, user) => user.role == UserRole.admin }
  }

  protected def adminPut[T](pm: PathMatcher0, um: FromRequestUnmarshaller[T]): Directive[(T, User)] = {
    userPut(pm, um).tfilter { case (_, user) => user.role == UserRole.admin }
  }

  protected def adminPut[L, T](pm: PathMatcher1[L], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit): Directive[(L, T, User)] = {
    userPut(pm, um).tfilter { case (_, _, user) => user.role == UserRole.admin }
  }

  protected def adminPut[L1, L2, T](pm: PathMatcher[(L1, L2)], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, T, User)] = {
    userPut(pm, um).tfilter { case (_, _, _, user) => user.role == UserRole.admin }
  }

  protected def adminPut[L1, L2, L3, T](pm: PathMatcher[(L1, L2, L3)], um: FromRequestUnmarshaller[T])(implicit dummyImplicit: DummyImplicit3): Directive[(L1, L2, L3, T, User)] = {
    userPut(pm, um).tfilter { case (_, _, _, _, user) => user.role == UserRole.admin }
  }

  protected def adminDelete(pm: PathMatcher0): Directive1[User] = {
    userDelete(pm).filter(_.role == UserRole.admin)
  }

  protected def adminDelete[L](pm: PathMatcher1[L])(implicit dummyImplicit: DummyImplicit): Directive[(L, User)] = {
    userDelete(pm).tfilter { case (_, user) => user.role == UserRole.admin }
  }

  protected def adminDelete[L1, L2](pm: PathMatcher[(L1, L2)])(implicit dummyImplicit: DummyImplicit2): Directive[(L1, L2, User)] = {
    userDelete(pm).tfilter { case (_, _, user) => user.role == UserRole.admin }
  }

  protected def adminDelete[L1, L2, L3](pm: PathMatcher[(L1, L2, L3)])(implicit dummyImplicit: DummyImplicit3): Directive[(L1, L2, L3, User)] = {
    userDelete(pm).tfilter { case (_, _, _, user) => user.role == UserRole.admin }
  }

  protected def verifying[A](validation: Validation[A])(innerRoute: A => Route): Route = {
    validation match {
      case Valid(value) => innerRoute(value)
      case invalid: Invalid => ctx => ctx.complete(BadRequest(invalid))
    }
  }

  protected def found[A](elementOpt: Option[A], error: String = CommonMessages.notFound)(innerRoute: A => Route): Route = {
    elementOpt match {
      case Some(value) => innerRoute(value)
      case None => ctx => ctx.complete(NotFound(Invalid(error)))
    }
  }

  protected implicit val jsValueWriter: JsonWriter[JsValue] = identity[JsValue]
  protected implicit val jsStringWriter: JsonWriter[JsString] = identity[JsString]

  protected val Ok = Response(StatusCodes.OK)
  protected val Created = Response(StatusCodes.Created)
  protected val BadRequest = Response(StatusCodes.BadRequest)
  protected val TemporaryRedirect = Response(StatusCodes.TemporaryRedirect)
  protected val Unauthorized = Response(StatusCodes.Unauthorized)
  protected val Forbidden = Response(StatusCodes.Forbidden)
  protected val NotFound = Response(StatusCodes.NotFound)
  protected val InternalServerError = Response(StatusCodes.InternalServerError)

  protected implicit def toRoute(httpResponse: HttpResponse): Route = {
    complete(httpResponse)
  }

  private val httpChallenge = HttpChallenge("auth", "realm")

  private def onAuthenticated(implicit executionContext: ExecutionContext): AuthenticationDirective[User] =
    authenticateOrRejectWithChallenge[User] {
      case Some(credentials) =>
        credentials match {
          case creds: BasicHttpCredentials =>
            verifyBasicAuth(creds) map {
              case Some(user) => Right(user)
              case None => Left(httpChallenge)
            }
          case _ => Future.successful(Left(httpChallenge))
        }
      case None =>
        Future.successful(Left(httpChallenge))
    }

  private def verifyBasicAuth(credentials: BasicHttpCredentials)(implicit executionContext: ExecutionContext): Future[Option[User]] = {
    Future {
      userService
        .findByEmail(credentials.username)
        .filter(_.authentications.exists(authentication => BCrypt.checkpw(credentials.password, authentication.token)))
    }
  }

  class DummyImplicit2

  object DummyImplicit2 {
    implicit def dummyImplicit2: DummyImplicit2 = new DummyImplicit2
  }

  class DummyImplicit3

  object DummyImplicit3 {
    implicit def dummyImplicit3: DummyImplicit3 = new DummyImplicit3
  }
}

class Response private(statusCode: StatusCode) {
  def apply[A](
    entity: A,
    headers: immutable.Seq[HttpHeader] = Nil,
    protocol: HttpProtocol = HttpProtocols.`HTTP/1.1`
  )(implicit jsonWriter: JsonWriter[A]): HttpResponse = {
    headers.find(_.is("content-type")) match {
      case Some(contentType: `Content-Type`) =>
        HttpResponse(statusCode, headers, HttpEntity(contentType.contentType, ByteString(entity.toString)), protocol)
      case _ =>
        HttpResponse(statusCode, headers, HttpEntity(ContentTypes.`application/json`, jsonWriter.write(entity).compactPrint), protocol)
    }
  }

  def apply(): HttpResponse = HttpResponse(statusCode)
}

object Response {
  def apply(statusCode: StatusCode): Response = new Response(statusCode)
}
