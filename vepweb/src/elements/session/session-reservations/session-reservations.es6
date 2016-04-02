(() => {
  const sessionService = window.vep.services.session;

  class SessionReservations {
    beforeRegister() {
      this.is = "session-reservations";

      this.properties = {
        theater: {
          type: String,
          value: null
        },
        session: {
          type: String,
          value: null
        },
        reservations: {
          type: Array,
          value: []
        }
      };
    }

    attached() {
      const sessionFuture = sessionService.find(this.theater, this.session);
      const reservationsFuture = sessionService.findReservations(this.theater, this.session);

      Promise.all([sessionFuture, reservationsFuture]).then((_) => {
        const [session, reservations] = _;

        this.reservations = reservations.map((reservation) => {
          const result = {
            "firstName": reservation.firstName,
            "lastName": reservation.lastName,
            "email": reservation.email,
            "city": reservation.city,
            "comment": reservation.comment || "",
            "prices": reservation.prices,
            "seats": reservation.seatList.join(", ")
          };

          let total = 0;
          for (let i = 0, c = result.prices.length ; i < c ; i++) {
            const price = result.prices[i];
            price.value /= 100;
            price.name = session.prices.find((_) => _.id === price.price).name;
            price.total = price.number * price.value;
            total += price.total;
          }
          result["total"] = total;

          return result;
        });
      });
    }
  }

  Polymer(SessionReservations); })();