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

  String get joinedSeats => seatList != null ? seatList.join(', ') : '';
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

void prepareJsonxReservation() {
  jsonx.jsonToObjects[new jsonx.TypeHelper<List<Reservation>>().type] = (json) {
    final reservations = (json as Map<String, Object>)['reservations'] as List<Map<String, Object>>;
    return reservations.map((reservationJson) {
      final reservation = new Reservation();
      reservation.id = reservationJson['id'];
      reservation.firstName = reservationJson['firstName'];
      reservation.lastName = reservationJson['lastName'];
      reservation.email = reservationJson['email'];
      reservation.city = reservationJson['city'];
      reservation.comment = reservationJson['comment'];
      reservation.seats = reservationJson['seats'];
      reservation.key = reservationJson['key'];
      reservation.seatList = reservationJson['seatList'];
      reservation.prices = (reservationJson['prices'] as List<Map<String, Object>>).map((priceJson){
        final price = new ReservationPrice();
        price.price = priceJson.containsKey('id') ? priceJson['id'] : priceJson['price'];
        price.number = priceJson['number'];
        price.name = priceJson['name'];
        price.value = priceJson['value'];
        return price;
      }).toList();
      return reservation;
    }).toList();
  };
}