package vep.app.user.adhesion

import scalikejdbc._
import vep.Configuration
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}
import java.util.UUID

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

  def findOpenedPeriods(): Seq[PeriodAdhesion] = withQueryConnection { implicit session =>
    findOpenedPeriodsAdhesion()
  }

  private def findOpenedPeriodsAdhesion()(implicit session: DBSession): Seq[PeriodAdhesion] = {
    sql"""
      SELECT * FROM period_adhesion
      WHERE current_timestamp BETWEEN startRegistration and endRegistration
    """
      .map(PeriodAdhesion.apply)
      .list()
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

  def createAdhesion(adhesion: Adhesion, period: PeriodAdhesion): Validation[Adhesion] = withCommandTransaction { implicit session =>
    insertAdhesion(adhesion, period)
    adhesion.members.foreach(insertAdhesionMember(_, adhesion))
    Valid(adhesion)
  }

  private def insertAdhesion(adhesion: Adhesion, period: PeriodAdhesion)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO adhesion(id, period, user_id, accepted)
      VALUES (${adhesion.id}, ${period.id}, ${adhesion.user}, ${adhesion.accepted})
    """
      .execute()
      .apply()
  }

  private def insertAdhesionMember(member: AdhesionMember, adhesion: Adhesion)(implicit session: DBSession): Unit = {
    val id = UUID.randomUUID().toString
    sql"""
      INSERT INTO adhesion_member(id, adhesion, first_name, last_name, birthday, activity)
      VALUES (${id}, ${adhesion.id}, ${member.firstName}, ${member.lastName}, ${member.birthday}, ${member.activity})
    """
      .execute()
      .apply()
  }
}
