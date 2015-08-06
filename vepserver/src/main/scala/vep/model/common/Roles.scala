package vep.model.common

/**
 * This class lists all application roles.
 */
object Roles {
  val user = "user"
  val userManager = "user-manager"
  val pageManager = "page-manager"
  val theaterManager = "theater-manager"
  val companyManager = "company-manager"
  val showManager = "show-manager"

  /**
   * This val groups all roles defined above.
   */
  lazy val acceptedRoles = Set(
    user,
    userManager,
    pageManager,
    theaterManager,
    companyManager,
    showManager
  )
}
