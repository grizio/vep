const messages = {
  common: {
    invalidEmail: "L'adresse e-mail est invalide",
    invalidPasswordScore: (got: number, expected: number) =>
      `Le mot de passe est trop faible, veuillez ajouter des lettres majuscules, minuscules, chiffres ou caractères spéciaux (score: ${got}/${expected  })`
  },
  user: {
    differentPassword: "Les mots de passe sont différents"
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