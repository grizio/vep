(() => {
  const http = window.vep.http;

  // @singleton
  class UserService {
    //_theatersPromise

    findAll() {
      return http.get(`/user/list`);
    }

    updateRoles(uid, roles) {
      return http.post(`/user/roles/${uid}`, roles);
    }
  }

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.user = new UserService();
})();