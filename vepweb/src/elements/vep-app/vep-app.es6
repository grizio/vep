(() => {
  const pageService = window.vep.services.page;

  class VepApp {
    beforeRegister() {
      this.is = "vep-app";

      this.properties = {
        _pages: {
          type: Array,
          value: []
        }
      }
    }

    attached() {
      this._buildMenu();
    }

    _buildMenu() {
      const that = this;
      pageService.findMenu().then((pages) => that._pages = pages);
    }
  }

  Polymer(VepApp);
})();