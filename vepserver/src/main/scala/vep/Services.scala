package vep

import anorm._
import com.typesafe.config.ConfigFactory
import vep.controller.VepControllersProductionComponent
import vep.service.{VepServicesComponent, VepServicesProductionComponent}
import vep.utils.DB

case class DBConfig(url: String, user: String, password: String)

trait AnormClient {
  val databases: Map[String, DBConfig]
  implicit val _selfAnormClient = this
}

/**
 * This trait defines all services with all required components.
 */
trait FinalVepServicesProductionComponent
  extends VepServicesComponent
  with VepServicesProductionComponent
  with VepControllersProductionComponent
  with AnormClient {

  private val config = ConfigFactory.load()
  private val environment = config.getString("vep.environment")

  override lazy val databases = Map(
    "default" ->
      DBConfig(
        config.getString("vep." + environment + ".database.url"),
        config.getString("vep." + environment + ".database.user"),
        config.getString("vep." + environment + ".database.password")
      )
  )
}