(() => {
  const showService = window.vep.services.show;

  class ShowSelectionItem {
    beforeRegister() {
      this.is = "show-selection-item";

      this.properties = {
        value: {
          type: String,
          value: null,
          notify: true,
          reflectToAttribute: true
        },
        validationMessage: {
          type: String,
          value: null
        },
        _shows: {
          type: Array,
          value: []
        }
      };

      this.listeners = {
        "field.change": "_fieldChanged",
        "remove.tap": "_removeTap"
      }
    }

    attached() {
      const that = this;
      showService.findAll().then((shows) => that._shows = shows);
    }

    _fieldChanged(e) {
      e.preventDefault();
      if (this.value !== this.$.field.value) {
        this.value = this.$.field.value;
      }
    }

    _removeTap(e) {
      e.preventDefault();
      this.fire("remove");
    }
  }

  Polymer(ShowSelectionItem);
})();