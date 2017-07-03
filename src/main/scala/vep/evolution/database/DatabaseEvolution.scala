package vep.evolution.database

import java.io.File
import java.nio.file.Files

import com.typesafe.scalalogging.Logger
import scalikejdbc._
import vep.Configuration
import vep.service.DatabaseContainer
import vep.utils.{CollectionUtils, NumberUtils}

case class EvolutionFile(version: Long, file: File)

class DatabaseEvolution(val configuration: Configuration) extends DatabaseContainer {
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
        sql"SELECT value FROM configurations WHERE key = $configurationKey"
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
    val directory = new File(getClass.getResource("/evolution/database").getPath)
    if (directory.exists()) {
      val files = getEvolutionsToRun(directory, currentVersion)
      if (files.nonEmpty) {
        files.foreach(evolutionFile => runEvolution(evolutionFile.file))
        files.maxBy(_.version).version
      } else {
        logger.info("No evolution to run")
        currentVersion
      }
    } else {
      logger.warn("Resource directory evolution/database not found")
      currentVersion
    }
  }

  private def getEvolutionsToRun(directory: File, currentVersion: Long): Seq[EvolutionFile] = {
    def nameWithoutExtension(file: File): String = file.getName.substring(0, file.getName.length - ".sql".length)

    val files = CollectionUtils.scalaSeq(directory.listFiles())
      .filter(_.getName.endsWith(".sql"))
      .flatMap { file =>
        NumberUtils.toLongOpt(nameWithoutExtension(file)) match {
          case Some(version) =>
            Some(EvolutionFile(version, file))
          case None =>
            logger.warn(s"File ${file.getName} is not a valid name. It will be ignored")
            None
        }
      }
      .filter(_.version > currentVersion)
    files.sortBy(_.version)
  }

  private def runEvolution(file: File)(implicit session: DBSession): Unit = {
    logger.info(s"Running evolution ${file.getName}")
    SQL(CollectionUtils.scalaSeq(Files.readAllLines(file.toPath)).mkString("\n"))
      .executeUpdate()
      .apply()
  }

  private def saveFinalVersion(version: Long): Unit = withCommandTransaction { implicit session =>
    sql"""
      INSERT INTO configurations AS c (key, value) VALUES ($configurationKey, $version)
      ON CONFLICT (key) DO UPDATE
      SET value = $version
      WHERE c.key = $configurationKey
    """
      .executeUpdate()
      .apply()
  }
}
