part of vep.model.session;

class Reservation extends ModelToString {
  int id;
  String session;
  String firstName;
  String lastName;
  String email;
  String city;
  String comment;
  String seats;
  String key;
  List<String> seatList;
  List<ReservationPrice> prices;
}

class ReservationPrice extends ModelToString {
  int price;
  int number;

  @jsonx.jsonIgnore
  String name;
  @jsonx.jsonIgnore
  int value;
}

class ReservationForm extends ModelToString {
  int id;
  String session;
  String firstName;
  String lastName;
  String email;
  String city;
  String comment;
  String seats;
  String key;
  List<String> seatList;
  List<ReservationPrice> prices;

  @jsonx.jsonIgnore
  Map<int, int> priceRepartition;

  @jsonx.jsonIgnore
  int get totalPrice => prices != null ? prices.fold(0, (sum, price) => sum + price.value * price.number) : 0;
}

class ReservationResult extends ModelToString {
  int id;
  String key;
}