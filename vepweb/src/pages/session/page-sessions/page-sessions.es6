(function () {
  const sessionService = window.vep.services.session;

  class PageSessions {
    beforeRegister() {
      this.is = "page-sessions";

      this.properties = {
        sessions: {
          type: Array,
          value: []
        }
      }
    }

    attached() {
      const that = this;
      sessionService.findAll().then((sessions) => that.sessions = sessions);
    }
  }

  Polymer(PageSessions);
})();