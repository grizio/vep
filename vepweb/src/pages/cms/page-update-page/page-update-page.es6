(() => {
  class PageUpdatePage {
    beforeRegister() {
      this.is = "page-update-page";

      this.properties = {
        canonical: {
          type: String,
          value: null
        }
      };

      this.listeners = {
        "form.sent": "_sent"
      };
    }

    _sent(e) {
      document.querySelector('app-router').go('/cms/pages');
    }
  }

  Polymer(PageUpdatePage);
})();