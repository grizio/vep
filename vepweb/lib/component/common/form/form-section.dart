part of vep.component.common.form;

/// This component describes a section in a [form-sections] component.
/// It is a simple field container specific to [form-sections].
@Component(
    selector: 'form-section',
    templateUrl: '/packages/vepweb/component/common/form/form-section.html',
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
    var ctx = utils.getContext(scope, (_) => _ is FormSectionsComponentContainer) as FormSectionsComponentContainer;
    if (ctx != null) {
      ctx.form.sections.add(this);
    }
  }
}