(() => {
  const http = window.vep.http;

  class CompanyService {
    findAll() {
      if (this._companiesPromise) {
        return Promise.resolve(this._companiesPromise);
      } else {
        return http.get("/companies").then((result) => {
          this._companiesPromise = result;
          return this._companiesPromise;
        });
      }
    }

    find(canonical) {
      return this.findAll().then((result) => result.find((c) => c.canonical === canonical));
    }

    create(company) {
      const that = this;
      company.isVep = !!company.isVep;
      return http.post(`/company/${company.canonical}`, company).then((_) => {
        that._companiesPromise = null;
        return _;
      });
    }

    update(company) {
      const that = this;
      company.isVep = !!company.isVep;
      return http.put(`/company/${company.canonical}`, company).then((_) => {
        that._companiesPromise = null;
        return _;
      });
    }
  }

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.company = new CompanyService();
})();