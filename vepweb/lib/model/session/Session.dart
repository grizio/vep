part of vep.model.session;

class Session extends ModelToString {
  String theater;
  String canonical;
  DateTime date;
  String name;
  DateTime reservationEndDate;
  List<SessionPrice> prices;
  List<String> shows;
}

class SessionPrice extends ModelToString {
  String name;
  int price;
  String condition;
}

class SessionForm extends ModelToString {
  @jsonx.jsonIgnore
  String theater;
  @jsonx.jsonIgnore
  String canonical;

  DateTime date;
  String name;
  DateTime reservationEndDate;
  List<SessionPrice> prices;
  List<String> shows;

  @jsonx.jsonIgnore
  List<SessionFormShow> internalShows;

  String get strDate => date != null ? new DateFormat('dd/MM/yyyy HH:mm').format(date) : '';
  String get strReservationEndDate => reservationEndDate != null ? new DateFormat('dd/MM/yyyy HH:mm').format(reservationEndDate) : '';
}

class SessionFormShow extends ModelToString {
  String show;
}