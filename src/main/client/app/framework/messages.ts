const messages = {
  common: {
    isNull: "Veuillez remplir ce champ",
    invalidEmail: "L'adresse e-mail est invalide",
    invalidPasswordScore: (got: number, expected: number) =>
      `Le mot de passe est trop faible, veuillez ajouter des lettres majuscules, minuscules, chiffres ou caractères spéciaux (score: ${got}/${expected  })`,
    emptyString: "Veuillez remplir ce champ",
    emptyArray: "Veuillez indiquer au moins un élément",
    invalidCanonical: "Le format d'url est invalide (caractères autorisés: lettres minuscules, chiffres et -)",
    notPositive: "Veuillez indiquer un nombre entier positif",
    notFuture: "Veuillez indiquer une date dans le futur",
    loading: "Veuillez patienter pendant le chargement des données",
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
      list: {
        loading: "Veuillez patienter pendant que nous cherchons toutes les troupes."
      },
      page: {
        loading: "Veuillez patienter pendant que la troupe se remet en ordre."
      },
      form: {
        loading: "Veuillez patienter pendant que les comédiens finissent leur café."
      }
    },
    show: {
      list: {
        loading: "Veuillez patienter pendant que nous cherchons toutes les pièces."
      },
      page: {
        loading: "Veuillez patienter pendant que montons la pièce."
      },
      form: {
        loading: "Veuillez patienter pendant que nous terminons notre scène."
      }
    },
    play: {
      form: {
        loading: "Veuillez patienter pendant que nous levons la séance.",
        reservationEndDateAfterDate: "Veuillez indiquer une date de fin de réservation antérieure à la date de la séance"
      }
    },
    reservation: {
      form: {
        noSeats: "Veuillez sélectionner au moins une place"
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