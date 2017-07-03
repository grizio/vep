package vep.service

import scalikejdbc.{ConnectionPool, DB, DBSession}
import vep.Configuration

trait DatabaseContainer {
  def configuration: Configuration

  Class.forName("org.postgresql.Driver")
  ConnectionPool.singleton(
    s"jdbc:postgresql://${configuration.database.host}:${configuration.database.port}/${configuration.database.name}",
    configuration.database.username,
    configuration.database.password
  )

  def withQueryConnection[A](block: DBSession => A): A = {
    DB.readOnly(block)
  }

  def withCommandTransaction[A](block: DBSession => A): A = {
    DB.localTx(block)
  }
}
