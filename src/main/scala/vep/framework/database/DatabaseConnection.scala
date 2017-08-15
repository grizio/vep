package vep.framework.database

import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}
import vep.Configuration

trait DatabaseConnection {
  def configuration: Configuration

  Class.forName("org.postgresql.Driver")
  ConnectionPool.singleton(
    s"jdbc:postgresql://${configuration.database.host}:${configuration.database.port}/${configuration.database.name}",
    configuration.database.username,
    configuration.database.password,
    ConnectionPoolSettings(
      initialSize = 0,
      maxSize = 3
    )
  )
}
