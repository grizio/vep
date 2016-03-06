(function () {
  const errors = window.vep.errors;

  class PageLogin {
    beforeRegister() {
      this.is = "page-login";

      this.properties = {
        _email: {
          type: String,
          value: ""
        },
        _password: {
          type: String,
          value: ""
        },
        _logged: {
          type: Boolean,
          value: false
        },
        _error: {
          type: String,
          value: null
        }
      };

      this.listeners = {
        "form.submit": "submit"
      };
    }

    submit(e) {
      e.preventDefault();
      const that = this;
      this._error = null;
      this.$.loginService
        .login(this._email, this._password)
        .catch((e) => that._error = errors.find(e.result));
    }
  }

  Polymer(PageLogin);
})();