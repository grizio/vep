package vep.app.user.profile

import scalikejdbc._
import vep.app.user._
import vep.app.user.adhesion.AdhesionService
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class ProfileService(
  adhesionService: AdhesionService
) extends DatabaseContainer {
  def findByUser(userId: String): Profile = withQueryConnection { implicit session =>
    findProfileByUser(userId)
  }

  private def findProfileByUser(userId: String)(implicit session: DBSession) = {
    sql"""
      SELECT * FROM users
      WHERE id = ${userId}
    """
      .map(Profile.apply)
      .single()
      .apply()
      .get // Directly from user, it must exists
  }

  def update(profile: Profile, user: User): Validation[Profile] = withCommandTransaction { implicit session =>
    updateProfile(profile, user)
    Valid(profile)
  }

  private def updateProfile(profile: Profile, user: User)(implicit session: DBSession): Unit = {
    val phones = Phone.phoneSeqFormat.write(profile.phones).compactPrint
    sql"""
      UPDATE users
      SET first_name = ${profile.firstName},
          last_name = ${profile.lastName},
          address = ${profile.address},
          zip_code = ${profile.zipCode},
          city = ${profile.city},
          phones = ${phones}
      WHERE id = ${user.id}
    """
      .update()
      .apply()
  }

  def delete(user: User): Validation[Unit] = withCommandTransaction { implicit session =>
    adhesionService.findByUser(user.id)
        .foreach(adhesion => adhesionService.removeAdhesion(adhesion.id))
    deleteUser(user)
    Valid()
  }

  private def deleteUser(user: User)(implicit session: DBSession): Unit = {
    sql"""
      DELETE FROM users
      WHERE id = ${user.id}
    """
      .update()
      .apply()
  }
}
