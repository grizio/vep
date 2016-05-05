(() => {
  class FormPriceList {
    beforeRegister() {
      this.is = "form-price-list";

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
          value: "Do not set twice a price"
        },
        messageInvalid: {
          type: String,
          value: "Please complete all invalid fields"
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
      Polymer.dom(this.$.prices).appendChild(document.createElement("form-price-item"));
      this._update();
    }

    _remove(e) {
      e.preventDefault();
      Polymer.dom(this.$.prices).removeChild(e.target);
      this._update();
    }

    _valueChanged(e) {
      if (e.target !== this) {
        e.preventDefault();
        this._update();
      }
    }

    _update() {
      this.value = Array.from(this.querySelectorAll("form-price-item"))
        .map((select) => select.value)
        .filter((val) => !!val);
    }

    // kwc-field compatibility
    checkValidity() {
      return !this.value || this._checkInvalidFields() && this._checkDuplicate();
    }

    _checkDuplicate() {
      const counts = {};
      this.value.forEach((item) => counts[item.price] = (counts[item.price] || 0) + 1);
      for (let c in counts) {
        if (counts.hasOwnProperty(c)) {
          if (counts[c] > 1) {
            this.validationMessage = this.messageDouble;
            return false;
          }
        }
      }
      return true;
    }

    _checkInvalidFields() {
      if (!Array.from(this.querySelectorAll("form-price-item")).every((_) => _.checkValidity())) {
        this.validationMessage = this.messageInvalid;
        return false;
      } else {
        return true;
      }
    }
  }

  Polymer(FormPriceList);
})();