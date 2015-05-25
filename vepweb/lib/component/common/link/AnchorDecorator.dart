part of vep.component.common.link;

@Decorator(selector: 'a')
class AnchorDecorator {
  final Element element;
  final App app;

  AnchorDecorator(this.element, this.app) {
    element.onClick.listen((_) => _goto(_));
  }

  _goto(MouseEvent event) {
    event.preventDefault();
    //window.history.pushState(null, '', (element as AnchorElement).href);
  }
}