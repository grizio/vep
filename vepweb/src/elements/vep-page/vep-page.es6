(() => {
  const http = window.vep.http;

  let pages = null;

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
        }
      }
    }

    attached() {
      const that = this;
      let promise;
      if (!pages) {
        promise = http.get("/cms/pages").then((result) => {
          pages = result;
          return pages;
        });
      } else {
        promise = Promise.resolve(pages);
      }
      promise.then((pages) => {
        that._page = pages.find((p) => p.canonical === that.canonical);
      });
    }

    _pageChanged(value) {
      console.log(value);
      if (value) {
        this.$.content.innerHTML = value.content;
      }
    }
  }

  Polymer(VepPage);
})();