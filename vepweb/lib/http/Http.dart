part of vep.http;

abstract class IHttp {
  static const GET = 'GET';
  static const POST = 'POST';
  static const PUT = 'PUT';
  static const DELETE = 'DELETE';

  Future<HttpResult> get(String url);

  Future<HttpResult> post(String url, Object entity);

  Future<HttpResult> put(String url, Object entity);

  Future<HttpResult> delete(String url);
}

@Injectable()
class HttpProduction implements IHttp {
  final logger = new Logger('vep.http.HttpProduction');
  final http.Client client;
  final App app;

  HttpProduction(this.client, this.app);

  Map<String, String> buildHeaders({Map<String, String> headers:const{}, bool withSession: false}) {
    var result = <String, String>{};
    result.addAll(headers);
    if (app.isLoggedIn) {
      headers['authorization'] = 'Basic ' + CryptoUtils.bytesToBase64(UTF8.encode(app.username + ':' + app.key));
    }
    return headers;
  }

  @override
  Future<HttpResult> get(String url, {bool withSession: false}) {
    logger.fine('GET ' + url);
    return client.get(
        getUrl(url),
        headers: buildHeaders(withSession: withSession)
    ).then(complete);
  }

  @override
  Future<HttpResult> post(String url, Object entity, {bool withSession: false}) {
    logger.fine('POST ' + url + ' (' + jsonx.encode(entity) + ')');
    return client.post(
        getUrl(url),
        body: jsonx.encode(entity),
        headers: buildHeaders(headers: {'Content-Type': 'application/json'}, withSession: withSession)
    ).then(complete);
  }

  @override
  Future<HttpResult> put(String url, Object entity, {bool withSession: false}) {
    logger.fine('PUT ' + url + ' (' + jsonx.encode(entity) + ')');
    return client.put(
        getUrl(url),
        body: jsonx.encode(entity),
        headers: buildHeaders(headers: {'Content-Type': 'application/json'}, withSession: withSession)
    ).then(complete);
  }

  @override
  Future<HttpResult> delete(String url, {bool withSession: false}) {
    logger.fine('DELETE ' + url);
    return client.delete(
        getUrl(url),
        headers: buildHeaders(withSession: withSession)
    ).then(complete);
  }

  String getUrl(String url) {
    String result = constants.host;
    if (!result.endsWith('/')) {
      result += '/';
    }
    if (url.startsWith('/')) {
      result += url.substring(1);
    } else {
      result += url;
    }
    return result;
  }

  Future<HttpResult> complete(http.Response response) {
    if (response.statusCode == 0) {
      logger.warning("The returned status was 0. It seems it was not done by a server.");
      return new Future.value(new HttpResultSuccess(response.statusCode, response.body));
    } else if (response.statusCode >= 200 && response.statusCode < 300) {
      return new Future.value(new HttpResultSuccess(response.statusCode, response.body));
    } else if (response.statusCode >= 300 && response.statusCode < 400) {
      logger.severe('This kind of status is not managed by this implementation of Http.');
      return new Future.value(new HttpResultUnhandled(response.statusCode, response));
    }
    else if (response.statusCode >= 400 && response.statusCode < 500) {
      try {
        var json = jsonx.decode(response.body);
        if (json is int) {
          return createHttpResultError(response.statusCode, json);
        } else if (json is Map) {
          return createHttpResultErrors(response.statusCode, json);
        } else {
          return new Future.value(new HttpResultUnhandled(response.statusCode, response));
        }
      } catch (e) {
        // Error when parsing json because of a raw exception.
        return new Future.value(new HttpResultUnhandled(response.statusCode, response));
      }
    } else {
      logger.severe('This kind of status is not managed by this implementation of Http.');
      return new Future.value(new HttpResultUnhandled(response.statusCode, response));
    }
  }

  Future<HttpResultError> createHttpResultError(int statusCode, int errorCode) {
    return new Future.value(new HttpResultError(statusCode, errorCode, i18n[errorCode]));
  }

  Future<HttpResultErrors> createHttpResultErrors(int statusCode, Map<String, List<int>> errors) {
    var errorMessages = <String, List<String>>{};
    errors.forEach((field, errorCodes) {
      errorMessages[field] = errorCodes.map((errorCode) => i18n[errorCode]);
    });
    return new Future.value(new HttpResultErrors(statusCode, errors, errorMessages));
  }
}