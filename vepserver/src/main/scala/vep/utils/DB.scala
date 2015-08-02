package vep.utils

import java.sql.{DriverManager, Connection}

import vep.AnormClient
import vep.exception.InvalidDatabaseException
import anorm._

/**
 * This class defines some utility methods for transaction management.
 * This class is an adaptation of DB object from play framework (see play.api.db.DB).
 */
object DB {
  /**
   * Gets the connection from database
   * @param dbName the database name
   * @param anormClient The anorm client
   * @return The connection
   */
  def getConnection(dbName: String)(implicit anormClient: AnormClient): Connection = {
    anormClient.databases.get(dbName) match {
      case Some(dbConfig) => DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password)
      case None => throw new InvalidDatabaseException(dbName)
    }
  }

  /**
   * Processes the given block with a connection.
   * @param dbName The database name
   * @param block The block to process
   * @param anormClient The anorm client
   * @return The result of the block
   */
  def withConnection[A](dbName: String = "default")(block: Connection => A)(implicit anormClient: AnormClient): A = {
    val connection = getConnection(dbName)
    try {
      block(connection)
    } finally {
      connection.close()
    }
  }

  /**
   * Processes the given block with a connection (default database).
   * @param block The block to process
   * @param anormClient The anorm client
   * @return The result of the block
   */
  def withConnection[A](block: Connection => A)(implicit anormClient: AnormClient): A = withConnection()(block)

  /**
   * Processes the given block with a connection in only one transaction.
   * @param dbName The database name
   * @param block The block to process
   * @param anormClient The anorm client
   * @return The result of the block
   */
  def withTransaction[A](dbName: String = "default")(block: Connection => A)(implicit anormClient: AnormClient): A = {
    withConnection(dbName) { connection =>
      try {
        connection.setAutoCommit(false)
        val r = block(connection)
        connection.commit()
        r
      } catch {
        case e: Throwable =>
          connection.rollback()
          println("ERROR SQL:" + e.getMessage)
          throw e
      }
    }
  }

  /**
   * Processes the given block with a connection in only one transaction (default database).
   * @param block The block to process
   * @param anormClient The anorm client
   * @return The result of the block
   */
  def withTransaction[A](block: Connection => A)(implicit anormClient: AnormClient): A = withTransaction()(block)

  /**
   * Disables all foreign key constraints in database.
   *
   * @param dbName The database name
   * @param anormClient The anorm client
   */
  def disableFK(dbName: String = "default")(implicit anormClient: AnormClient): Unit = {
    withTransaction(dbName) { implicit c =>
      SQL("SET FOREIGN_KEY_CHECKS = 0")
    }
  }

  /**
   * Disables all foreign key constraints in default database.
   *
   * @param anormClient The anorm client
   */
  def disableFK(implicit anormClient: AnormClient): Unit = disableFK()

  /**
   * Enables all foreign key constraints in database.
   *
   * @param dbName The databasename
   * @param anormClient The anorm client
   */
  def enableFK(dbName: String = "default")(implicit anormClient: AnormClient): Unit = {
    withTransaction(dbName) { implicit c =>
      SQL("SET FOREIGN_KEY_CHECKS = 1")
    }
  }

  /**
   * Enables all foreign key constraints in default database.
   *
   * @param anormClient The anorm client
   */
  def enableFK(implicit anormClient: AnormClient): Unit = enableFK()
}
