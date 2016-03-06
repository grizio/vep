(function () {
  const ContentType = window.vep.ContentType;
  const ContentTypes = window.vep.ContentTypes;
  const objectUtils = window.vep.utils.object;

  const CONNECT = "CONNECT";
  const DELETE = "DELETE";
  const GET = "GET";
  const HEAD = "HEAD";
  const OPTIONS = "OPTIONS";
  const PATCH = "PATCH";
  const POST = "POST";
  const PUT = "PUT";
  const TRACE = "TRACE";

  const defaultOptions = {
    contentType: ContentTypes.ApplicationJson
  };

  class RequestBuilder {
    constructor(options) {
      this._options = objectUtils.clone(options || {});
      Object.freeze(this._options);
      Object.freeze(this);
    }

    withMethod(method) {
      return new RequestBuilder(objectUtils.merge({method: method}, this._options));
    }

    withUrl(url) {
      return new RequestBuilder(objectUtils.merge({url: url}, this._options));
    }

    withHost(host) {
      return new RequestBuilder(objectUtils.merge({host: host}, this._options));
    }

    withEntity(entity) {
      return new RequestBuilder(objectUtils.merge({entity: entity}, this._options));
    }

    withContentType(contentType) {
      if (contentType instanceof ContentType) {
        return new RequestBuilder(objectUtils.merge({contentType: contentType}, this._options));
      } else {
        throw "Please give a valid `ContentType` object from module `lib/k/ref/http/contentTypes`.";
      }
    }

    withHeader(field, value) {
      const headers = objectUtils.clone(this._options.headers || {});
      headers[field] = value;
      return new RequestBuilder(objectUtils.merge({headers: Object.freeze(headers)}, this._options));
    }

    send() {
      const options = objectUtils.merge(this._options, defaultOptions);
      return new Promise(function (resolve, reject) {
        const uri = buildURI(options.url, options.host);
        const xhr = new XMLHttpRequest();
        xhr.open(options.method, uri, true);
        if (options.contentType.isNotNone) {
          xhr.setRequestHeader("Content-Type", options.contentType.toString());
        }
        //if (session.isDefined) {
        //  xhr.setRequestHeader("Authorization", "Basic " + session.authorization);
        //}
        objectUtils.foreachKeyValue(options.headers, (field, value) => {
          xhr.setRequestHeader(field, value);
        });

        xhr.onreadystatechange = function () {
          processReadyStateChange(xhr, resolve, reject);
        };

        if (options.entity) {
          if (ContentTypes.ApplicationJson.equals(options.contentType)) {
            xhr.send(JSON.stringify(options.entity));
          } else {
            xhr.send(options.entity);
          }
        } else {
          xhr.send();
        }
      });
    }
  }

  // @singleton = http
  class Http {
    get(url) {
      return new RequestBuilder()
        .withMethod(GET)
        .withUrl(url)
        .send();
    }

    post(url, entity) {
      return new RequestBuilder()
        .withMethod(POST)
        .withUrl(url)
        .withEntity(entity)
        .send();
    }

    put(url, entity) {
      return new RequestBuilder()
        .withMethod(PUT)
        .withUrl(url)
        .withEntity(entity)
        .send();
    }

    delete(url) {
      return new RequestBuilder()
        .withMethod(DELETE)
        .withUrl(url)
        .send();
    }
  }

  // @private
  function buildURI(url, host) {
    if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("//")) {
      return Promise.resolve(url);
    } else {
      if (host) {
        let result = "//" + (host === "this" ? window.location.host : host);
        result += result.endsWith("/") ? "" : "/";
        result += url.startsWith("/") ? url.substr(1) : url;
        return result;
      } else {
        const remote = window.vep.config.remote;
        let result = "//";
        if (remote.startsWith("//")) {
          result += remote.substr(2);
        } else if (remote.startsWith("/")) {
          result += remote.substr(1);
        } else {
          result += remote;
        }
        result += result.endsWith("/") ? "" : "/";
        result += url.startsWith("/") ? url.substr(1) : url;
        return result;
      }
    }
  }

  // @private
  function processReadyStateChange(xhr, resolve, reject) {
    if (xhr.readyState == 4) {
      if (xhr.status == 200) {
        try {
          resolve(JSON.parse(xhr.responseText))
        } catch (e) {
          resolve(xhr.response)
        }
      } else if (xhr.status == 0) {
        reject({
          result: {
            success: false,
            value: "connection error"
          },
          xhr: xhr
        })
      } else {
        if (xhr.status == 401 || xhr.status == 403) {
          //session.logout()
        }
        try {
          reject({
            result: JSON.parse(xhr.responseText),
            xhr: xhr
          })
        } catch (e) {
          reject({
            result: xhr.response,
            xhr: xhr
          })
        }
      }
    }
  }

  window.vep = window.vep || {};
  window.vep.RequestBuilder = RequestBuilder;
  window.vep.http = new Http();
  window.vep.methods = Object.freeze({
    CONNECT: CONNECT,
    DELETE: DELETE,
    GET: GET,
    HEAD: HEAD,
    OPTIONS: OPTIONS,
    PATCH: PATCH,
    POST: POST,
    PUT: PUT,
    TRACE: TRACE
  });
})();