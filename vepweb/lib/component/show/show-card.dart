part of vep.component.show;

@Component(
    selector: 'show-card',
    templateUrl: '/packages/vepweb/component/show/show-card.html',
    useShadowDom: false
)
class ShowCardComponent implements AttachAware {
  final ShowService showService;
  final CompanyService companyService;
  final RouteProvider routeProvider;
  final App app;

  @NgOneWayOneTime('canonical')
  String canonical;

  @NgOneWayOneTime('small')
  bool small = false;

  Show show;
  Company company;

  ShowCardComponent(this.showService, this.companyService, this.routeProvider, this.app);

  @override
  void attach() {
    showService.find(getCanonical()).then((s) {
      show = s;
      if (app.breadCrumb.path.last.code == 'show-read') {
        app.breadCrumb.path.last.name = show.title;
      }
      companyService.find(show.canonical).then((c) => company = c);
    });
  }

  String getCanonical() {
    if (canonical != null) {
      return canonical;
    } else if (routeProvider.parameters.containsKey('canonical')) {
      return routeProvider.parameters['canonical'];
    } else {
      return null;
    }
  }
}