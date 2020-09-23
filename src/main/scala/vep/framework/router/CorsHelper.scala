package vep.framework.router

import akka.http.scaladsl.model.{HttpMethods, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, handleRejections}
import akka.http.scaladsl.server._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings

object CorsHelper {
  private val corsSettings = CorsSettings.defaultSettings
    .withAllowedMethods(CorsSettings.defaultSettings.allowedMethods ++ Seq(HttpMethods.PUT, HttpMethods.DELETE))

  def withCors(route: Route): Route = {
    cors(corsSettings) {
      handleRejections(rejectionHandler) {
        route
      }
    }
  }

  private def rejectionHandler = RejectionHandler.newBuilder().handle {
    case MalformedRequestContentRejection(message, _) =>
      complete(HttpResponse(
        status = StatusCodes.BadRequest,
        entity = message
      ))
    case UnsupportedRequestContentTypeRejection(_) =>
      complete(HttpResponse(
        status = StatusCodes.BadRequest,
        entity = "Please provide a valid JSON value"
      ))
    case MalformedFormFieldRejection(fieldName, errorMsg, _) =>
      complete(HttpResponse(
        status = StatusCodes.BadRequest,
        entity = s"Malformed field: $fieldName (info: $errorMsg)"
      ))
    case MissingFormFieldRejection(fieldName) =>
      complete(HttpResponse(
        status = StatusCodes.BadRequest,
        entity = s"Missing field: $fieldName"
      ))
    case AuthenticationFailedRejection(cause, challenge) =>
      complete(HttpResponse(
        status = StatusCodes.Unauthorized,
        entity = StatusCodes.Unauthorized.defaultMessage
      ))
    case AuthorizationFailedRejection =>
      complete(HttpResponse(
        status = StatusCodes.Forbidden,
        entity = StatusCodes.Forbidden.defaultMessage
      ))
  }.result()
}
