(() => {
  const pageService = window.vep.services.page;

  class VepPage {
    beforeRegister() {
      this.is = "vep-page";

      this.properties = {
        canonical: {
          type: String,
          value: null
        },
        _page: {
          type: Object,
          value: null,
          observer: "_pageChanged"
        },
        _done: {
          type: Boolean,
          value: false
        }
      }
    }

    attached() {
      const that = this;
      pageService.find(this.canonical)
        .then((page) => {
          that._page = page;
          that._done = true;
        });
    }

    _pageChanged(value) {
      if (value) {
        this.$.content.innerHTML = value.content;
      }
    }
  }

  Polymer(VepPage);
})();