(() => {
  const http = window.vep.http;

  const pageService = {};

  let pagesPromise = null;

  pageService.findAll = () => {
    if (!pagesPromise) {
      pagesPromise = http.get("/cms/pages");
    }
    return pagesPromise;
  };

  pageService.find = (canonical) => pageService.findAll().then((pages) => pages.find((p) => p.canonical === canonical));

  pageService.findMenu = () => pageService.findAll().then((pages) =>
    pages.filter((p) => p.menu)
      .map((p) => {
        return {canonical: p.canonical, title: p.title};
      }));

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.page = Object.freeze(pageService);
})();