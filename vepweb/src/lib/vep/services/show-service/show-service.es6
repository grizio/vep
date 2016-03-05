(() => {
  const http = window.vep.http;
  const companyService = window.vep.services.company;

  const showService = {};

  showService.findAll = () =>
    new Promise((resolve, reject) => {
      // Current API adaptation - will need an update
      var process = (page, showsStack) => {
        http.get(`/shows?p=${page}`).then((result) => {
          showsStack.push.apply(showsStack, result.shows);
          if (page >= result.pageMax) {
            resolve(showsStack);
          } else {
            process(page + 1, showsStack);
          }
        })
      };
      process(1, []);
    });

  showService.find = (canonical) =>
    http.get(`/show/${canonical}`).then((show) =>
      companyService.find(show.company).then((company) => {
        show.company = company;
        return show;
      })
    );

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.show = Object.freeze(showService);
})();