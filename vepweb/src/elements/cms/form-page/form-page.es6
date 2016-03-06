(() => {
  const pageService = window.vep.services.page;
  const stringUtils = window.vep.utils.string;

  class PageUpdatePage {
    beforeRegister() {
      this.is = "form-page";

      this.properties = {
        canonical: {
          type: String,
          value: null
        },
        page: {
          type: Object,
          value: null
        }
      };

      this.observers = [
        "_titleChanged(page.title)"
      ];

      this.listeners = {
        "form.submit": "_submit"
      };
    }

    attached() {
      const that = this;

      if (this.canonical) {
        pageService.find(this.canonical).then((page) => that.page = page);
        this.$.canonical.disabled = true;
      } else {
        this.page = {};
      }
    }

    _titleChanged(title) {
      if (title && !this.canonical) {
        if (stringUtils.isEmpty(this._oldCanonical) && stringUtils.isEmpty(this.page.canonical) || stringUtils.equals(this._oldCanonical, this.page.canonical)) {
          this.$.canonicalField.value = stringUtils.canonicalize(title);
        }
        this._oldCanonical = stringUtils.canonicalize(title);
      }
    }

    _submit(e) {
      e.preventDefault();
      const that = this;
      const promise = (this.canonical) ? pageService.update(this.page) : pageService.create(this.page);
      promise.then((_) => that.fire("sent"));
    }
  }

  Polymer(PageUpdatePage);
})();