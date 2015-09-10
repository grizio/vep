part of vep.model.session;

class Session extends ModelToString {
  String theater;
  String canonical;
  DateTime date;
  String name;
  DateTime reservationEndDate;
  List<SessionPrice> prices;
  List<String> shows;

  SessionForm toSessionForm([SessionForm sessionForm]) {
    if (sessionForm == null) {
      sessionForm = new SessionForm();
    }
    copyByAccessors(this, sessionForm);
    sessionForm.internalShows = sessionForm.shows == null ? null : sessionForm.shows.map((_){
      var sessionFormShow = new SessionFormShow();
      sessionFormShow.show = _;
      return sessionFormShow;
    }).toList();
    return sessionForm;
  }

  SessionFormUpdate toSessionFormUpdate() {
    return toSessionForm(new SessionFormUpdate());
  }
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

class SessionFormUpdate extends SessionForm {
  String reason;
}

class SessionFormShow extends ModelToString {
  String show;
}