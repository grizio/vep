part of vep.service.session;

@Injectable()
class SessionService {
  static final logger = new Logger('vep.service.sesson.SessionService');

  final IHttp http;

  SessionService(this.http);

  Future<HttpResult> create(SessionForm session) {
    return http.post('/session/${session.theater}', session, withSession: true);
  }
}