(function () {
  const objectUtils = window.vep.utils.object;

  const errors = Object.freeze({
    emptyEmail: {num: 1, message: "L'adresse e-mail n'a pas été renseignée."},
    invalidEmail: {num: 2, message: "L'adresse e-mail est invalide."},
    emptyFirstName: {num: 3, message: "Le prénom n'a pas été renseigné."},
    emptyLastName: {num: 4, message: "Le nom n'a pas été renseigné."},
    emptyPassword: {num: 5, message: "Le mot de passe n'a pas été renseigné."},
    weakPassword: {num: 6,message: "Le mot de passe ne respecte pas les critères de sécurité (8 caractères dont une lettre et un chiffre)."},
    usedEmail: {num: 7, message: "L'adresse e-mail est déjà utilisée par un autre membre."},
    differentPasswords: {num: 8, message: "Les mots de passe sont différents"},
    unknownUser: {num: 9, message: "L'utilisateur ou le mot de passe est incorrect."},
    unauthenticated: {num: 10, message: "Veuillez vous connecter pour réaliser cette opération"},
    unauthorized: {num: 11, message: "Vous n'avez pas l'autorisation d'effectuer cette opération"},
    roleUnknown: {num: 12, message: "Le rôle indiqué n'existe pas"},
    userUnknown: {num: 13, message: "L'utilisateur indiqué n'existe pas"},
    differentEmail: {num: 14, message: "Les adresses e-mail sont différentes."},
    invalidCanonical: {num: 15, message: "L'URL fournie ne respecte pas le format requis"},
    emptyField: {num: 16, message: "Veuillez renseigner ce champ"},
    negativeOrNull: {num: 17, message: "Veuillez indiquer un nombre strictement positif"},
    usedCanonical: {num: 18, message: "L'URL fournie est déjà utilisée"},
    unknownCanonical: {num: 19, message: "Le canonical n'a pas été trouvé."},
    invalidJson: {num: 20, message: "Le format de la chaine fourni ne respecte pas celui attendu (JSON)."},
    bigString: {num: 21, message: "La taille du texte dépasse celle autorisée."},
    undefinedCompany: {num: 22, message: "La troupe fournie n'existe pas ou plus."},
    negative: {num: 23, message: "Veuillez indiquer un nombre positif ou nul."},
    undefinedShow: {num: 24, message: "Le spectacle n'a pas été trouvé."},
    notANumber: {num: 25, message: "Le nombre fourni est invalide"},
    unknownOrder: {num: 26, message: "La colonne indiquée est inconnue."},
    undefinedTheater: {num: 27, message: "Le théâtre indiqué n'a pas été trouvé."},
    existingDateForTheater: {num: 28, message: "Il existe déjà une séance pour ce théâtre."},
    invalidDate: {num: 29, message: "La date est invalide"},
    dateTooSoon: {num: 30, message: "La date doit être supérieure à la date actuelle."},
    reservationEndDateTooLate: {num: 31, message: "La date de fin de réservation doit être inférieure ou égale à la date de la séance."},
    emptyShowList: {num: 32, message: "Veuillez indiquer au moins une pièce."},
    undefinedSession: {num: 33, message: "La séance indiquée n'a pas été trouvée."},
    lockedTheater: {num: 34, message: "Le théâtre ne peut pas être modifié pour le moment."},

    find: function(num) {
      const result = objectUtils.getValues(this).find((v) => v.num === num);
      return result ? result.message : null;
    }
  });

  window.vep = window.vep || {};
  window.vep.errors = errors;
})();