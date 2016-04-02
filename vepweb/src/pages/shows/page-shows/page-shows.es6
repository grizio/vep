(function () {
  const showService = window.vep.services.show;
  const companyService = window.vep.services.company;

  class PageShows {
    beforeRegister() {
      this.is = "page-shows";

      this.properties = {
        shows: {
          type: Array,
          value: []
        },
        companies: {
          type: Array,
          value: []
        }
      };
    }

    attached() {
      const that = this;
      showService.findAll().then((shows) => that.shows = shows);
      companyService.findAll().then((companies) => that.companies = companies);
    }

    getCompany(companyCanonical) {
      const company = this.companies.find((c) => c.canonical === companyCanonical);
      return company ? company.name : null;
    }
  }

  Polymer(PageShows);
})();