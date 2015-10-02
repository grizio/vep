part of vep.service.cms;

@Injectable()
class ContactService {
  static final logger = new Logger('vep.service.cms.ContactService');

  final IHttp http;

  ContactService(this.http);

  Future<HttpResult> post(Contact contact) {
    return http.post('/contact', contact);
  }
}