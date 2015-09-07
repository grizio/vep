part of vep.model.show;

class Show {
  String canonical;
  String title;
  String author;
  String director;
  String company;
  int duration;
  String content;

  String update = 'Modifier';

  String get prettyDuration {
    int hour = (duration / 60).floor();
    int minutes = duration % 60;
    if (hour > 0) {
      return '${hour}h${minutes}';
    } else {
      return '${minutes}m';
    }
  }

  ShowForm toShowForm() {
    var showForm = new ShowForm();
    showForm.canonical = canonical;
    showForm.title = title;
    showForm.author = author;
    showForm.director = director;
    showForm.company = company;
    showForm.duration = duration;
    showForm.content = content;
    return showForm;
  }
}

class ShowSearchCriteria {
  String title;
  String author;
  String director;
  String company;
  String order;
  int page;

  void consider(Map<String, Object> filters) {
    title = filters.containsKey('title') ? filters['title'] : title;
    author = filters.containsKey('author') ? filters['author'] : author;
    director = filters.containsKey('director') ? filters['director'] : director;
    company = filters.containsKey('company') ? filters['company'] : company;
    order = filters.containsKey('order') ? filters['order'] : order;
    page = filters.containsKey('page') ? filters['page'] : page;
  }
}

class ShowSearchResponse {
  int pageMax;
  List<Show> shows;
}

class ShowForm {
  String canonical;
  String title;
  String author;
  String director;
  String company;
  int duration;
  String content;
}