(() => {
  const http = window.vep.http;
  const companyService = window.vep.services.company;

  class ShowService {
    findAll() {
      return new Promise((resolve, reject) => {
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
    }

    find(canonical) {
      return http.get(`/show/${canonical}`).then((show) =>
        companyService.find(show.company).then((company) => {
          show.company = company;
          return show;
        })
      );
    }

    create(show) {
      show.duration = show.duration ? parseInt(show.duration) : 0;
      return http.post(`/show/${show.canonical}`, show);
    }

    update(show) {
      show.duration = show.duration ? parseInt(show.duration) : 0;
      return http.put(`/show/${show.canonical}`, show);
    }
  }

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.show = new ShowService();
})();