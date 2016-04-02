(() => {
  const theaterService = window.vep.services.theater;

  class PageTheaters {
    beforeRegister() {
      this.is = "page-theaters";

      this.properties = {
        theaters: {
          type: Array,
          value: []
        }
      }
    }

    attached() {
      const that = this;
      theaterService.findAll().then((_) => that.theaters = _);
    }
  }

  Polymer(PageTheaters);
})();