(() => {
  class PageUpdatePage {
    beforeRegister() {
      this.is = "page-create-page";

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