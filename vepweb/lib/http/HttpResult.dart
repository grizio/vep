part of vep.http;

abstract class HttpResult {
  int get statusCode;

  bool get isSuccess;
}

class HttpResultSuccess extends HttpResult {
  final int statusCode;

  bool get isSuccess => true;

  HttpResultSuccess(this.statusCode);
}

class HttpResultSuccessEntity extends HttpResult {
  final int statusCode;

  bool get isSuccess => true;

  final Object entity;

  HttpResultSuccessEntity(this.statusCode, this.entity);
}

class HttpResultError extends HttpResult {
  final int statusCode;

  bool get isSuccess => false;

  final int errorCode;
  final String errorMessage;

  HttpResultError(this.statusCode, this.errorCode, this.errorMessage);
}

class HttpResultErrors extends HttpResult {
  final int statusCode;

  bool get isSuccess => false;

  final Map<String, List<int>> errorCodes;
  final Map<String, List<String>> errorMessages;

  HttpResultErrors(this.statusCode, this.errorCodes, this.errorMessages);
}

class HttpResultUnhandled extends HttpResult {
  final int statusCode;

  bool get isSuccess => false;

  final http.Response response;

  HttpResultUnhandled(this.statusCode, this.response);
}