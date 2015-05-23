part of vep.component.main;

@Injectable()
class App {
  /// Username of the current user if connected (session)
  String username;

  /// Key of the current usr for the current session.
  String key;

  BreadCrumbSubscriber onBreadCrumbChange = new BreadCrumbSubscriber();

  BreadCrumb _breadCrumb;

  BreadCrumb get breadCrumb => _breadCrumb;

  set breadCrumb(BreadCrumb newBreadCrumb) {
    _breadCrumb = newBreadCrumb;
    onBreadCrumbChange.process(newBreadCrumb);
  }
}

typedef void BreadCrumbListener(BreadCrumb data);
class BreadCrumbSubscriber {
  List<BreadCrumbListener> _listeners = [];

  void listen(BreadCrumbListener listener) {
    _listeners.add(listener);
  }

  void process(BreadCrumb data) {
    _listeners.forEach((_) => _(data));
  }
}