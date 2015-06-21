part of vep.service.cms;

@Injectable()
class PageService {
  final IHttp http;

  PageService(this.http);

  Future<HttpResult> create(PageForm pageForm) {
    return http.post('/cms/page/${pageForm.canonical}', pageForm, withSession: true);
  }

  Future<HttpResult> findAll() {
    return http.get('/cms/pages', type: const jsonx.TypeHelper<List<Page>>().type);
  }
}