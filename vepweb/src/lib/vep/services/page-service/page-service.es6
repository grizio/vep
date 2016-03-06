(() => {
  const http = window.vep.http;
  const objectUtils = window.vep.utils.object;

  // @singleton
  class PageService {
    //_pagesPromise

    findAll() {
      if (!this._pagesPromise) {
        this._pagesPromise = http.get("/cms/pages");
      }
      return this._pagesPromise;
    }

    find(canonical){
      return this.findAll().then((pages) => pages.find((p) => p.canonical === canonical));
    }

    findMenu() {
      return this.findAll().then((pages) =>
        pages.filter((p) => p.menu)
          .map((p) => {
            return {canonical: p.canonical, title: p.title};
          }));
    }

    create(page) {
      const that = this;
      const entity = {
        title: page.title,
        menu: parseInt(page.menu),
        content: page.content
      };
      return http.post(`/cms/page/${page.canonical}`, entity).then((_) => {
        that._pagesPromise = null;
      });
    }

    update(page) {
      const that = this;
      const entity = {
        title: page.title,
        menu: parseInt(page.menu),
        content: page.content
      };
      return http.put(`/cms/page/${page.canonical}`, entity).then((_) => {
        that._pagesPromise = null;
      });
    }
  }

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.page = new PageService();
})();