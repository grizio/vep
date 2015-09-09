part of vep.model.session;

class SessionSearchCriteria extends ModelToString {
  String theater;
  String show;
  DateTime startDate;
  DateTime endDate;
  String order;
  int page;

  void consider(Map<String, Object> filters) {
    theater = filters.containsKey('theater') ? filters['theater'] : theater;
    show = filters.containsKey('shows') ? filters['shows'] : show;
    page = filters.containsKey('page') ? filters['page'] : page;
    if (filters.containsKey('date')) {
      var dates = filters['date'];
      if (dates != null && dates is List<DateTime> && (dates as List<DateTime>).length >= 2) {
        startDate = dates[0];
        endDate = dates[1];
      }
    }
  }
}

class SessionSearchResponse {
  int pageMax;
  List<SessionSearchItem> sessions;
}

class SessionSearchItem {
  String canonical;
  String theater;
  String theaterName;
  List<SessionSearchShow> showList;
  DateTime date;

  String get shows => showList != null ? showList.map((_) => _ != null ? _.title : null).where((_) => _ != null).join(', ') : '';
}

class SessionSearchShow {
  String canonical;
  String title;
}

void prepareJsonxSessionSearch() {
  jsonx.jsonToObjects[SessionSearchResponse] = (json) {
    var sessionSearchResponse = new SessionSearchResponse();
    sessionSearchResponse.pageMax = json['pageMax'];
    sessionSearchResponse.sessions = (json['sessions'] as List<Map<String, Object>>).map((sessionJson) {
      var session = new SessionSearchItem();
      session.canonical = sessionJson['canonical'];
      session.theater = sessionJson['theater'];
      session.theaterName = sessionJson['theaterName'];
      session.date = sessionJson['date'];
      session.showList = (sessionJson['shows'] as List<Map<String, Object>>).map((showJson) {
        var show = new SessionSearchShow();
        show.canonical = showJson['canonical'];
        show.title = showJson['title'];
        return show;
      });
      return session;
    }).toList();
    return sessionSearchResponse;
  };
}