(() => {
  const theaterService = window.vep.services.theater;
  const stringUtils = window.vep.utils.string;
  const errorCodes = window.vep.errors;

  class PageFormTheater {
    beforeRegister() {
      this.is = "page-form-theater";

      this.properties = {
        canonical: {
          type: String,
          value: null
        },
        theater: {
          type: Object,
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

      this.observers = [
        "_nameChanged(theater.name)"
      ];

      this.listeners = {
        "form.send": "_submit"
      };
    }

    attached() {
      const that = this;
      if (this.canonical) {
        theaterService.find(this.canonical).then((theater) => that.theater = theater);
        this.$.canonical.disabled = true;
      } else {
        this.theater = {};
      }
    }

    _computeIsCreation(canonical) {
      return !canonical;
    }

    _computeHasError(errors) {
      return errors && errors.length > 0;
    }

    _nameChanged(name) {
      if (name && !this.canonical) {
        if (stringUtils.isEmpty(this._oldCanonical) && stringUtils.isEmpty(this.theater.canonical) || stringUtils.equals(this._oldCanonical, this.theater.canonical)) {
          this.$.canonical.value = stringUtils.canonicalize(name);
        }
        this._oldCanonical = stringUtils.canonicalize(name);
      }
    }

    _submit(e) {
      e.preventDefault();
      const that = this;
      const promise = (this.canonical) ? theaterService.update(this.theater) : theaterService.create(this.theater);
      promise
        .then((_) => document.querySelector('app-router').go('/theaters'))
        .catch((e) => {
          if (e.xhr.status == 400) {
            const errors = [];
            if (e.result._ && e.result._.length > 0) {
              errors.push(errorCodes.find(e.result._[0]));
            }
            that.errors = errors;
            that.$.form.goToStep(1);
            that.$.form.enable();
          }
        });
    }
  }

  Polymer(PageFormTheater);
})();