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
  }

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.theater = new TheaterService();
})();