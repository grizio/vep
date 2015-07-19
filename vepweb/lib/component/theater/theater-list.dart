part of vep.component.theater;

@Component(
    selector: 'theater-list',
    templateUrl: '/packages/vepweb/component/theater/theater-list.html',
    useShadowDom: false)
class TheaterListComponent implements TableSearchContext {
  final TheaterService theaterService;

  Lazy<TableDescriptor> _tableDescriptor = lazy(() => new TableDescriptor([
    new ColumnDescriptor('canonical', 'URL', 'link', url: '/theater/update/{canonical}', hasFilter: true),
    new ColumnDescriptor('name', 'Nom', 'text', hasFilter: true),
    new ColumnDescriptor('address', 'Adresse', 'text', hasFilter: true),
    new ColumnDescriptor('fixed', 'Salle fixe', 'checkbox', hasFilter: false)
  ]));

  @override
  TableDescriptor get tableDescriptor => _tableDescriptor.value;

  TheaterListComponent(this.theaterService);

  @override
  Future<List<Map<String, Object>>> search(Map<String, Object> filters) {
    return theaterService.findAll().then((theaterList){
      return utils.objectToListOfMap(theaterList);
    });
  }

  @override
  void onChange(Map<String, Object> data) {
    // Do nothing
  }
}