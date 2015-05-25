part of vep.http.dev;

http.Response ClientInMemoryUser(http.Request request) {
  if (new RegExp(r"/?user/register/?").hasMatch(request.url.path)) {
    UserRegistration ur = jsonx.decode(request.body, type: UserRegistration);
    if (ur.email == null || ur.firstName == null || ur.lastName == null || ur.password == null) {
      return new http.Response(malformedRequestContent, 400);
    } else {
      var errorManager = new ErrorsManager();
      if (ur.email.isEmpty) errorManager.add('email', errors.emptyEmail);
      else if (!new RegExp(r'[a-z0-9_.-]+@[a-z0-9_.-]+\.[a-z]+').hasMatch(ur.email)) errorManager.add('email', errors.invalidEmail);
      else if (ur.email == 'abc@def.com') errorManager.add('email', errors.usedEmail);
      if (ur.firstName.isEmpty) errorManager.add('firstName', errors.emptyFirstName);
      if (ur.lastName.isEmpty) errorManager.add('lastName', errors.emptyLastName);
      if (ur.password.isEmpty) errorManager.add('password', errors.emptyPassword);
      else if (!ur.password.contains(new RegExp(r'[a-z]')) || !ur.password.contains(new RegExp(r'[0-9]')) || ur.password.length < 8) errorManager.add('password', errors.weakPassword);

      if (errorManager.hasError) {
        return new http.Response(errorManager.toJson, 400);
      } else {
        return new http.Response('', 200);
      }
    }
  } else if (new RegExp(r"/?login/?").hasMatch(request.url.path)) {
    UserLogin ul = jsonx.decode(request.body, type: UserLogin);
    if (ul.email == null || ul.password == null) {
      return new http.Response(malformedRequestContent, 400);
    } else {
      if (ul.email == "abc@abc.abc" && ul.password == "abcd1234") {
        return new http.Response('abcdefghijkl', 200);
      } else {
        return new http.Response(errors.unknownUser.toString(), 403);
      }
    }
  } else {
    return new http.Response("", 404);
  }
}