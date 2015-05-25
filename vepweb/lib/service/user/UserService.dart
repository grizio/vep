part of vep.service.user;

@Injectable()
class UserService {
  final IHttp http;

  UserService(this.http);

  Future<HttpResult> register(UserRegistration user) {
    return http.post('/user/register', user);
  }

  Future<HttpResult> login(UserLogin user) {
    return http.post('/login', user);
  }
}