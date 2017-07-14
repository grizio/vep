package vep.app.user

object UserMessages {
  def userExists(email: String) = s"Un utilisateur avec l'adresse email ${email} existe déjà."

  val unknown = "L'utilisateur n'existe pas"

  val invalidActivationKey = "La clef d'activation est invalide"
}
