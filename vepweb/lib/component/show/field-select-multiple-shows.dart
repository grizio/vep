part of vep.component.show;

/// This component provides the user to enter a date value.
@Component(
    selector: 'field-select-multiple-shows',
    templateUrl: '/packages/vepweb/component/show/field-select-multiple-shows.html',
    useShadowDom: false)
class FieldSelectMultipleShowsComponent extends RepeatableContainer<SessionFormShow> {
  final ShowService showService;

  FieldSelectMultipleShowsComponent(this.showService): super() {
    addConstraint(
            (List<SessionFormShow> shows) => shows != null && shows.any((_) => stringUtilities.isNotBlank(_.show)),
        'Veuillez choisir au moins une pièce'
    );
    addConstraint(
            (_) => fields.every((_) => _.isValid),
        'Au moins une pièce séléctionnée est invalide'
    );
  }

  @override
  SessionFormShow createModel() => new SessionFormShow();

  Future<List<Choice<String>>> searchShows(String title) {
    if (stringUtilities.isNotBlank(title)) {
      return showService.findCanonicalByTitle(title.trim());
    } else {
      return new Future.value(null);
    }
  }

  Future<Choice<String>> inverseSearchShow(String canonical) {
    if (stringUtilities.isNotBlank(canonical)) {
      return showService.find(canonical).then((show){
        if (show != null) {
          return new Choice(show.canonical, show.title);
        } else {
          return new Choice('','');
        }
      });
    } else {
      return new Future.value(new Choice('', ''));
    }
  }
}