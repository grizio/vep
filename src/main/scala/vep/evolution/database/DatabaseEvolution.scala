package vep.evolution.database

import java.io.{File, InputStream}
import java.util.Scanner

import com.typesafe.scalalogging.Logger
import scalikejdbc._
import vep.Configuration
import vep.framework.database.DatabaseContainer

import scala.collection.mutable.ListBuffer

case class EvolutionScript(version: Long, script: String)

class DatabaseEvolution(val configuration: Configuration) extends DatabaseContainer {
  private val classLoader = classOf[DatabaseEvolution].getClassLoader
  private val logger = Logger(getClass)
  val configurationKey = "version"

  def setup(): Unit = {
    val currentVersion = fetchCurrentVersion()
    val finalVersion = runEvolutions(currentVersion)
    saveFinalVersion(finalVersion)
  }

  private def fetchCurrentVersion(): Long = withQueryConnection { implicit session =>
    try {
      val maxVersion =
        sql"SELECT value FROM configurations WHERE key = ${configurationKey}"
          .map(resultSet => resultSet.long("value"))
          .single()
          .apply()
      maxVersion.getOrElse(0)
    } catch {
      case _: Exception => 0
    }
  }

  private def runEvolutions(currentVersion: Long): Long = withCommandTransaction { implicit session =>
    logger.info(s"Running evolutions after version ${currentVersion}")
    val scripts = getEvolutionsToRun(currentVersion)
    if (scripts.nonEmpty) {
      scripts.foreach(runEvolution)
      scripts.maxBy(_.version).version
    } else {
      logger.info("No evolution to run")
      currentVersion
    }
  }

  private def getEvolutionsToRun(currentVersion: Long): Seq[EvolutionScript] = {
    val evolutions = ListBuffer[EvolutionScript]()
    var version = currentVersion
    var inputStream: InputStream = null
    do {
      version += 1
      inputStream = classLoader.getResourceAsStream(s"evolution/database/${version}.sql")
      if (inputStream != null) {
        val scanner = new Scanner(inputStream).useDelimiter("\\A")
        val content = if (scanner.hasNext) scanner.next else ""
        evolutions.append(EvolutionScript(version, content))
      }
    } while (inputStream != null)
    evolutions
  }

  private def runEvolution(evolutionScript: EvolutionScript)(implicit session: DBSession): Unit = {
    logger.info(s"Running evolution ${evolutionScript.version}")
    SQL(evolutionScript.script)
      .executeUpdate()
      .apply()
  }

  private def saveFinalVersion(version: Long): Unit = withCommandTransaction { implicit session =>
    if (containsConfigurationEvolution) {
      updateConfigurationValue(version)
    } else {
      insertConfigurationValue(version)
    }
  }

  private def containsConfigurationEvolution(implicit session: DBSession): Boolean = {
    sql"""
      SELECT * FROM configurations
      WHERE key = ${configurationKey}
    """
      .map(_.any("value"))
      .single()
      .apply()
      .nonEmpty
  }

  private def updateConfigurationValue(version: Long)(implicit session: DBSession) = {
    sql"""
        UPDATE configurations
        SET value = ${version}
        WHERE key = ${configurationKey}
      """
      .executeUpdate()
      .apply()
  }

  private def insertConfigurationValue(version: Long)(implicit session: DBSession) = {
    sql"""
        INSERT INTO configurations(key, value) VALUES (${configurationKey}, ${version})
      """
      .executeUpdate()
      .apply()
  }
}
