package vep.model.common

object Roles {
  val user = "user"
  val userManager = "user-manager"

  lazy val acceptedRoles = Set(
    user,
    userManager
  )
}
