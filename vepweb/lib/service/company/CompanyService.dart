part of vep.service.company;

@Injectable()
class CompanyService {
  static const companiesCache = 'companies-cache';
  static const companiesCacheStart = 'companies-cache-start';

  static final logger = new Logger('vep.service.company.CompanyService');

  final IHttp http;

  CompanyService(this.http);

  Future<List<Company>> findAll() {
    return _prepare().then((_) {
      if (_) {
        return _companies;
      } else {
        return [];
      }
    });
  }

  Future<HttpResult> create(CompanyForm company) {
    return http.post('/company/' + company.canonical, company, withSession: true).then((_) {
      _clearCache();
      return _;
    });
  }

  Future<HttpResult> update(CompanyForm company) {
    return http.put('/company/${company.canonical}', company, withSession: true).then((_) {
      _clearCache();
      return _;
    });
  }

  Future<Company> find(String canonical) {
    return _prepare().then((_) {
      if (_) {
        return _companies.firstWhere((c) => c.canonical == canonical, orElse: () => null);
      } else {
        return null;
      }
    });
  }

  Future _loading;

  /// Checks if the list of companies was previously loaded.
  /// Returns a Future indicating that pages are loaded (because of cache or after getting from server).
  Future<bool> _prepare() {
    if (_loading == null) {
      if (_isCacheExpired()) {
        _loading = http.get('/companies', type: const jsonx.TypeHelper<List<Company>>().type).then((httpResult) {
          if (httpResult.isSuccess) {
            _companies = (httpResult as HttpResultSuccessEntity).entity as List<Company>;
            window.localStorage[companiesCacheStart] = new DateTime.now().millisecondsSinceEpoch.toString();
            return true;
          } else {
            logger.severe('Error when loading list of companies', httpResult);
            return false;
          }
        });
      } else {
        _loading = new Future.value(true);
      }
    }
    return _loading.then((_) {
      _loading = null;
      return _;
    });
  }

  bool _isCacheExpired() {
    if (window.localStorage.containsKey(companiesCacheStart)) {
      var dtCache = JSON.decode(window.localStorage[companiesCacheStart]);
      var expire = new DateTime.fromMillisecondsSinceEpoch(dtCache).add(new Duration(hours: 1));
      if (expire.isAfter(new DateTime.now())) {
        _clearCache();
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  List<Theater> get _companies {
    if (window.localStorage.containsKey(companiesCache)) {
      return jsonx.decode(window.localStorage[companiesCache], type: const jsonx.TypeHelper<List<Company>>().type);
    } else {
      return [];
    }
  }

  set _companies(List<Company> companies) {
    window.localStorage[companiesCache] = jsonx.encode(companies);
  }

  void _clearCache() {
    window.localStorage.remove(companiesCache);
    window.localStorage.remove(companiesCacheStart);
  }
}