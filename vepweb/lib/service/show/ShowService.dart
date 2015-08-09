part of vep.service.show;

@Injectable()
class ShowService {
  static final logger = new Logger('vep.service.show.ShowService');

  final IHttp http;

  ShowService(this.http);

  Map<String, Future> _loading = {};

  Future<ShowSearchResponse> search(ShowSearchCriteria criteria) {
    final query = <String>[
      criteria.title != null ? 't=' + Uri.encodeQueryComponent(criteria.title) : '',
      criteria.author != null ? 'a=' + Uri.encodeQueryComponent(criteria.author) : '',
      criteria.director != null ? 'd=' + Uri.encodeQueryComponent(criteria.director) : '',
      criteria.company != null ? 'c=' + Uri.encodeQueryComponent(criteria.company) : '',
      criteria.order != null ? 'o=' + Uri.encodeQueryComponent(criteria.order) : '',
      criteria.page != null ? 'p=' + criteria.page.toString() : ''
    ].where((_) => _.isNotEmpty).join('&');

    var loading = _loading[query];
    if (loading == null) {
      loading = http.get('/shows?${query}', type: const jsonx.TypeHelper<ShowSearchResponse>().type).then((httpResult){
        if (httpResult.isSuccess) {
          return (httpResult as HttpResultSuccessEntity).entity;
        } else {
          logger.severe('Error when searching shows', httpResult);
          return [];
        }
      });
    }
    return loading.then((_){
      _loading.remove(query);
      return _;
    });
  }
}