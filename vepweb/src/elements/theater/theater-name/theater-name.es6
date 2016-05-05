(() => {
  const theaterService = window.vep.services.theater;

  class TheaterName {
    beforeRegister() {
      this.is = "theater-name";

      this.properties = {
        canonical: {
          type: String,
          value: null,
          observer: "_update"
        },
        name: {
          type: String,
          value: null,
          readOnly: true
        }
      };
    }

    attached() {
      this._update();
    }

    _update() {
      const that = this;
      this._promise = this._promise || Promise.resolve();
      this._promise.then(() => theaterService.find(that.canonical)).then((theater) => that._setName(theater ? theater.name : null));
    }
  }

  Polymer(TheaterName);
})();