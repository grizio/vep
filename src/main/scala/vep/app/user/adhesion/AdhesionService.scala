package vep.app.user.adhesion

import java.util.UUID

import scalikejdbc._
import spray.json.{JsArray, JsString, JsonParser}
import vep.app.common.types.Period
import vep.app.user.UserService
import vep.framework.database.DatabaseContainer
import vep.framework.utils.DateUtils
import vep.framework.validation.{Valid, Validation}

class AdhesionService(
  userService: UserService
) extends DatabaseContainer {
  import AdhesionService._

  def findAllPeriods(): Seq[PeriodAdhesion] = withQueryConnection { implicit session =>
    findAllPeriodsAdhesion()
  }

  private def findAllPeriodsAdhesion()(implicit session: DBSession): Seq[PeriodAdhesion] = {
    sql"""
      SELECT * FROM period_adhesion
    """
      .map(toPeriodAdhesion)
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
      .map(toPeriodAdhesion)
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
      .map(toPeriodAdhesion)
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
      .map(toAdhesionEntry)
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
      .map(toAdhesionEntry)
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
      .map(toAdhesionEntry)
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
      .map(toAdhesionMember)
      .list()
      .apply()
  }

  def createPeriod(periodAdhesion: PeriodAdhesion): Validation[PeriodAdhesion] = withCommandTransaction { implicit session =>
    createPeriodAdhesion(periodAdhesion)
    Valid(periodAdhesion)
  }

  private def createPeriodAdhesion(periodAdhesion: PeriodAdhesion)(implicit session: DBSession): Unit = {
    val activities = JsArray(periodAdhesion.activities.map(JsString(_)): _*).compactPrint
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
    val activities = JsArray(periodAdhesion.activities.map(JsString(_)): _*).compactPrint
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

object AdhesionService {
  def toPeriodAdhesion(resultSet: WrappedResultSet): PeriodAdhesion = {
    PeriodAdhesion(
      id = resultSet.string("id"),
      period = Period(
        start = DateUtils.fromDateTime(resultSet.jodaDateTime("startPeriod")),
        end = DateUtils.fromDateTime(resultSet.jodaDateTime("endPeriod"))
      ),
      registrationPeriod = Period(
        start = DateUtils.fromDateTime(resultSet.jodaDateTime("startRegistration")),
        end = DateUtils.fromDateTime(resultSet.jodaDateTime("endRegistration"))
      ),
      activities = resultSet.stringOpt("activities").map(activities => JsonParser(activities).asInstanceOf[JsArray].elements.collect { case JsString(v) => v }.toList).getOrElse(List.empty)
    )
  }

  def toAdhesionMember(resultSet: WrappedResultSet): AdhesionMember = {
    AdhesionMember(
      firstName = resultSet.string("first_name"),
      lastName = resultSet.string("last_name"),
      birthday = DateUtils.fromDateTime(resultSet.jodaDateTime("birthday")),
      activity = resultSet.string("activity")
    )
  }

  def toAdhesionEntry(resultSet: WrappedResultSet): AdhesionEntry = {
    AdhesionEntry(
      id = resultSet.string("id"),
      user = resultSet.string("user_id"),
      period = resultSet.string("period"),
      accepted = resultSet.boolean("accepted")
    )
  }
}