part of vep.service.theater;

@Injectable()
class TheaterService {
  static const theatersCache = 'theaters-cache';
  static const theatersCacheStart = 'theaters-cache-start';

  static final logger = new Logger('vep.service.theater.TheaterService');

  final IHttp http;

  TheaterService(this.http);

  Future<List<Theater>> findAll() {
    return _prepare().then((_) {
      if (_) {
        return _theaters;
      } else {
        return [];
      }
    });
  }

  Future<HttpResult> create(TheaterForm theater) {
    return http.post('/theater/' + theater.canonical, theater, withSession: true).then((_) {
      _clearCache();
      return _;
    });
  }

  Future<HttpResult> update(TheaterForm theater) {
    return http.put('/theater/${theater.canonical}', theater, withSession: true).then((_) {
      _clearCache();
      return _;
    });
  }

  Future<Theater> find(String canonical) {
    return _prepare().then((_) {
      if (_) {
        return _theaters.firstWhere((t) => t.canonical == canonical, orElse: () => null);
      } else {
        return null;
      }
    });
  }

  Future _loading;

  /// Checks if the list of theaters was previously loaded.
  /// Returns a Future indicating that pages are loaded (because of cache or after getting from server).
  Future<bool> _prepare() {
    if (_loading == null) {
      if (_isCacheExpired()) {
        _loading = http.get('/theaters', type: const jsonx.TypeHelper<List<Theater>>().type).then((httpResult) {
          if (httpResult.isSuccess) {
            _theaters = (httpResult as HttpResultSuccessEntity).entity as List<Theater>;
            window.localStorage[theatersCacheStart] = new DateTime.now().millisecondsSinceEpoch.toString();
            return true;
          } else {
            logger.severe('Error when loading list of theaters', httpResult);
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
    if (window.localStorage.containsKey(theatersCacheStart)) {
      var dtCache = JSON.decode(window.localStorage[theatersCacheStart]);
      var expire = new DateTime.fromMillisecondsSinceEpoch(dtCache).add(new Duration(minutes: 1));
      if (expire.isBefore(new DateTime.now())) {
        _clearCache();
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  List<Theater> get _theaters {
    if (window.localStorage.containsKey(theatersCache)) {
      return jsonx.decode(window.localStorage[theatersCache], type: const jsonx.TypeHelper<List<Theater>>().type);
    } else {
      return [];
    }
  }

  set _theaters(List<Theater> theaters) {
    window.localStorage[theatersCache] = jsonx.encode(theaters);
  }

  void _clearCache() {
    window.localStorage.remove(theatersCache);
    window.localStorage.remove(theatersCacheStart);
  }
}