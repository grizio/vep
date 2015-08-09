part of vep.component.show;

@Component(
    selector: 'show-list',
    templateUrl: '/packages/vepweb/component/show/show-list.html',
    useShadowDom: false)
class ShowListComponent implements TableSearchContext {
  final ShowService showService;
  final CompanyService companyService;
  final App app;

  int pageMax = 1;

  int page;

  @override
  TableDescriptor tableDescriptor;

  ShowListComponent(this.showService, this.companyService, this.app) {
    createTableDescriptor();
    companyService.findAll().then((companies){
      final choices = companies.map((c) => new Choice(c.canonical, c.name)).toList();
      tableDescriptor['company'].choices = choices;
    });
  }

  void createTableDescriptor() {
    var columns = [
      new ColumnDescriptor('title', 'Titre', ColumnTypes.link, url: '/show/read/{canonical}', hasFilter: true),
      new ColumnDescriptor('author', 'Auteur', ColumnTypes.text, hasFilter: true),
      new ColumnDescriptor('director', 'Metteur en scène', ColumnTypes.text, hasFilter: true),
      new ColumnDescriptor('company', 'Troupe', ColumnTypes.choices, hasFilter: true, choices: [new Choice('a', 'b')])
    ];

    if (app.roles.contains(roles.showManager)) {
      columns.add(new ColumnDescriptor('update', 'Modifier', ColumnTypes.link, url: '/show/update/{canonical}', hasFilter: false));
    }
    tableDescriptor = new TableDescriptor(columns);
  }

  @override
  Future<List<Map<String, Object>>> search(Map<String, Object> filters) {
    var criteria = new ShowSearchCriteria();
    criteria.consider(filters);
    criteria.page = page;
    return showService.search(criteria).then((showSearchResponse) {
      pageMax = showSearchResponse.pageMax;
      return utils.objectToListOfMap(showSearchResponse.shows);
    });
  }

  @override
  void onChange(Map<String, Object> data) {
    // Do nothing
  }
}