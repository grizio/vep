part of vep.model.theater;

class Theater {
  String canonical;
  String name;
  String address;
  String content;
  bool fixed;
  String plan;
  int maxSeats;

  TheaterForm toTheaterForm() {
    var theaterForm = new TheaterForm();
    theaterForm.canonical = canonical;
    theaterForm.name = name;
    theaterForm.address = address;
    theaterForm.content = content;
    theaterForm.fixed = fixed;
    theaterForm.plan = plan;
    theaterForm.maxSeats = maxSeats;
    return theaterForm;
  }
}

class TheaterForm {
  @jsonIgnore
  String canonical;
  String name;
  String address;
  String content;
  bool fixed;
  String plan;
  int maxSeats;
}

class Seat {
  /// Code
  String c;

  /// Type
  String t;

  /// x-axis
  double x;

  /// y-axis
  double y;

  /// Width
  double w;

  /// Height
  double h;

  @jsonIgnore
  bool selected;

  @jsonIgnore
  String get className => 'theater-plan-seat-' + (selected != null && selected ? 'selected' : (t != null ? t : 'normal'));
}