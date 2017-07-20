package vep.app.common

object CommonMessages {
  val invalidEmail = "L'adresse e-mail est invalide"
  val invalidPassword = "Le mot de passe est trop simple, tentez de rajouter des lettres majuscules, minuscules, chiffres et caractères spéciaux"
  val errorIsEmpty = "Veuillez renseigner le champ"
  val errorIsBlank = "Veuillez renseigner le champ"
  val errorIsSeqEmpty = "Veuillez indiquer au moins un élément"
  val isNotPositive = "Veuillez indiquer un nom strictement positif"
  def isDifferent[A](value: A, expected: A) = s"${value} est différent de ${expected}"
  def notFound = "L'élément n'a pas été trouvé"
}
