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
  val isNotFuture = "Veuillez indiquer une date postérieure à la date du jour"
  val isBefore = "La date fournie n'est pas antérieure à la date de référence"
  val isAfter = "La date fournie n'est pas postérieure la date de référence"
  def duplicatedElement[A](value: A) = s"${value.toString} existe deux fois"
  def forbiddenElement[A](value: A) = s"${value.toString} n'est pas autorisé"
  val invalidCanonical = "Le format d'url est invalide (caractères autorisés: lettres minuscules, chiffres et -)"
  val periodNotIncluded = "La période n'est pas inclue dans la période de référence"
}
