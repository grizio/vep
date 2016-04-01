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
        }
      };
    }

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
      return `width:${width+10}px;height:${height+10}px`;
    }

    _getSeatClass(seat) {
      return "seat " + (seat.t || "normal");
    }

    _getSeatStyle(seat) {
      return `top: ${seat.y}px;left:${seat.x}px;width:${seat.w}px;height:${seat.h}px;line-height:${seat.h}px`
    }
  }

  Polymer(VepApp);
})();