package vep.router

import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`, `Access-Control-Max-Age`}
import spray.http._
import spray.routing
import spray.routing._
import spray.routing.authentication.{BasicAuth, UserPass}
import vep.model.user.User
import vep.service.VepServicesComponent
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


trait VepRouter extends HttpService {
  self: VepServicesComponent =>
  val route: Route

  private val allowOriginHeader = `Access-Control-Allow-Origin`(AllOrigins)
  private val allowHeaders = `Access-Control-Allow-Headers`("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")
  private val controlMaxAge = `Access-Control-Max-Age`(1728000)

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

  def vepUserPassAuthenticator(userPass: Option[UserPass]): Future[Option[User]] = Future {
    userPass flatMap {
      up => userService.authenticate(up)
    }
  }

  def vepBasicAuth = BasicAuth(vepUserPassAuthenticator _, realm = "Private access")
}