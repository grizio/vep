(() => {
  const pageService = window.vep.services.page;

  class VepApp {
    beforeRegister() {
      this.is = "vep-app";

      this.properties = {
        _pages: {
          type: Array,
          value: []
        },
        _logged: {
          type: Boolean,
          value: null
        }
      };

      this.listeners = {
        "logout.tap": "logout",
        "loginService.login": "_onLogin"
      };
    }

    attached() {
      this._buildMenu();
    }

    logout(e) {
      e.preventDefault();
      this.$.loginService.logout();
    }

    _buildMenu() {
      const that = this;
      pageService.findMenu().then((pages) => that._pages = pages);
    }

    _onLogin(e) {
      this._logged = e.detail.logged;
    }
  }

  Polymer(VepApp);
})();