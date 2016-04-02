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
          value: {}
        },
        _i18nErrorInvalidValue: {
          type: String,
          value: null,
          observer: "_refresh"
        }
      };
    }

    attached() {
      this.addEventListener("value-changed", (e) => {
        e.preventDefault();
        const value = {};
        Array.from(this.querySelectorAll("kwc-field")).forEach((field) => {
          value[field.getAttribute("data-id")] = field.value === "{{prices[item.id]}}" ? 0 : parseInt(field.value);
        });
        this.value = value;
      }, true);
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