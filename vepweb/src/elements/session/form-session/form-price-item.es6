(() => {
  class FormPriceItem {
    beforeRegister() {
      this.is = "form-price-item";

      this.properties = {
        value: {
          type: Object,
          value: null,
          notify: true,
          reflectToAttribute: true
        },
        validationMessage: {
          type: String,
          value: null
        },
        messageCorrectChildren: {
          type: String,
          value: "Please correct field values"
        }
      };

      this.listeners = {
        "remove.tap": "_removeTap",
        "name.value-changed": "_update",
        "price.value-changed": "_update",
        "condition.value-changed": "_update"
      }
    }

    _removeTap(e) {
      e.preventDefault();
      this.fire("remove");
    }

    _update(e) {
      this.value = {
        name: this.$.name.value,
        price: this.$.price.value,
        condition: this.$.condition.value
      }
    }

    checkValidity() {
      if (!this.$.name.hasNotError || !this.$.price.hasNotError || !this.$.condition.hasNotError) {
        this.validationMessage = this.messageCorrectChildren;
        return false;
      } else {
        return true;
      }
    }
  }

  Polymer(FormPriceItem);
})();