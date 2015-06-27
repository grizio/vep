part of vep.component.cms.page;

@Component(
    selector: 'cms-form-page',
    templateUrl: '/packages/vepweb/component/cms/page/cms-form-page.html',
    useShadowDom: false)
class CmsFormPageComponent extends FormComponent<PageForm> {
  static const stateUpdate = 'update';
  static const stateCreate = 'create';

  final PageService pageService;
  final Router router;
  final App app;
  final NavigationComponent navigationComponent;

  final List<String> fields = ['title', 'canonical'];

  InputTextComponent get title => getField('title');
  InputTextComponent get canonical => getField('canonical');

  bool loaded = true;
  String state;

  CmsFormPageComponent(this.pageService, this.router, this.app, this.navigationComponent, RouteProvider routeProvider) {
    if (routeProvider.route.name == 'page-update') {
      state = stateUpdate;
      _load(routeProvider);
    } else {
      state = stateCreate;
      formData = new PageForm();
    }
  }

  String _oldCanonical;

  @override
  void initialize() {
    title.onValueChange.listen((_) {
      if (stringUtilities.isEmpty(_oldCanonical) && stringUtilities.isEmpty(canonical.value) || stringUtilities.equals(_oldCanonical, canonical.value)) {
        canonical.value = _canonicalize(title.value);
      }
      _oldCanonical = _canonicalize(title.value);
    });
    canonical.enableWhen = () => state == stateCreate;
  }

  void _load(RouteProvider routeProvider) {
    loaded = false;
    pageService.find(routeProvider.parameters['canonical']).then((_){
      formData = _;
      app.breadCrumb.path.last.name = 'Mise à jour de la page ${formData.title}';
      loaded = true;
    });
  }

  // TODO: optimize and externalize to utilities.
  String _canonicalize(String str) {
    if (stringUtilities.isBlank(str)) {
      return '';
    } else {
      str = str.toLowerCase();
      str = str.replaceAll(new RegExp('(à|ä|â|@)'), 'a');
      str = str.replaceAll(new RegExp('(é|è|ë|ê)'), 'e');
      str = str.replaceAll(new RegExp('(ï|î)'), 'i');
      str = str.replaceAll(new RegExp('(ö|ô)'), 'o');
      str = str.replaceAll(new RegExp('(ù|ü|û)'), 'u');
      str = str.replaceAll(new RegExp('\\s+'), '-');
      str = str.replaceAll(new RegExp('[^a-z0-9_+-]+'), '-');
      str = str.replaceAll(new RegExp('-+'), '-');
      while (str.startsWith(new RegExp('(_|\\+|-)'))) {
        str = str.substring(1);
      }
      while (str.endsWith('_') || str.endsWith('-') || str.endsWith('+')) {
        str = str.substring(0, str.length - 1);
      }
      return str;
    }
  }

  @override
  Future<HttpResult> onSubmit(PageForm data) {
    if (state == stateCreate) {
      return pageService.create(data);
    } else {
      return pageService.update(data);
    }
  }

  @override
  void onDone(HttpResult httpResult) {
    router.gotoUrl('/cms/pages');
    navigationComponent.invalidatePageList();
  }
}