part of vep.model.user;

class User {
  int uid;
  String email;
  String password;
  String firstName;
  String lastName;
  String city;
}

class UserRegistration {
  String email;
  String password;
  @jsonIgnore
  String password2;
  String firstName;
  String lastName;
  String city;
}