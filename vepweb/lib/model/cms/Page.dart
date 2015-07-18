part of vep.model.cms;

class Page {
  int id;
  String canonical;
  String title;
  String content;
  int menu;

  PageForm toPageForm() {
    var pageForm = new PageForm();
    pageForm.canonical = canonical;
    pageForm.title = title;
    pageForm.content = content;
    pageForm.menu = menu;
    return pageForm;
  }
}

class PageForm {
  @jsonIgnore
  String canonical;
  String title;
  String content;
  int menu;
}