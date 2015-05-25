part of vep.component;

typedef void Listener<A>(A data);
class Subscriber<A> {
  List<Listener<A>> _listeners = [];

  void listen(Listener<A> listener) {
    _listeners.add(listener);
  }

  void process(A data) {
    _listeners.forEach((_) => _(data));
  }
}