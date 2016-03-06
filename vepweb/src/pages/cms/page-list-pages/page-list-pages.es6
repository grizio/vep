(() => {
  const pageService = window.vep.services.page;

  class PageListPages {
    beforeRegister() {
      this.is = "page-list-pages";

      this.properties = {
        _pages: {
          type: Array,
          value: []
        }
      };
    }

    attached() {
      const that = this;
      pageService.findAll().then((pages) => {
        // force the value of p.menu
        pages.forEach((p) => p.menu = p.menu || "");
        that._pages = pages
      });
    }
  }

  Polymer(PageListPages);
})();