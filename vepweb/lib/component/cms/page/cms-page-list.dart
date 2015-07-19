part of vep.component.cms.page;

@Component(
    selector: 'cms-page-list',
    templateUrl: '/packages/vepweb/component/cms/page/cms-page-list.html',
    useShadowDom: false)
class CmsPageListComponent extends TableSearchContext {
  final PageService pageService;

  @NgTwoWay('processing')
  bool processing;

  Lazy<TableDescriptor> _tableDescriptor = lazy(() => new TableDescriptor([
    new ColumnDescriptor('canonical', 'URL', 'link', url: '/cms/page/update/{canonical}', hasFilter: true),
    new ColumnDescriptor('title', 'Titre', 'text', hasFilter: true),
    new ColumnDescriptor('menu', 'Ordre dans le menu', 'integer', hasFilter: true),
  ]));

  @override
  TableDescriptor get tableDescriptor => _tableDescriptor.value;

  CmsPageListComponent(this.pageService);

  @override
  Future<List<Map<String, Object>>> search(Map<String, Object> filters) {
    return pageService.findAll().then((pageList) {
      return utils.objectToListOfMap(pageList);
    });
  }

  @override
  void onChange(Map<String, Object> data) {
    // Do nothing
  }
}