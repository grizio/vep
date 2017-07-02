package vep

import com.typesafe.config.ConfigFactory

case class ServerConfiguration(
  host: String,
  port: Int
)

case class DatabaseConfiguration(
  host: String,
  username: String,
  password: String,
  name: String
)

class Configuration {
  private val config = ConfigFactory.load()

  lazy val server = ServerConfiguration(
    host = config.getString("vep.server.host"),
    port = config.getInt("vep.server.port")
  )

  lazy val database = DatabaseConfiguration(
    host = config.getString("vep.database.host"),
    username = config.getString("vep.database.username"),
    password = config.getString("vep.database.password"),
    name = config.getString("vep.database.name")
  )
}
