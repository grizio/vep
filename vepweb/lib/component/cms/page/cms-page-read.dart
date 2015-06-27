part of vep.component.cms.page;

@Component(
    selector: 'cms-page-read',
    templateUrl: '/packages/vepweb/component/cms/page/cms-page-read.html',
    useShadowDom: false)
class CmsPageReadComponent implements AttachAware {
  final PageService pageService;
  final RouteProvider routeProvider;
  final App app;

  @NgAttr('canonical')
  String canonical;

  Page page;

  CmsPageReadComponent(this.pageService, this.routeProvider, this.app);

  @override
  void attach() {
    if (canonical != null) {
      _load(canonical);
    } else {
      if (routeProvider.parameters.containsKey('canonical')) {
        _load(routeProvider.parameters['canonical']);
      } else {
        throw 'No canonical found in component attributes, nor in route parameters.';
      }
    }
  }

  void _load(String canonical) {
    pageService.find(canonical).then((_){
      page = _;
      if (routeProvider.routeName == 'page-read') {
        app.breadCrumb.path.last.name = page.title;
      }
    });
  }
}