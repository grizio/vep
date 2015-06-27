part of vep.service.cms;

@Injectable()
class PageService {
  static final logger = new Logger('vep.service.cms.PageService');

  final IHttp http;

  PageService(this.http);

  Future<HttpResult> create(PageForm pageForm) {
    return http.post('/cms/page/${pageForm.canonical}', pageForm, withSession: true).then((_){
      _clearCache();
      return _;
    });
  }

  Future<HttpResult> update(PageForm pageForm) {
    return http.put('/cms/page/${pageForm.canonical}', pageForm, withSession: true).then((_){
      _clearCache();
      return _;
    });
  }

  Future<List<Page>> findAll() {
    return _prepare().then((_){
      if (_) {
        return _pages;
      } else {
        return [];
      }
    });
  }

  Future<Page> find(String canonical) {
    return _prepare().then((_){
      if (_) {
        return _pages.firstWhere((p) => p.canonical == canonical, orElse: () => null);
      } else {
        return null;
      }
    });
  }

  Future<List<Page>> findForMenu() {
    return _prepare().then((_){
      if (_) {
        var pages = _pages.where((p) => p.menu != null).toList();
        pages.sort((Page p1, Page p2) => p2.menu - p1.menu);
        return pages;
      } else {
        return [];
      }
    });
  }

  Future _loading;

  /// Checks if the list of pages was previously loaded.
  /// Returns a Future indicating that pages are loaded (because of cache or after getting from server).
  Future<bool> _prepare() {
    if (_loading == null) {
      if (_isCacheExpired()) {
        _loading = http.get('/cms/pages', type: const jsonx.TypeHelper<List<Page>>().type).then((httpResult){
          if (httpResult.isSuccess) {
            _pages = (httpResult as HttpResultSuccessEntity).entity as List<Page>;
            window.localStorage['page-cache-start'] = new DateTime.now().millisecondsSinceEpoch.toString();
            return true;
          } else {
            logger.severe('Error when loading list of pages', httpResult);
            return false;
          }
        });
      } else {
        _loading = new Future.value(true);
      }
    }
    return _loading.then((_){
      _loading = null;
      return _;
    });
  }

  bool _isCacheExpired() {
    if (window.localStorage.containsKey('pages-cache-start')) {
      var pageCache = JSON.decode(window.localStorage['pages-cache-start']);
      var expire = new DateTime.fromMillisecondsSinceEpoch(pageCache).add(new Duration(hours: 1));
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

  List<Page> get _pages {
    if (window.localStorage.containsKey('pages-cache')) {
      return jsonx.decode(window.localStorage['pages-cache'], type: const jsonx.TypeHelper<List<Page>>().type);
    } else {
      return [];
    }
  }

  set _pages(List<Page> pages) {
    window.localStorage['pages-cache'] = jsonx.encode(pages);
  }

  void _clearCache() {
    window.localStorage.remove('pages-cache');
    window.localStorage.remove('pages-cache-start');
  }
}