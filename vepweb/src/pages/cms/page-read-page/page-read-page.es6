(() => {
  class PageReadPage {
    beforeRegister() {
      this.is = "page-read-page";

      this.properties = {
        canonical: {
          type: String,
          value: null
        }
      };
    }
  }

  Polymer(PageReadPage);
})();