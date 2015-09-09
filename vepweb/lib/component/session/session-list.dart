part of vep.component.session;

@Component(
    selector: 'session-list',
    templateUrl: '/packages/vepweb/component/session/session-list.html',
    useShadowDom: false)
class SessionListComponent implements TableSearchContext {
  final SessionService sessionService;
  final TheaterService theaterService;
  final ShowService showService;
  final App app;

  int pageMax = 1;

  int page;

  @override
  TableDescriptor tableDescriptor;

  SessionListComponent(this.sessionService, this.theaterService, this.showService, this.app) {
    createTableDescriptor();
    theaterService.findAll().then((theaters) {
      tableDescriptor['theater'].choices = theaters.map((t) => new Choice(t.canonical, t.name)).toList();
    });
  }

  void createTableDescriptor() {
    var columns = [
      new ColumnDescriptor('theater', 'Théâtre', ColumnTypes.choices, hasFilter: true, choices: []),
      new ColumnDescriptor('shows', 'Pièce(s)', ColumnTypes.text, hasFilter: true),
      new ColumnDescriptor('date', 'Date', ColumnTypes.date, hasFilter: true)
    ];

    if (app.roles.contains(roles.sessionManager)) {
      columns.add(new ColumnDescriptor('update', 'Modifier', ColumnTypes.link, url: '/session/update/{theater}/{session}', hasFilter: false));
    }
    tableDescriptor = new TableDescriptor(columns);
  }

  @override
  Future<List<Map<String, Object>>> search(Map<String, Object> filters) {
    var criteria = new SessionSearchCriteria();
    criteria.consider(filters);
    criteria.page = page;
    return sessionService.search(criteria).then((sessionSearchResponse) {
      pageMax = sessionSearchResponse.pageMax;
      return sessionSearchResponse.sessions.map((SessionSearchItem session) => {
        'theater': session.theater,
        'session': session.canonical,
        'theaterName': session.theaterName,
        'shows': session.shows,
        'date': session.date,
        'update': 'Modifier'
      }).toList();
    });
  }

  @override
  void onChange(Map<String, Object> data) {
    // Do nothing
  }
}