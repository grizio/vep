part of vep.service.user;

@Injectable()
class UserService {
  final IHttp http;

  UserService(this.http);

  Future<HttpResult> register(UserRegistration user) {
    return http.post('/user/register', user, doNotLog: true);
  }

  Future<HttpResult> login(UserLogin user) {
    return http.post('/login', user, doNotLog: true);
  }

  Future<HttpResult> getRoles(String email, String key) {
    return http.get('/user/roles', headers: {'authorization': 'Basic ' + CryptoUtils.bytesToBase64(UTF8.encode(email + ':' + key))});
  }

  Future<HttpResult> getUserList() {
    return http.get('/user/list', withSession: true, type: const jsonx.TypeHelper<List<User>>().type);
  }

  Future<HttpResult> updateRoles(int uid, List<String> roles) {
    return http.post('/user/roles/${uid}', roles, withSession: true);
  }
}