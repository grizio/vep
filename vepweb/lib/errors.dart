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

const i18n = const <int, String>{
  emptyEmail: "L'adresse e-mail n'a pas été renseignée.",
  invalidEmail: "L'adresse e-mail est invalide.",
  emptyFirstName: "Le prénom n'a pas été renseigné.",
  emptyLastName: "Le nom n'a pas été renseigné.",
  emptyPassword: "Le mot de passe n'a pas été renseigné.",
  weakPassword: "Le mot de passe ne respecte pas les critères de sécurité (8 caractères dont une lettre et un chiffre).",
  usedEmail: "L'adresse e-mail est déjà utilisée par un autre membre.",
  differentPasswords: "Les mots de passe sont différents"
};