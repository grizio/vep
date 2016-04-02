(() => {
  const companyService = window.vep.services.company;
  const stringUtils = window.vep.utils.string;

  class PageFormCompany {
    beforeRegister() {
      this.is = "page-form-company";

      this.properties = {
        canonical: {
          type: String,
          value: null
        },
        company: {
          type: Object,
          value: {}
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
        "_nameChanged(company.name)"
      ];

      this.listeners = {
        "form.submit": "_submit"
      }
    }

    attached() {
      const that = this;
      if (this.canonical) {
        companyService.find(this.canonical).then((company) => that.company = company);
        this.$.canonical.disabled = true;
      } else {
        this.company = {};
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
        if (stringUtils.isEmpty(this._oldCanonical) && stringUtils.isEmpty(this.company.canonical) || stringUtils.equals(this._oldCanonical, this.company.canonical)) {
          this.$.canonical.value = stringUtils.canonicalize(name);
        }
        this._oldCanonical = stringUtils.canonicalize(name);
      }
    }

    _submit(e) {
      e.preventDefault();
      const promise = (this.canonical) ? companyService.update(this.company) : companyService.create(this.company);
      promise.then((_) => document.querySelector('app-router').go('/companies'));
    }
  }

  Polymer(PageFormCompany);
})();