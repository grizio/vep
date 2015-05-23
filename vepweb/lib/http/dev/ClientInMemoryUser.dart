part of vep.http.dev;

http.Response ClientInMemoryUser(http.Request request) {
  if (new RegExp(r"/?user/register/?").hasMatch(request.url.path)) {
    UserRegistration ur = jsonx.decode(request.body, type: UserRegistration);
    if (ur.email == null || ur.firstName == null || ur.lastName == null || ur.password == null) {
      return new http.Response(malformedRequestContent, 400);
    } else {
      var errors = new ErrorsManager();
      if (ur.email.isEmpty) errors.add('email', 1);
      else if (!new RegExp(r'[a-z0-9_.-]+@[a-z0-9_.-]+\.[a-z]+').hasMatch(ur.email)) errors.add('email', 2);
      else if (ur.email == 'abc@def.com') errors.add('email', 7);
      if (ur.firstName.isEmpty) errors.add('firstName', 3);
      if (ur.lastName.isEmpty) errors.add('lastName', 4);
      if (ur.password.isEmpty) errors.add('password', 5);
      else if (!ur.password.contains(new RegExp(r'[a-z]')) || !ur.password.contains(new RegExp(r'[0-9]')) || ur.password.length < 8) errors.add('password', 6);

      if (errors.hasError) {
        return new http.Response(errors.toJson, 400);
      } else {
        return new http.Response('', 200);
      }
    }
  } else {
    return new http.Response("", 404);
  }
}