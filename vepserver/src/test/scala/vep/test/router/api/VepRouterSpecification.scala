package vep.test.router.api

import akka.actor.ActorRefFactory
import spray.http.BasicHttpCredentials
import spray.testkit.Specs2RouteTest
import vep.router.VepApiRouter
import vep.test.controller.VepControllersInMemoryComponent

trait VepRouterSpecification extends Specs2RouteTest with VepApiRouter with VepControllersInMemoryComponent {
  override def actorRefFactory: ActorRefFactory = system
  lazy val validCredentialsUser = BasicHttpCredentials("abc@def.com", "abcd")
  lazy val validCredentialsAdmin = BasicHttpCredentials("admin@admin.com", "admin")
  lazy val invalidCredentials = BasicHttpCredentials("abc@def.com", "unknown")
}
