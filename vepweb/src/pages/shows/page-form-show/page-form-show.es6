(function () {
  const showService = window.vep.services.show;
  const companyService = window.vep.services.company;
  const stringUtils = window.vep.utils.string;

  class PageFormShow {
    beforeRegister() {
      this.is = "page-form-show";

      this.properties = {
        canonical: {
          type: String,
          value: null
        },
        show: {
          type: Object,
          value: {}
        },
        companies: {
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

      this.observers = [
        "_titleChanged(show.title)"
      ];

      this.listeners = {
        "form.submit": "_submit"
      }
    }

    attached() {
      const that = this;
      if (this.canonical) {
        showService.find(this.canonical).then((show) => {
          show.company = show.company.canonical;
          that.show = show;
        });
        this.$.canonicalField.disabled = true;
      } else {
        this.show = {};
      }
      companyService.findAll().then((companies) => that.companies = companies);
    }

    _computeIsCreation(canonical) {
      return !canonical;
    }

    _computeHasError(errors) {
      return errors && errors.length > 0;
    }

    _titleChanged(title) {
      if (title && !this.canonical) {
        if (stringUtils.isEmpty(this._oldCanonical) && stringUtils.isEmpty(this.show.canonical) || stringUtils.equals(this._oldCanonical, this.show.canonical)) {
          this.$.canonical.value = stringUtils.canonicalize(title);
        }
        this._oldCanonical = stringUtils.canonicalize(title);
      }
    }

    _submit(e) {
      e.preventDefault();
      const promise = (this.canonical) ? showService.update(this.show) : showService.create(this.show);
      promise.then((_) => document.querySelector('app-router').go('/shows'));
    }
  }

  Polymer(PageFormShow);
})();