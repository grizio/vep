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

  Future<HttpResult> create(ShowForm show) {
    return http.post('/show/' + show.canonical, show, withSession: true);
  }

  Future<HttpResult> update(ShowForm show) {
    return http.put('/show/${show.canonical}', show, withSession: true);
  }

  Future<Show> find(String canonical) {
    return http.get('/show/${canonical}', type: Show).then((_){
      if (_.isSuccess) {
        return (_ as HttpResultSuccessEntity).entity;
      } else {
        return null;
      }
    });
  }

  Future<List<Choice<String>>> findCanonicalByTitle(String title) {
    final criteria = new ShowSearchCriteria();
    criteria.title = title;
    return search(criteria).then((ShowSearchResponse response){
      if (response.shows.isNotEmpty) {
        final show = response.shows.firstWhere((_) => _.title == title, orElse: () => response.shows.first);
        final result = [show];
        result.addAll(response.shows.where((_) => _.canonical != show.canonical));
        return result.map((_) => new Choice(_.canonical, _.title)).toList();
      } else {
        return null;
      }
    });
  }
}