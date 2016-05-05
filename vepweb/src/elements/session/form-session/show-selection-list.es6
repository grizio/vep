(() => {
  class ShowSelectionList {
    beforeRegister() {
      this.is = "show-selection-list";

      this.properties = {
        value: {
          type: Array,
          value: [],
          notify: true,
          reflectToAttribute: true
        },
        validationMessage: {
          type: String,
          value: null
        },
        messageDouble: {
          type: String,
          value: "Please select once each show"
        }
      };

      this.listeners = {
        "add.tap": "_addTap"
      }
    }

    attached() {
      const that = this;
      this.addEventListener("remove", (e) => that._remove(e), true);
      this.addEventListener("value-changed", (e) => that._valueChanged(e), true);
    }

    _addTap(e) {
      e.preventDefault();
      Polymer.dom(this.$.shows).appendChild(document.createElement("show-selection-item"));
      this._update();
    }

    _remove(e) {
      e.preventDefault();
      Polymer.dom(this.$.shows).removeChild(e.target);
      this._update();
    }

    _valueChanged(e) {
      if (e.target !== this) {
        e.preventDefault();
        this._update();
      }
    }

    _update() {
      this.value = Array.from(this.querySelectorAll("show-selection-item"))
        .map((select) => select.value)
        .filter((val) => !!val);
    }

    // kwc-field compatibility
    checkValidity() {
      if (this.value) {
        const counts = {};
        this.value.forEach((item) => counts[item] = (counts[item] || 0) + 1);
        for (let c in counts) {
          if (counts.hasOwnProperty(c)) {
            if (counts[c] > 1) {
              this.validationMessage = this.messageDouble;
              return false;
            }
          }
        }
        return true;
      } else {
        return true;
      }
    }
  }

  Polymer(ShowSelectionList);
})();