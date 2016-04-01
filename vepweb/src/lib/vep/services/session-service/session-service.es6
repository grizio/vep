(() => {
  const http = window.vep.http;

  // @singleton
  class SessionService {
    find(theater, session) {
      return http.get(`/session/${theater}/${session}`);
    }

    findAll() {
      return new Promise((resolve, reject) => {
        // Current API adaptation - will need an update
        var process = (page, sessionsStack) => {
          http.get(`/sessions?p=${page}`).then((result) => {
            sessionsStack.push.apply(sessionsStack, result.sessions);
            if (page >= result.pageMax) {
              resolve(sessionsStack);
            } else {
              process(page + 1, sessionsStack);
            }
          })
        };
        process(1, []);
      });
    }
  }

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.session = new SessionService();
})();