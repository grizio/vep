package vep

import akka.actor.Actor
import spray.routing.HttpService
import vep.router.{VepApiRouter, VepRouter, VepWebRouter}
import vep.service.VepServicesProductionComponent

/**
 * Base actor
 */
trait VepActor extends Actor with FinalVepServicesProductionComponent {
  self: HttpService with VepRouter =>

  // The HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  override def actorRefFactory = context

  // This actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  override def receive = runRoute(route)
}

/**
 * Actor for web route
 */
class VepWebActor extends VepActor with VepWebRouter

/**
 * Actor for api route
 */
class VepApiActor extends VepActor with VepApiRouter