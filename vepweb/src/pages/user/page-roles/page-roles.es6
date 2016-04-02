(function () {
  const userService = window.vep.services.user;

  class PageRoles {
    beforeRegister() {
      this.is = "page-roles";

      this.properties = {
        users: {
          type: Array,
          value: []
        },
        showSave: {
          type: Boolean,
          value: false
        }
      };
    }

    attached() {
      const that = this;
      userService.findAll().then((_) => that.users = _);

      this.addEventListener("click", (e) => {
        if (e.target.classList.contains("change-role")) {
          e.preventDefault();
          const uid = e.target.getAttribute("data-id");
          const user = that.users.find((u) => u.uid == uid);
          const role = e.target.getAttribute("data-role");
          if (e.target.getAttribute("icon") === "icons:check") {
            user.roles = user.roles.filter((_) => _ !== role);
          } else {
            user.roles = user.roles.concat([role]);
          }
          that._sendToServer(user);
          // Force binding reload
          that.users = [].concat(that.users);
        }
      }, true);
    }

    hasRole(row, role) {
      return !!row.roles.find((_) => _ === role);
    }
    
    _sendToServer(user) {
      const that = this;
      userService.updateRoles(user.uid, user.roles).then((_) => {
        that.showSave = true;
      });
    }
  }

  Polymer(PageRoles);
})();