package vep

import com.typesafe.config.ConfigFactory

case class ServerConfiguration(
  protocol: String,
  host: String,
  port: Int
)

case class DatabaseConfiguration(
  host: String,
  port: Int,
  username: String,
  password: String,
  name: String
)

case class EmailConfiguration(
  tls: Boolean,
  ssl: Boolean,
  port: Int,
  host: String,
  email: String,
  user: String,
  password: String,
  replyTo: String
)

object Environment extends Enumeration {
  val dev, prod = Value

  def fromString(value: String): Environment.Value = {
    values.find(_.toString == value)
      .getOrElse(prod)
  }
}

class Configuration {
  private val config = ConfigFactory.load()

  lazy val environment: Environment.Value = Environment.fromString(config.getString("vep.environment"))

  lazy val server = ServerConfiguration(
    protocol = config.getString("vep.server.protocol"),
    host = config.getString("vep.server.host"),
    port = config.getInt("vep.server.port")
  )

  lazy val database = DatabaseConfiguration(
    host = config.getString("vep.database.host"),
    port = config.getInt("vep.database.port"),
    username = config.getString("vep.database.username"),
    password = config.getString("vep.database.password"),
    name = config.getString("vep.database.name")
  )

  lazy val email = EmailConfiguration(
    tls = config.getBoolean("vep.email.tls"),
    ssl = config.getBoolean("vep.email.ssl"),
    port = config.getInt("vep.email.port"),
    host = config.getString("vep.email.host"),
    email = config.getString("vep.email.email"),
    user = config.getString("vep.email.username"),
    password = config.getString("vep.email.password"),
    replyTo = config.getString("vep.email.replyTo")
  )

  lazy val baseUrl: String = {
    val port = environment match {
      case Environment.dev => ":" + server.port
      case _ => ""
    }
    s"${server.protocol}://${server.host}${port}"
  }
}
