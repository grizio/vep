(() => {
  class PageRead {
    beforeRegister() {
      this.is = "page-read";

      this.properties = {
        canonical: {
          type: String,
          value: null
        }
      };
    }
  }

  Polymer(PageRead);
})();