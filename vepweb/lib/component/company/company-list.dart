part of vep.component.company;

@Component(
    selector: 'company-list',
    templateUrl: '/packages/vepweb/component/company/company-list.html',
    useShadowDom: false)
class CompanyListComponent implements TableSearchContext {
  final CompanyService companyService;

  Lazy<TableDescriptor> _tableDescriptor = lazy(() => new TableDescriptor([
    new ColumnDescriptor('canonical', 'URL', 'link', url: '/company/update/{canonical}', hasFilter: true),
    new ColumnDescriptor('name', 'Nom', 'text', hasFilter: true),
    new ColumnDescriptor('address', 'Adresse', 'text', hasFilter: true),
    new ColumnDescriptor('isVep', 'Troupe interne', 'checkbox', hasFilter: true)
  ]));

  @override
  TableDescriptor get tableDescriptor => _tableDescriptor.value;

  CompanyListComponent(this.companyService);

  @override
  Future<List<Map<String, Object>>> search(Map<String, Object> filters) {
    return companyService.findAll().then((companyList) {
      return utils.objectToListOfMap(companyList);
    });
  }

  @override
  void onChange(Map<String, Object> data) {
    // Do nothing
  }
}