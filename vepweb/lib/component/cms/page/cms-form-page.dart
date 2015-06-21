part of vep.component.cms.page;

@Component(
    selector: 'cms-form-page',
    templateUrl: '/packages/vepweb/component/cms/page/cms-form-page.html',
    useShadowDom: false)
class CmsFormPageComponent extends FormComponent<PageForm> {
  final PageService pageService;
  final Router router;

  CmsFormPageComponent(this.pageService, this.router);

  @override
  PageForm get formData => page;

  PageForm page = new PageForm();

  String _oldTitle;

  InputTextComponent _title;

  InputTextComponent get title => _title;

  set title(InputTextComponent title) {
    _title = title;
    title.onValueChange.listen((_) {
      var oldCanonical = _canonicalize(_oldTitle);
      if (stringUtilities.isEmpty(oldCanonical) && stringUtilities.isEmpty(canonical.value) || stringUtilities.equals(oldCanonical, canonical.value)) {
        canonical.value = _canonicalize(title.value);
      }
      _oldTitle = title.value;
    });
  }

  InputTextComponent canonical;

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
  Future<HttpResult> onSubmit() {
    return pageService.create(page);
  }

  @override
  void onDone(HttpResult httpResult) {
    router.gotoUrl('/cms/pages');
  }
}