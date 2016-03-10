(() => {
  const http = window.vep.http;

  // @singleton
  class SessionService {
    find(theater, session) {
      return http.get(`/session/${theater}/${session}`);
    }
  }

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.session = new SessionService();
})();