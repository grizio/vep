(() => {
  const http = window.vep.http;

  const companyService = {};

  let companies = null;

  companyService.findAll = () => {
    if (companies) {
      return Promise.resolve(companies);
    } else {
      return http.get("/companies").then((result) => {
        companies = result;
        return companies;
      });
    }
  };

  companyService.find = (canonical) =>
    companyService.findAll().then((result) => result.find((c) => c.canonical === canonical));

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.company = Object.freeze(companyService);
})();