part of vep.component.common.form;

/// This class describes components containing a [FormSimpleComponent].
/// All component using the previous one should extends (or implements) this class.
/// In the other cases, the [FormSimpleComponent] could have unwanted behaviors.
abstract class FormSimpleComponentContainer extends FormComponentContainer<FormSimpleComponent> {
}

/// This component describes a simple form with a list a fields.
/// It has no specific behavior than a classic [FormComponent].
@Component(
    selector: 'form-simple',
    templateUrl: '/packages/vepweb/component/common/form/form-simple.html',
    useShadowDom: false
)
class FormSimpleComponent extends FormComponent with FieldContainer {
  @NgOneWay('label')
  String title;

  @override
  void updateFieldFromModel(String fieldName) {
    updateFromModel(dataProxy, fieldName);
  }

  @override
  void updateAllFieldsFromModel() {
    updateAllFromModel(dataProxy);
  }
}