package vep.app.user.adhesion

import scalikejdbc._
import vep.Configuration
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class AdhesionService(
  val configuration: Configuration
) extends DatabaseContainer {
  def findAllPeriods(): Seq[PeriodAdhesion] = withQueryConnection { implicit session =>
    findAllPeriodsAdhesion()
  }

  private def findAllPeriodsAdhesion()(implicit session: DBSession): Seq[PeriodAdhesion] = {
    sql"""
      SELECT * FROM period_adhesion
    """
      .map(PeriodAdhesion.apply)
      .list()
      .apply()
  }

  def findPeriod(id: String): Option[PeriodAdhesion] = withQueryConnection { implicit session =>
    findPeriodAdhesion(id)
  }

  private def findPeriodAdhesion(id: String)(implicit session: DBSession): Option[PeriodAdhesion] = {
    sql"""
      SELECT * FROM period_adhesion
      WHERE id = ${id}
    """
      .map(PeriodAdhesion.apply)
      .single()
      .apply()
  }

  def createPeriod(periodAdhesion: PeriodAdhesion): Validation[PeriodAdhesion] = withCommandTransaction { implicit session =>
    createPeriodAdhesion(periodAdhesion)
    Valid(periodAdhesion)
  }

  private def createPeriodAdhesion(periodAdhesion: PeriodAdhesion)(implicit session: DBSession): Unit = {
    val activities = PeriodAdhesion.activitiesFormat.write(periodAdhesion.activities).compactPrint
    sql"""
      INSERT INTO period_adhesion(id, startPeriod, endPeriod, startRegistration, endRegistration, activities)
      VALUES (
        ${periodAdhesion.id},
        ${periodAdhesion.period.start},
        ${periodAdhesion.period.end},
        ${periodAdhesion.registrationPeriod.start},
        ${periodAdhesion.registrationPeriod.end},
        ${activities}
      )
    """
      .execute()
      .apply()
  }

  def updatePeriod(periodAdhesion: PeriodAdhesion): Validation[PeriodAdhesion] = withCommandTransaction { implicit session =>
    updatePeriodAdhesion(periodAdhesion)
    Valid(periodAdhesion)
  }

  private def updatePeriodAdhesion(periodAdhesion: PeriodAdhesion)(implicit session: DBSession): Unit = {
    val activities = PeriodAdhesion.activitiesFormat.write(periodAdhesion.activities).compactPrint
    sql"""
      UPDATE period_adhesion
      SET startPeriod = ${periodAdhesion.period.start},
          endPeriod = ${periodAdhesion.period.end},
          startRegistration = ${periodAdhesion.registrationPeriod.start},
          endRegistration = ${periodAdhesion.registrationPeriod.end},
          activities = ${activities}
      WHERE id = ${periodAdhesion.id}
    """
      .execute()
      .apply()
  }
}
