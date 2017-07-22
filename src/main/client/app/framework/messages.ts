const messages = {
  common: {
    invalidEmail: "L'adresse e-mail est invalide",
    invalidPasswordScore: (got: number, expected: number) =>
      `Le mot de passe est trop faible, veuillez ajouter des lettres majuscules, minuscules, chiffres ou caractères spéciaux (score: ${got}/${expected  })`,
    emptyString: "Veuillez remplir ce champ",
    emptyArray: "Veuillez indiquer au moins un élément",
    invalidCanonical: "Le format d'url est invalide (caractères autorisés: lettres minuscules, chiffres et -)",
    notPositive: "Veuillez indiquer un nombre entier positif",
    contact: {
      differentEmail: "Les adresses e-mail sont différentes"
    }
  },
  user: {
    differentPassword: "Les mots de passe sont différents",
    activation: {
      done: "Votre compte a bien été activé. Vous pouvez dès à présent vous connecter."
    }
  },
  production: {
    theater: {
      list: {
        loading: "Veuillez patienter pendant que nous retrouvons la liste sous une liasse de poussière."
      },
      form: {
        loading: "Veuillez patienter pendant que nous trions la liste des sièges."
      }
    },
    company: {
      form: {
        loading: "Veuillez patienter pendant que les comédiens finissent leur café."
      }
    }
  },
  http: {
    serverError: "Une erreur est survenue lors de l'appel au serveur. Veuillez réessayer ultérieurement. Si le problème persiste, merci de contacter l'administrateur.",
    notAccess: "Vous avez été déconnecté."
  },
  find(key: string) {
    const parts = key.split(".")
    let current = this
    for (let i = 0; i < parts.length && current; i++) {
      current = current[parts[i]]
    }
    if (current) {
      return current
    } else {
      return key
    }
  }
}
export default messages