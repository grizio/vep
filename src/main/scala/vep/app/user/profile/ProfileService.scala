package vep.app.user.profile

import scalikejdbc._
import spray.json.{JsArray, JsonParser}
import vep.app.user._
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class ProfileService() extends DatabaseContainer {
  import ProfileService._

  def findByUser(userId: String): Profile = withQueryConnection { implicit session =>
    findProfileByUser(userId)
  }

  private def findProfileByUser(userId: String)(implicit session: DBSession) = {
    sql"""
      SELECT * FROM users
      WHERE id = ${userId}
    """
      .map(toProfile)
      .single()
      .apply()
      .get // Directly from user, it must exists
  }

  def update(profile: Profile, user: User): Validation[Profile] = withCommandTransaction { implicit session =>
    updateProfile(profile, user)
    Valid(profile)
  }

  private def updateProfile(profile: Profile, user: User)(implicit session: DBSession): Unit = {
    val phones = JsArray(profile.phones.map(Phone.PhoneFormat.write): _*).compactPrint
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
}

object ProfileService {
  def toProfile(rs: WrappedResultSet): Profile = new Profile(
    email = rs.stringOpt("email").getOrElse(""),
    firstName = rs.stringOpt("first_name").getOrElse(""),
    lastName = rs.stringOpt("last_name").getOrElse(""),
    address = rs.stringOpt("address").getOrElse(""),
    zipCode = rs.stringOpt("zip_code").getOrElse(""),
    city = rs.stringOpt("city").getOrElse(""),
    phones = rs.stringOpt("phones").map(phones => JsonParser(phones).asInstanceOf[JsArray].elements.map(Phone.PhoneFormat.read).toList).getOrElse(List.empty)
  )
}