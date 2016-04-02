(() => {
  class InputHtml {
    beforeRegister() {
      this.is = "input-html";

      this.properties = {
        value: {
          type: String,
          value: "",
          reflectToAttribute: true,
          notify: true,
          observer: "_valueChanged"
        },

        required: {
          type: Boolean,
          value: false
        },

        validationMessage: {
          type: String,
          computed: "_computeValidationMessage(value, required, _i18nErrorRequired)"
        },

        _i18nErrorRequired: {
          type: String,
          value: null,
          // Needs a refresh because error was not set
          observer: "_refresh"
        }
      };

      this.listeners = {
        "editor.change": "editorChange"
      };
    }

    editorChange(e) {
      console.log(this.$.editor.value);
      this.value = this.$.editor.value;
    }

    attached() {
    }

    checkValidity() {
      return !this.validationMessage;
    }

    _computeValidationMessage(value, required, i18nErrorRequired) {
      if (required && (!value || value === "")) {
        return i18nErrorRequired;
      } else {
        return null;
      }
    }

    _valueChanged(value) {
      if (this.$.editor.value !== value) {
        this.$.editor.value = value;
      }
      this._refresh();
    }

    _refresh() {
      this.fire("input");
    }
  }

  Polymer(InputHtml);
})();