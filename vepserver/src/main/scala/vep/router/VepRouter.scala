package vep.router

import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`, `Access-Control-Max-Age`}
import spray.http._
import spray.routing._
import vep.model.common.Result


trait VepRouter extends HttpService {
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


  import spray.httpx.SprayJsonSupport._
  import vep.model.common.ResultImplicits._

  def processResult(result: Result)(implicit ctx: RequestContext) = {
    val status = if (result.isValid) StatusCodes.OK else StatusCodes.BadRequest
    ctx.complete(status, result)
  }
}