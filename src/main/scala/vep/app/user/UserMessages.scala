package vep.app.user

object UserMessages {
  def userExists(email: String) = s"Un utilisateur avec l'adresse email ${email} existe déjà."

  val invalidLogin = "L'identifiant ou le mot de passe est invalide."

  val inactive = "Votre compte n'est pas encore actif. Veuillez accéder au lien fourni dans le mail de validation."

  val unknown = "L'utilisateur n'existe pas"

  val invalidActivationKey = "La clef d'activation est invalide"

  val closedPeriod = "La période est fermée aux inscriptions"

  val invalidResetPasswordKey = "La clef de réinitialisation du mot de passe est invalide"
}
