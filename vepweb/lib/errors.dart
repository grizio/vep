library vep.errors;

// Because we do not really have a i18n constraint and the size of the application is limited,
// we define our error messages in this file as constants.
// In the case of the application growth enough, the strategy should change to get only wanted messages.
// Consequence: The application will be long to start but faster to run.
// The codes and constants are the same as VepServer.

const emptyEmail = 1;
const invalidEmail = 2;
const emptyFirstName = 3;
const emptyLastName = 4;
const emptyPassword = 5;
const weakPassword = 6;
const usedEmail = 7;
const differentPasswords = 8;
const unknownUser = 9;
const unauthenticated = 10;
const unauthorized = 11;
const roleUnknown = 12;
const userUnknown = 13;
const differentEmail = 14;
const invalidCanonical = 15;
const emptyField = 16;
const negativeOrNull = 17;
const usedCanonical = 18;
const unknownCanonical = 19;
const invalidJson = 20;
const bigString = 21;
const undefinedCompany = 22;
const negative = 23;
const undefinedShow = 24;
const notANumber = 25;
const unknownOrder = 26;
const undefinedTheater = 27;
const existingDateForTheater = 28;
const invalidDate = 29;
const dateTooSoon = 30;
const reservationEndDateTooLate = 31;
const emptyShowList = 32;
const undefinedSession = 33;
const lockedTheater = 34;

const i18n = const <int, String>{
  emptyEmail: "L'adresse e-mail n'a pas été renseignée.",
  invalidEmail: "L'adresse e-mail est invalide.",
  emptyFirstName: "Le prénom n'a pas été renseigné.",
  emptyLastName: "Le nom n'a pas été renseigné.",
  emptyPassword: "Le mot de passe n'a pas été renseigné.",
  weakPassword: "Le mot de passe ne respecte pas les critères de sécurité (8 caractères dont une lettre et un chiffre).",
  usedEmail: "L'adresse e-mail est déjà utilisée par un autre membre.",
  differentPasswords: "Les mots de passe sont différents",
  unknownUser: "L'utilisateur ou le mot de passe est incorrect.",
  unauthenticated: "Veuillez vous connecter pour réaliser cette opération",
  unauthorized: "Vous n'avez pas l'autorisation d'effectuer cette opération",
  roleUnknown: "Le rôle indiqué n'existe pas",
  userUnknown: "L'utilisateur indiqué n'existe pas",
  differentEmail: "Les adresses e-mail sont différentes.",
  invalidCanonical: "L'URL fournie ne respecte pas le format requis",
  emptyField: "Veuillez renseigner ce champ",
  negativeOrNull: "Veuillez indiquer un nombre strictement positif",
  usedCanonical: "L'URL fournie est déjà utilisée",
  unknownCanonical: "Le canonical n'a pas été trouvé.",
  invalidJson: "Le format de la chaine fourni ne respecte pas celui attendu (JSON).",
  bigString: "La taille du texte dépasse celle autorisée.",
  undefinedCompany: "La troupe fournie n'existe pas ou plus.",
  negative: "Veuillez indiquer un nombre positif ou nul.",
  undefinedShow: "Le spectacle n'a pas été trouvé.",
  notANumber: "Le nombre fourni est invalide",
  unknownOrder: "La colonne indiquée est inconnue.",
  undefinedTheater: "Le théâtre indiqué n'a pas été trouvé.",
  existingDateForTheater: "Il existe déjà une séance pour ce théâtre.",
  invalidDate: "La date est invalide",
  dateTooSoon: "La date doit être supérieure à la date actuelle.",
  reservationEndDateTooLate: "La date de fin de réservation doit être inférieure ou égale à la date de la séance.",
  emptyShowList: "Veuillez indiquer au moins une pièce.",
  undefinedSession: "La séance indiquée n'a pas été trouvée.",
  lockedTheater: "Le théâtre ne peut pas être modifié pour le moment.",
};