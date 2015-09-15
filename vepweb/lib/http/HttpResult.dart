part of vep.http;

abstract class HttpResult {
  int get statusCode;

  bool get isSuccess;
}

class HttpResultSuccess extends HttpResult {
  final int statusCode;
  final String body;

  @override
  bool get isSuccess => true;

  HttpResultSuccess(this.statusCode, this.body);

  @override
  String toString() => '[HttpResultSuccess][$statusCode] $body';
}

class HttpResultSuccessEntity extends HttpResult {
  final int statusCode;

  @override
  bool get isSuccess => true;

  final Object entity;

  HttpResultSuccessEntity(this.statusCode, this.entity);

  @override
  String toString() => '[HttpResultSuccessEntity][$statusCode] ${entity.toString()}';
}

class HttpResultError extends HttpResult {
  final int statusCode;

  @override
  bool get isSuccess => false;

  final int errorCode;
  final String errorMessage;

  HttpResultError(this.statusCode, this.errorCode, this.errorMessage);

  @override
  String toString() => '[HttpResultError][$statusCode][$errorCode] $errorMessage';
}

class HttpResultErrors extends HttpResult {
  final int statusCode;

  @override
  bool get isSuccess => false;

  final Map<String, List<int>> errorCodes;
  final Map<String, List<String>> errorMessages;

  HttpResultErrors(this.statusCode, this.errorCodes, this.errorMessages);

  @override
  String toString() => '[HttpResultErrors][$statusCode] {${errorCodes.toString()}} {${errorMessages.toString()}}';
}

class HttpResultUnhandled extends HttpResult {
  final int statusCode;

  @override
  bool get isSuccess => false;

  final http.Response response;

  HttpResultUnhandled(this.statusCode, this.response);

  @override
  String toString() => '[HttpResultUnhandled][$statusCode] ${response.body}';
}