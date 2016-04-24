(() => {
  class VepApp {
    beforeRegister() {
      this.is = "show-plan";

      this.properties = {
        enabled: {
          type: Boolean,
          value: false
        },
        legend: {
          type: Boolean,
          value: false
        },
        jsonPlan: {
          type: String,
          value: null,
          observer: "_jsonPlanChanged"
        },
        reservedSeats: {
          type: Array,
          value: []
        },
        _seats: {
          type: Array,
          value: []
        },
        _mapClass: {
          type: String,
          compute: "_computeMapClass(enabled)"
        },
        _mapStyles: {
          type: String,
          value: null
        },
        value: {
          type: Array,
          value: [],
          reflectToAttribute: true,
          notify: true,
          observer: "_valueChanged"
        },
        _i18nErrorEmptyValue: {
          type: String,
          value: null,
          observer: "_valueChanged"
        }
      };
    }

    attached() {
      const that = this;

      this.addEventListener("click", (e) => this._seatTap(e, that), true);
      this.addEventListener("touchend", (e) => this._seatTap(e, that), true);
    }

    _seatTap(e, that) {
      if (this.enabled) {
        const classList = e.target.classList;
        if (classList.contains("seat")) {
          e.preventDefault();
          if (!classList.contains("taken")) {
            const seat = that._seats.find((s) => s.c === e.target.textContent);
            seat.selected = !seat.selected;
          }
          this.value = this._seats.filter((s) => !!s.selected);
          const seats = that._seats;
          that._seats = null;
          that.async(() => that._seats = seats);
        }
      }
    }

    // Field behavior compatibility

    checkValidity() {
      return this.value && this.value.length > 0;
    }

    _valueChanged() {
      this.fire("input");
    }
    
    get validationMessage() {
      return this.checkValidity() ? null : this._i18nErrorEmptyValue;
    }

    // Private methods

    _jsonPlanChanged(value) {
      if (value) {
        this._seats = JSON.parse(value);
        this._mapStyles = this._computeMapStyles(this._seats);
      }
    }

    _computeMapClass(enabled) {
      return enabled ? "enabled" : "disabled"
    }

    _computeMapStyles(_seats) {
      let width = 0;
      let height = 0;
      _seats.forEach((_) => {
        width = Math.max(width, _.x + _.w);
        height = Math.max(height, _.y + _.h);
      });
      return `width:${width + 10}px;height:${height + 10}px`;
    }

    _getSeatClass(seat, reservedSeats) {
      if (seat.selected) {
        return "seat selected";
      } else if (reservedSeats.some((s) => s == seat.c)) {
        return "seat taken";
      } else {
        return "seat " + (seat.t || "normal");
      }
    }

    _getSeatStyle(seat) {
      return `top: ${seat.y}px;left:${seat.x}px;width:${seat.w}px;height:${seat.h}px;line-height:${seat.h}px`
    }
  }

  Polymer(VepApp);
})();