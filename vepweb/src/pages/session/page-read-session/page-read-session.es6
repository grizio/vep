(() => {
  const sessionService = window.vep.services.session;
  const theaterService = window.vep.services.theater;
  const showService = window.vep.services.show;

  class PageReadSession {
    beforeRegister() {
      this.is = "page-read-session";

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
        _theaterPlace: {
          type: Object,
          value: null
        }
      };

      this.listeners = {
        "gmapSearch.google-map-search-results": "_gmapResult"
      }
    }

    attached() {
      const that = this;
      sessionService.find(this.theaterCanonical, this.sessionCanonical).then((session) => {
        session.prices.forEach((p) => p.price /= 100);
        session.canReserve = new Date(session.reservationEndDate).getTime() > new Date().getTime();
        session.reservationURL = `/reservation/${this.theaterCanonical}/${this.sessionCanonical}`;
        Promise.all(session.shows.map((s) => showService.find(s))).then((shows) => {
          session.shows = shows;
          that.session = session;
        });
      });

      theaterService.find(this.theaterCanonical).then((theater) => {
        that.theater = theater;
        that.$.gmapSearch.search();
      });

      sessionService.findReservedSeats(this.theaterCanonical, this.sessionCanonical).then((reservedSeats) => {
        that.reservedSeats = reservedSeats;
      });
    }

    _gmapResult(event) {
      if (event.detail && event.detail.length > 0) {
        this._theaterPlace = event.detail[0];
      }
    }
  }

  Polymer(PageReadSession);
})();