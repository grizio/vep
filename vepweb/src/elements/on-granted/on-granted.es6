(() => {
  class OnGranted {
    beforeRegister() {
      this.is = "on-granted";

      this.properties = {
        roles: {
          type: String,
          value: null
        },
        block: {
          type: Boolean,
          value: false
        },
        hide: {
          type: Boolean,
          value: false
        },
        _logged: {
          type: Boolean,
          value: false
        },
        _hideContent: {
          type: Boolean,
          computed: "_computeHideContent(roles, _logged)"
        },
        _hideLogin: {
          type: Boolean,
          computed: "_computeHideLogin(block, _logged)"
        },
        _hideCallout: {
          type: Boolean,
          computed: "_computeHideCallout(roles, hide, _logged)"
        }
      }
    }

    attached() {

    }

    _computeHideContent(rolesStr, logged) {
      const that = this;
      const roles = rolesStr ? rolesStr.split(",") : [];
      return !logged || roles.some((role) => !that.$.loginService.getRoles().some((r) => r === role));
    }

    _computeHideLogin(block, logged) {
      return logged || block;
    }

    _computeHideCallout(rolesStr, hide, logged) {
      const that = this;
      const roles = rolesStr ? rolesStr.split(",") : [];
      return !logged || hide || roles.every((role) => that.$.loginService.getRoles().some((r) => r === role));
    }
  }

  Polymer(OnGranted);
})();