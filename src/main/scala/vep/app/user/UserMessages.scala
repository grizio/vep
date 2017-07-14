package vep.app.user

object UserMessages {
  def userExists(email: String) = s"Un utilisateur avec l'adresse email ${email} existe déjà."
}
