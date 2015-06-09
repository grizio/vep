part of vep.component.main;

@Component(
    selector: 'navigation',
    templateUrl: '/packages/vepweb/component/main/navigation.html',
    useShadowDom: false)
class NavigationComponent {
  final App app;

  bool get loggedIn => app.isLoggedIn;

  NavigationComponent(this.app);
}