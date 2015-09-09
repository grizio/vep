part of vep.service.session;

@Injectable()
class SessionService {
  static final logger = new Logger('vep.service.sesson.SessionService');

  final IHttp http;

  SessionService(this.http);

  Future<HttpResult> create(SessionForm session) {
    return http.post('/session/${session.theater}', session, withSession: true);
  }

  Map<String, Future> _loading = {};

  Future<SessionSearchResponse> search(SessionSearchCriteria criteria) {
    final query = <String>[
      criteria.theater != null ? 't=' + Uri.encodeQueryComponent(criteria.theater) : '',
      criteria.show != null ? 's=' + Uri.encodeQueryComponent(criteria.show) : '',
      criteria.startDate != null ? 'sd=' + Uri.encodeQueryComponent(criteria.startDate.toIso8601String()) : '',
      criteria.endDate != null ? 'ed=' + Uri.encodeQueryComponent(criteria.endDate.toIso8601String()) : '',
      criteria.order != null ? 'o=' + Uri.encodeQueryComponent(criteria.order) : '',
      criteria.page != null ? 'p=' + criteria.page.toString() : ''
    ].where((_) => _.isNotEmpty).join('&');

    var loading = _loading[query];
    if (loading == null) {
      loading = http.get('/sessions?${query}', type: const jsonx.TypeHelper<SessionSearchResponse>().type).then((httpResult) {
        if (httpResult.isSuccess) {
          return (httpResult as HttpResultSuccessEntity).entity;
        } else {
          logger.severe('Error when searching sessions', httpResult);
          return [];
        }
      });
    }
    return loading.then((_) {
      _loading.remove(query);
      return _;
    });
  }
}