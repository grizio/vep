(() => {
  const sessionService = window.vep.services.session;
  const theaterService = window.vep.services.theater;
  const showService = window.vep.services.show;
  const vepErrors = window.vep.errors;

  // model
  class Reservation {
    //_reservation

    constructor() {
      this.selectedSeats = [];
      this.prices = [];
    }

    toJsonObject() {
      return {
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
        city: this.city,
        comment: this.comment,
        seatList: this.seatList,
        prices: this.pricesToSend
      };
    }

    get pricesToSend() {
      const prices = [];
      for (let price in this.prices) {
        if (this.prices.hasOwnProperty(price)) {
          prices.push({price: parseInt(price), number: this.prices[price]});
        }
      }
      return prices;
    }

    get seatList() {
      return this.selectedSeats.map((s) => s.c);
    }
  }

  class Session {
    //_session

    constructor(session, shows) {
      session.prices.forEach((p) => p.price /= 100);
      session.reservationURL = `/reservation/${this.theaterCanonical}/${this.sessionCanonical}`;
      shows.forEach(s => s.link = `/show/read/${s.canonical}`);
      session.shows = shows;
      this._session = session;
    }

    get date() {
      return new Date(this._session.date);
    }

    get strDate() {
      return this._session.date;
    }

    get name() {
      return this._session.name;
    }

    get prices() {
      return this._session.prices;
    }

    get reservationEndDate() {
      return this._session.reservationEndDate;
    }

    get shows() {
      return this._session.shows;
    }

    get firstShow() {
      return this.shows && this.shows.length > 0 ? this.shows[0] : null;
    }

    get canReserve() {
      return new Date(this.reservationEndDate).getTime() > new Date().getTime()
    }

    get reservationURL() {
      return `/reservation/${this._session.theater}/${this._session.canonical}`;
    }
  }

  class PageReservation {
    beforeRegister() {
      this.is = "page-reservation";

      this.properties = {
        theaterCanonical: {
          type: String,
          value: null
        },
        sessionCanonical: {
          type: String,
          value: null
        },
        session: {
          type: Object,
          value: null
        },
        shows: {
          type: Array,
          value: []
        },
        theater: {
          type: Object,
          value: null
        },
        reservedSeats: {
          type: Array,
          value: []
        },
        reservation: {
          type: Object,
          value: null
        },
        i18nPersonEmail2Error: {
          type: String,
          value: null,
          observer: "_i18nPersonEmail2ErrorChanged"
        },
        seatNumber: {
          type: Number,
          value: 0
        },
        summaryPrices: {
          type: Array,
          value: []
        },
        summaryTotalPrice: {
          type: Number,
          value: 0
        },
        hasCity: {
          type: Boolean,
          value: false
        },
        hasComment: {
          type: Boolean,
          value: false
        },
        done: {
          type: Boolean,
          value: false
        },
        errors: {
          type: Array,
          value: []
        },
        hasError: {
          type: Boolean,
          computed: "_computeHasError(errors)"
        }
      };

      this.observers = [
        "_computeSeatNumber(reservation.selectedSeats)",
        "_computeSummaryPrices(reservation.prices)",
        "_computeHasCity(reservation.city)",
        "_computeHasComment(reservation.comment)"
      ];

      this.listeners = {
        "form.send": "_formSend"
      };
    }

    attached() {
      const that = this;

      const theaterFuture = theaterService.find(this.theaterCanonical).then((theater) => {
        theater.link = `/theater/${theater.canonical}`;
        return theater;
      });

      const sessionFuture = sessionService.find(this.theaterCanonical, this.sessionCanonical);

      const showsFuture = sessionFuture.then((session) => Promise.all(session.shows.map((s) => showService.find(s))));

      const reservedSeatsFuture = sessionService.findReservedSeats(this.theaterCanonical, this.sessionCanonical);

      Promise.all([theaterFuture, sessionFuture, showsFuture, reservedSeatsFuture]).then((results) => {
        const [theater, session, shows, reservedSeats] = results;
        that.theater = theater;
        that.session = new Session(session, shows);
        that.reservedSeats = reservedSeats;
      });

      this.reservation = new Reservation();
    }

    _i18nPersonEmail2ErrorChanged(message) {
      if (message) {
        const that = this;
        this.$.email2Field.verify(message, (value) => that.$.emailField.value === value);
      }
    }

    _computeSeatNumber(selectedSeats) {
      this.seatNumber = selectedSeats ? selectedSeats.length : 0;
    }

    _computeSummaryPrices(prices) {
      if (this.session) {
        this.summaryPrices = this.session.prices.map((p) => {
          return {name: p.name, price: p.price, number: prices[p.id], total: p.price * prices[p.id]}
        });
        this.summaryTotalPrice = this.summaryPrices.reduce((acc, cur) => acc + cur.total, 0);
      }
    }

    _computeHasCity(city) {
      this.hasCity = city && city.length > 0;
    }

    _computeHasComment(comment) {
      this.hasComment = comment && comment.length > 0;
    }

    _computeHasError(errors) {
      return errors && errors.length > 0;
    }

    _formSend(e) {
      const that = this;
      sessionService.reserve(this.theaterCanonical, this.sessionCanonical, this.reservation.toJsonObject()).then((result) => {
        that.done = true;
      }).catch((e) => {
        if (e.xhr.status == 400) {
          const errors = [];
          if (e.result.seatList) {
            const seatList = e.result.seatList;
            for (let index in seatList) {
              if (seatList.hasOwnProperty(index)) {
                seatList[index].forEach((error) => {
                  errors.push(vepErrors.find(error, that.reservation.seatList[index]));
                });
              }
            }
          }
          that.errors = errors;
          that.$.form.goToStep(1);
          that.$.form.enable();
        }
      });
    }
  }

  Polymer(PageReservation);
})();