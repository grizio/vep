package vep.test.service.inmemory

import java.io.File
import java.util.logging.Logger

import anorm.SqlParser._
import anorm._
import com.typesafe.config.ConfigFactory
import vep.controller.VepControllersProductionComponent
import vep.service.{VepServicesComponent, VepServicesProductionComponent}
import vep.utils.DB
import vep.{AnormClient, DBConfig}

import scala.io.{Codec, Source}

trait VepServicesDBInMemoryComponent
  extends VepServicesComponent
  with VepServicesProductionComponent
  with VepControllersProductionComponent
  with AnormClient {
  lazy val logger = Logger.getLogger("vep.test.service.inmemory.VepServicesDBInMemoryComponent")

  private val config = ConfigFactory.load()
  private val environment = "in-memory"

  override lazy val databases = Map(
    "default" ->
      DBConfig(
        config.getString("vep." + environment + ".database.url"),
        config.getString("vep." + environment + ".database.user"),
        config.getString("vep." + environment + ".database.password")
      )
  )

  recreateDB()

  new File(getClass.getResource("/sql/mount").getFile).listFiles sortWith { (f1, f2) =>
    f1.getName.substring(0, f1.getName.indexOf('.')).toInt < f2.getName.substring(0, f2.getName.indexOf('.')).toInt
  } foreach (f => executeFile("/sql/mount/" + f.getName))

  private def executeFile(fileName: String) = {
    logger.info(s"execute file ${fileName}")
    val file = Source.fromURL(getClass.getResource(fileName))(Codec.UTF8)
    file.mkString.split(";").foreach(query => DB.withTransaction { implicit c =>
      if (!query.trim.isEmpty) {
        c.createStatement.execute(query)
      }
    })
  }

  private def recreateDB() = {
    DB.disableFK()

    DB.withTransaction { implicit c =>
      val queryReset = SQL( """SELECT delete_list
      FROM
      (
        SELECT GROUP_CONCAT(table_name) delete_list
        FROM
        (
          SELECT table_schema, table_name
          FROM   information_schema.tables
          WHERE  table_schema = database()
        ) ListOfTables
      ) DeleteParameters""").as(scalar[Option[String]].single)
      queryReset foreach {
        q => SQL("DROP TABLE IF EXISTS " + q).execute()
      }
    }

    DB.enableFK()
  }

  private def cleanDB() = {
    DB.disableFK()

    DB.withTransaction { implicit c =>
      val tables = SQL(
        """SELECT table_name
          | FROM  information_schema.tables
          | WHERE table_schema = database()
          | """.stripMargin).as(scalar[String] *)
      tables foreach {
        table => SQL("TRUNCATE TABLE " + table).execute()
      }
    }

    DB.enableFK()
  }

  def prepareDB(files: String*): Unit = {
    cleanDB()
    files foreach { file =>
      var finalFile = if (file.startsWith("/")) file else "/" + file
      finalFile = if (finalFile.startsWith("/sql")) finalFile else "/sql" + finalFile
      finalFile = if (finalFile.endsWith(".sql")) finalFile else finalFile + ".sql"
      executeFile(finalFile)
    }
  }
}