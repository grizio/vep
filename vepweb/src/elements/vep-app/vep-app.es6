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
        },
        _showUseCookies: {
          type: Boolean,
          value: true,
          observer: "_showUseCookiesChanged"
        }
      };

      this.listeners = {
        "logout.tap": "logout",
        "loginService.login": "_onLogin"
      };
    }

    attached() {
      this._buildMenu();
      this._showUseCookies = localStorage["use-cookies"] !== "hide";
      this.isAttached = true;
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

    _showUseCookiesChanged(value) {
      if (this.isAttached && !value) {
        localStorage["use-cookies"] = "hide";
      }
    }
  }

  Polymer(VepApp);
})();