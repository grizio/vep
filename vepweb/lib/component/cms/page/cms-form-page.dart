part of vep.component.cms.page;

@Component(
    selector: 'cms-form-page',
    templateUrl: '/packages/vepweb/component/cms/page/cms-form-page.html',
    useShadowDom: false)
class CmsFormPageComponent extends FormSimpleComponentContainer {
  static const stateUpdate = 'update';
  static const stateCreate = 'create';

  final PageService pageService;
  final Router router;
  final App app;
  final NavigationComponent navigationComponent;

  String get formTitle {
    if (state == stateCreate) {
      return "Création d'une page";
    } else if (page.title != null) {
      return "Modification de la page " + page.title;
    } else {
      return "Modification de la page";
    }
  }

  InputTextComponent get title => form['title'];

  InputTextComponent get canonical => form['canonical'];

  bool loaded = true;
  String state;

  PageForm page = new PageForm();

  CmsFormPageComponent(this.pageService, this.router, this.app, this.navigationComponent, RouteProvider routeProvider) {
    if (routeProvider.route.name == 'page-update') {
      state = stateUpdate;
      _load(routeProvider);
    } else {
      state = stateCreate;
    }
  }

  String _oldCanonical;

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
    pageService.find(routeProvider.parameters['canonical']).then((_) {
      page = _.toPageForm();
      app.breadCrumb.path.last.name = 'Mise à jour de la page ${page.title}';
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

  Future<HttpResult> onSubmit(PageForm data) {
    if (state == stateCreate) {
      return pageService.create(data);
    } else {
      return pageService.update(data);
    }
  }

  Future onDone(HttpResult httpResult) {
    router.gotoUrl('/cms/pages');
    navigationComponent.invalidatePageList();
  }
}