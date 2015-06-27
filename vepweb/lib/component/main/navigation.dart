part of vep.component.main;

@Component(
    selector: 'navigation',
    templateUrl: '/packages/vepweb/component/main/navigation.html',
    useShadowDom: false)
class NavigationComponent {
  final App app;
  final PageService pageService;

  bool get loggedIn => app.isLoggedIn;

  NavigationComponent(this.app, this.pageService);

  List<Page> _pages;
  Future<List<Page>> _findForFuture;

  List<Page> get pages {
    if (_pages == null) {
      if (_findForFuture == null) {
        _findForFuture = pageService.findForMenu();
      }
      _findForFuture.then((_) {
        _pages = _;
        _findForFuture = null;
      });
    }
    return _pages;
  }

  void invalidatePageList() {
    _pages = null;
  }
}