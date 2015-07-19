part of vep.component.common.link;

/// Helper to avoid links sending the user into another page and refreshing the site.
@Decorator(selector: 'a')
class AnchorDecorator {
  final Element element;
  final App app;

  AnchorDecorator(this.element, this.app) {
    element.onClick.listen((_) => _goto(_));
  }

  _goto(MouseEvent event) {
    event.preventDefault();
  }
}