part of vep.component.common.form;

@Component(
    selector: 'form-sections',
    templateUrl: '/packages/vepweb/component/common/form/form-sections.html',
    useShadowDom: false
)
class FormSectionComponent extends FieldContainer implements ScopeAware, AttachAware {
  @NgOneWay('title')
  String title;

  @NgAttr('name')
  String name;

  Scope scope;

  @override
  void attach() {
    var ctx = utils.getContext(scope, FormSectionsComponentContainer) as FormSectionsComponentContainer;
    if (ctx != null) {
      ctx.form.sections.add(this);
    }
  }
}