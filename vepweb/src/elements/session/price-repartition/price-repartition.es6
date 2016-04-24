(() => {
  class PriceRepartition {
    beforeRegister() {
      this.is = "price-repartition";

      this.properties = {
        prices: {
          type: Array,
          value: []
        },
        number: {
          type: Number,
          value: 0,
          observer: "_refresh"
        },
        value: {
          type: Object,
          value: {},
          reflectToAttribute: true,
          notify: true
        },
        _i18nErrorInvalidValue: {
          type: String,
          value: null,
          observer: "_refresh"
        }
      };
    }

    attached() {
      const that = this;
      this.addEventListener("value-changed", (e) => {
        e.preventDefault();
        const value = {};
        Array.from(that.querySelectorAll("kwc-field")).forEach((field) => {
          value[field.getAttribute("data-id")] = field.value === "{{prices[item.id]}}" ? 0 : parseInt(field.value);
        });
        if (!that._isSameObject(that.value, value)) {
          that.value = value;
        }
      }, true);
    }

    _isSameObject(obj1, obj2) {
      for (let key in obj1) {
        if (obj1.hasOwnProperty(key)) {
          if (obj1[key] !== obj2[key]) {
            return false;
          }
        }
      }
      for (let key in obj2) {
        if (obj2.hasOwnProperty(key)) {
          if (obj2[key] !== obj1[key]) {
            return false;
          }
        }
      }
      return true;
    }

    // kwc-field compatibility

    checkValidity() {
      if (this.value) {
        let total = 0;
        for (let key in this.value) {
          if (this.value.hasOwnProperty(key)) {
            total += this.value[key];
          }
        }
        return total == this.number;
      } else {
        return false;
      }
    }

    get validationMessage() {
      return this.checkValidity() ? null : this._i18nErrorInvalidValue;
    }

    _refresh() {
      this.fire("input");
    }
  }

  Polymer(PriceRepartition);
})();