(() => {
  const RequestBuilder = window.vep.RequestBuilder;
  const methods = window.vep.methods;

  const KEY = "vep-session";

  let listeners = [];

  class LoginService {
    // used because of bidirectional dependency
    get http() {
      return window.vep.http;
    }

    beforeRegister() {
      this.is = "login-service";

      this.properties = {
        logged: {
          type: Boolean,
          value: false,
          reflectToAttribute: true,
          notify: true,
          readOnly: true,
          computed: "_computeLogged(_session)"
        },
        _session: {
          type: Object,
          value: null
        }
      }
    }

    attached() {
      listeners.push(this);
      this._refresh();
    }

    detached() {
      const that = this;
      listeners = listeners.filter((l) => l !== that);
    }

    login(email, password) {
      const that = this;
      return this.http.post("/login", {
        email: email,
        password: password
      }).then((response) => {
        new RequestBuilder()
          .withMethod(methods.GET)
          .withUrl("/user/roles")
          .withHeader("Authorization", `Basic ${btoa(`${email}:${response}`)}`)
          .send()
          .then((result) => {
            that._save({
              email: email,
              token: response,
              roles: result
            });
          });
      });
    }

    logout() {
      this._save(null);
    }

    getRoles() {
      return this.logged ? this._session.roles : [];
    }

    _computeLogged(session) {
      return !!session;
    }

    _save(info) {
      this._session = info;
      if (info) {
        localStorage[KEY] = btoa(JSON.stringify(info));
      } else {
        localStorage.removeItem(KEY);
      }
      listeners.forEach((l) => l._refresh());
    }

    _refresh() {
      const fromLocalStorage = localStorage[KEY];
      if (fromLocalStorage) {
        this._session = JSON.parse(atob(fromLocalStorage));
      } else {
        this._session = null;
      }
    }
  }

  Polymer(LoginService);
})();