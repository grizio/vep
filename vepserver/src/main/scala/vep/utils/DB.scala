package vep.utils

import java.sql.{DriverManager, Connection}

import vep.AnormClient
import vep.exception.InvalidDatabaseException

object DB {
  def getConnection(dbName: String)(implicit anormClient: AnormClient): Connection = {
    anormClient.databases.get(dbName) match {
      case Some(dbConfig) => DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password)
      case None => throw new InvalidDatabaseException(dbName)
    }
  }

  def withConnection[A](dbName: String = "default")(block: Connection => A)(implicit anormClient: AnormClient): A = {
    val connection = getConnection(dbName)
    try {
      block(connection)
    } finally {
      connection.close()
    }
  }

  def withConnection[A](block: Connection => A)(implicit anormClient: AnormClient): A = withConnection()(block)

  def withTransaction[A](dbName: String = "default")(block: Connection => A)(implicit anormClient: AnormClient): A = {
    withConnection(dbName) { connection =>
      try {
        connection.setAutoCommit(false)
        val r = block(connection)
        connection.commit()
        r
      } catch {
        case e: Throwable => connection.rollback(); throw e
      }
    }
  }

  def withTransaction[A](block: Connection => A)(implicit anormClient: AnormClient): A = withTransaction()(block)
}
