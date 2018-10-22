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
    notPassed: "Veuillez indiquer une date dans le passé",
    loading: "Veuillez patienter pendant le chargement des données",
    invalidPeriod: "La date de fin est avant la date de début",
    contact: {
      differentEmail: "Les adresses e-mail sont différentes"
    },
    page: {
      form: {
        loading: "Veuillez patienter pendant nous nous mettons à la page"
      }
    },
    blog: {
      form: {
        loading: "Veuillez patienter pendant nous rédigeons l'article"
      }
    }
  },
  user: {
    differentPassword: "Les mots de passe sont différents",
    activation: {
      done: "Votre compte a bien été activé. Vous pouvez dès à présent vous connecter."
    },
    adhesion: {
      formPeriod: {
        loading: "Veuillez patienter pendant que nous nous y retrouvons dans les dates."
      },
      formRequest: {
        loading: "Veuillez patienter pendant que nous vous récupérons le formulaire."
      }
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
        loading: "Veuillez patienter pendant que nous cherchons tous les spectacles."
      },
      page: {
        loading: "Veuillez patienter pendant que montons le spectacle.",
        plays: {
          deletion: {
            title: "Supprimer la séance ?",
            message: "Voulez-vous vraiment supprimer cette séance ? Toutes les réservations seront également supprimées. Il ne sera pas possible de revenir en arrière."
          }
        }
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
        noSeats: "Veuillez sélectionner au moins une place",
        invalidPriceRepartition: "Veillez a bien répartir l'ensemble de vos places dans les différents tarifs."
      },
      list: {
        deletion: {
          title: "Supprimer la réservation ?",
          message: "Voulez-vous vraiment supprimer cette réservation ? Il ne sera pas possible de revenir en arrière."
        }
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