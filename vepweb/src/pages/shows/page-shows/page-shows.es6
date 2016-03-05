(function () {
  const showService = window.vep.services.show;

  class PageShows {
    beforeRegister() {
      this.is = "page-shows";

      this.properties = {
        shows: {
          type: Array,
          value: []
        }
      }
    }

    attached() {
      const that = this;
      showService.findAll().then((shows) => that.shows = shows);
    }
  }

  Polymer(PageShows);
})();