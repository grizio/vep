package vep.app.user.adhesion

import scalikejdbc._
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}
import java.util.UUID

import vep.app.user.{User, UserService}

class AdhesionService(
  userService: UserService
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

  def findByPeriod(periodId: String): Seq[AdhesionView] = withQueryConnection { implicit session =>
    findAdhesionsByPeriod(periodId)
      .flatMap(adhesionEntryToAdhesionView(_))
  }

  private def findAdhesionsByPeriod(periodId: String)(implicit session: DBSession): Seq[AdhesionEntry] = {
    sql"""
      SELECT * FROM adhesion
      WHERE period = ${periodId}
    """
      .map(AdhesionEntry.apply)
      .list()
      .apply()
  }

  def findByUser(userId: String): Seq[AdhesionView] = withQueryConnection { implicit session =>
    findAdhesionEntriesByUser(userId)
      .flatMap(adhesionEntryToAdhesionView(_))
  }

  private def findAdhesionEntriesByUser(userId: String)(implicit session: DBSession): Seq[AdhesionEntry] = {
    sql"""
      SELECT * FROM adhesion
      WHERE user_id = ${userId}
    """
      .map(AdhesionEntry.apply)
      .list()
      .apply()
  }

  def findAdhesionByPeriod(period: PeriodAdhesion, id: String): Option[AdhesionView] = withQueryConnection { implicit session =>
    findAdhesionEntryByPeriod(period, id)
      .flatMap(adhesionEntryToAdhesionView)
  }

  private def findAdhesionEntryByPeriod(period: PeriodAdhesion, id: String)(implicit session: DBSession): Option[AdhesionEntry] = {
    sql"""
      SELECT * FROM adhesion
      WHERE period = ${period.id}
      AND   id = ${id}
    """
      .map(AdhesionEntry.apply)
      .single()
      .apply()
  }

  private def adhesionEntryToAdhesionView(adhesionEntry: AdhesionEntry)(implicit session: DBSession): Option[AdhesionView] = {
    findPeriod(adhesionEntry.period).flatMap { period =>
      userService.findView(adhesionEntry.user).map { user =>
        AdhesionView(
          id = adhesionEntry.id,
          period = period,
          user = user,
          accepted = adhesionEntry.accepted,
          members = findMembersByAdhesion(adhesionEntry.id)
        )
      }
    }
  }

  private def findMembersByAdhesion(adhesionId: String)(implicit session: DBSession): Seq[AdhesionMember] = {
    sql"""
      SELECT * FROM adhesion_member
      WHERE adhesion = ${adhesionId}
    """
      .map(AdhesionMember.apply)
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

  def acceptAdhesion(id: String): Validation[Unit] = withCommandTransaction { implicit session =>
    updateAdhesionToAccepted(id)
    Valid()
  }

  private def updateAdhesionToAccepted(id: String)(implicit session: DBSession) = {
    sql"""
      UPDATE adhesion
      SET accepted = TRUE
      WHERE id = ${id}
    """
      .execute()
      .apply()
  }

  def removeAdhesion(id: String): Validation[Unit] = withCommandTransaction { implicit session =>
    deleteMembersFromAdhesion(id)
    deleteAdhesion(id)
    Valid()
  }

  private def deleteAdhesion(id: String)(implicit session: DBSession) = {
    sql"""
      DELETE FROM adhesion
      WHERE id = ${id}
    """
      .execute()
      .apply()
  }

  private def deleteMembersFromAdhesion(id: String)(implicit session: DBSession) = {
    sql"""
      DELETE FROM adhesion_member
      WHERE adhesion = ${id}
    """
      .execute()
      .apply()
  }
}
