package vep.router

import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`, `Access-Control-Max-Age`}
import spray.http._
import spray.routing._
import spray.routing.authentication.{BasicAuth, UserPass}
import spray.routing.directives.AuthMagnet
import vep.model.user.User
import vep.service.VepServicesComponent

import scala.concurrent.{ExecutionContext, Future}


/**
 * This trait defines the specification for a router (route) and global directives.
 */
trait VepRouter extends HttpService {
  self: VepServicesComponent =>
  val route: Route

  private val allowOriginHeader = `Access-Control-Allow-Origin`(AllOrigins)
  private val allowHeaders = `Access-Control-Allow-Headers`("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")
  private val controlMaxAge = `Access-Control-Max-Age`(1728000)

  /**
   * Accepts a request from another domain
   */
  def cors[T]: Directive0 = mapRequestContext { ctx =>
    ctx.withRouteResponseHandling({
      //It is an option request for a resource that responds to some other method
      case Rejected(x) if ctx.request.method.equals(HttpMethods.OPTIONS) && x.exists(_.isInstanceOf[MethodRejection]) =>
        val allowedMethods: List[HttpMethod] = x.filter(_.isInstanceOf[MethodRejection]).map(rejection => {
          rejection.asInstanceOf[MethodRejection].supported
        })
        ctx.complete(HttpResponse().withHeaders(
          `Access-Control-Allow-Methods`(HttpMethods.OPTIONS, allowedMethods: _*),
          allowOriginHeader,
          allowHeaders,
          controlMaxAge
        ))
    }).withHttpResponseHeadersMapped { headers =>
      allowOriginHeader :: headers
    }
  }

  /**
   * Authenticator for all users in vep application.
   */
  def vepBasicUserAuthenticator(implicit ec: ExecutionContext): AuthMagnet[User] = {
    def authenticator(userPass: Option[UserPass]): Future[Option[User]] = Future {
      userPass flatMap {
        up => userService.authenticate(up)
      }
    }

    BasicAuth(authenticator _, realm = "Private access")
  }
}