(() => {
  const companyService = window.vep.services.company;

  class PageCompanies {
    beforeRegister() {
      this.is = "page-companies";

      this.properties = {
        companies: {
          type: Array,
          value: []
        }
      }
    }

    attached() {
      const that = this;
      companyService.findAll().then((companies) => {
        companies.forEach((company) => company.address = company.address || "");
        that.companies = companies;
      });
    }
  }

  Polymer(PageCompanies);
})();