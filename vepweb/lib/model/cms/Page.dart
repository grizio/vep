part of vep.model.cms;

class Page {
  int id;
  String canonical;
  String title;
  String content;
  int menu;
}

class PageForm {
  @jsonIgnore
  String canonical;
  String title;
  String content;
  int menu;
}