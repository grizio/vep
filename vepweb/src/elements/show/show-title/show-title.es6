(() => {
  const showService = window.vep.services.show;

  class ShowName {
    beforeRegister() {
      this.is = "show-title";

      this.properties = {
        canonical: {
          type: String,
          value: null,
          observer: "_update"
        },
        title: {
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
      this._promise.then(() => showService.find(that.canonical)).then((show) => that._setTitle(show.title));
    }
  }

  Polymer(ShowName);
})();