(() => {
  const http = window.vep.http;

  class PageContact {
    beforeRegister() {
      this.is = "page-contact";

      this.properties = {
        contact: {
          type: Object,
          value: {
            content: "" // avoid "undefined" as value
          }
        },
        i18nEmail2ErrorsCopy: {
          type: String,
          value: null,
          observer: "_i18nEmail2ErrorsCopyChanged"
        },
        _done: {
          type: Boolean,
          value: false
        }
      };

      this.listeners = {
        "form.submit": "submit"
      };
    }

    _i18nEmail2ErrorsCopyChanged(message) {
      const that = this;
      this.$.email2.verify(message, (value) => value !== null && value === that.$.email.value);
    }

    submit(e) {
      e.preventDefault();
      const that = this
      http.post("/contact", this.contact).then((_) => that._done = true);
    }
  }

  Polymer(PageContact);
})();