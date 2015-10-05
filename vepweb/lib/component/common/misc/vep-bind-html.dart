part of vep.component.common.misc;

@Decorator(
    selector: '[vep-bind-html]',
    map: const {'vep-bind-html': '=>value'}
)
class VepBindHTML {
  final Element element;

  VepBindHTML(this.element);

  set value(value) {
    final sanitizer = NodeTreeSanitizer.trusted;
    element.setInnerHtml(
        value == null ? '' : value.toString(),
        treeSanitizer: sanitizer);
  }
}