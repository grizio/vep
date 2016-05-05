(() => {
  const theaterService = window.vep.services.theater;
  const sessionService = window.vep.services.session;
  const stringUtils = window.vep.utils.string;

  class PageFormSession {
    beforeRegister() {
      this.is = "page-form-session";

      this.properties = {
        canonical: {
          type: String,
          value: null
        },
        session: {
          type: Object,
          value: {}
        },
        theaters: {
          type: Array,
          value: []
        },
        isCreation: {
          type: Boolean,
          computed: "_computeIsCreation(canonical)"
        },
        errors: {
          type: Array,
          value: []
        },
        hasError: {
          type: Boolean,
          computed: "_computeHasError(errors)"
        }
      };

      this.listeners = {
        "form.send": "_submit"
      }
    }

    attached() {
      const that = this;
      this.session = {};
      theaterService.findAll().then((theaters) => that.theaters = theaters);
    }

    _computeIsCreation(canonical) {
      return !canonical;
    }

    _computeHasError(errors) {
      return errors && errors.length > 0;
    }

    _titleChanged(title) {
      if (title && !this.canonical) {
        if (stringUtils.isEmpty(this._oldCanonical) && stringUtils.isEmpty(this.session.canonical) || stringUtils.equals(this._oldCanonical, this.show.canonical)) {
          this.$.canonical.value = stringUtils.canonicalize(title);
        }
        this._oldCanonical = stringUtils.canonicalize(title);
      }
    }

    _submit(e) {
      e.preventDefault();
      const that = this;
      const promise = (this.canonical) ? sessionService.update(this.session) : sessionService.create(this.session);
      promise.then((_) => {
        that.$.form.goToStep(1);
        that.$.form.enable();
      });
    }
  }

  Polymer(PageFormSession);
})();