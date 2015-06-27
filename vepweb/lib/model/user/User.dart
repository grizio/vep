part of vep.model.user;

class User {
  int uid;
  String email;
  String password;
  String firstName;
  String lastName;
  String city;
  String key;
  List<String> roles;
}

class UserRegistration {
  String email;
  String email2;
  String password;
  @jsonIgnore
  String password2;
  String firstName;
  String lastName;
  String city;
}

class UserLogin {
  String email;
  String password;
}