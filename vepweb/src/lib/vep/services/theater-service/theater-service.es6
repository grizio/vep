(() => {
  const http = window.vep.http;

  // @singleton
  class TheaterService {
    //_theatersPromise

    findAll() {
      if (!this._theatersPromise) {
        this._theatersPromise = http.get(`/theaters`);
      }
      return this._theatersPromise;
    }

    find(canonical) {
      return this.findAll().then((theaters) => theaters.find((t) => t.canonical === canonical));
    }

    create(theater) {
      const that = this;
      theater.fixed = true;
      return http.post(`/theater/${theater.canonical}`, theater).then((_) => {
        that._theatersPromise = null;
        return _;
      });
    }

    update(theater) {
      const that = this;
      theater.fixed = true;
      return http.put(`/theater/${theater.canonical}`, theater).then((_) => {
        that._theatersPromise = null;
        return _;
      });
    }
  }

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.theater = new TheaterService();
})();