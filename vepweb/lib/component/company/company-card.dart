part of vep.component.company;

@Component(
    selector: 'company-card',
    templateUrl: '/packages/vepweb/component/company/company-card.html',
    useShadowDom: false
)
class CompanyCardComponent implements AttachAware {
  final CompanyService companyService;
  final RouteProvider routeProvider;
  final App app;
  final GeocodingService geocodingService;

  @NgOneWayOneTime('canonical')
  String canonical;

  @NgOneWayOneTime('small')
  bool small = false;

  Company company;

  int companyLat;
  int companyLng;

  CompanyCardComponent(this.companyService, this.routeProvider, this.app, this.geocodingService);

  @override
  void attach() {
    companyService.find(getCanonical()).then((_) {
      company = _;
      if (app.breadCrumb.path.last.code == 'company-read') {
        app.breadCrumb.path.last.name = company.name;
      }
      initializeMapPosition();
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

  void initializeMapPosition() {
    geocodingService.findLatLngByAddress(company.address).then((_) {
      companyLat = _.lat;
      companyLng = _.lng;
    });
  }
}