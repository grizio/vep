part of vep.http.dev;

var ClientInMemory = new MockClient((http.Request request){
  if (request.url.path.startsWith(new RegExp(r'/?user')) || new RegExp('/?login/?').hasMatch(request.url.path)) {
    return ClientInMemoryUser(request);
  } else {
    return new http.Response('', 404);
  }
});