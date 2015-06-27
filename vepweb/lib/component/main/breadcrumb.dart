part of vep.component.main;

@Component(
    selector: 'breadcrumb',
    templateUrl: '/packages/vepweb/component/main/breadcrumb.html',
    useShadowDom: false)
class BreadCrumbComponent {
  final App app;

  @NgTwoWay('breadcrumb')
  BreadCrumb breadcrumb;

  BreadCrumbComponent(this.app) {
    app.onBreadCrumbChange.listen((BreadCrumb _) => breadcrumb = _);
  }
}

class BreadCrumb {
  final List<BreadCrumbData> path;

  const BreadCrumb([this.path=const[]]);

  BreadCrumb child(String code, String url, String path) {
    var data = <BreadCrumbData>[];
    data.addAll(this.path);
    data.add(new BreadCrumbData(code, url, path));
    return new BreadCrumb(data);
  }
}

class BreadCrumbData {
  // Not final because controllers could change these values.
  String code;
  String url;
  String name;

  BreadCrumbData(this.code, this.url, this.name);
}