part of vep.http.dev;

var ClientInMemory = new MockClient((http.Request request){
  if (request.url.path.startsWith(new RegExp(r'/?user'))) {
    return ClientInMemoryUser(request);
  } else {
    return new http.Response('', 404);
  }
});