part of vep.component.common.form;

abstract class FormSimpleComponentContainer extends FormComponentContainer<FormSimpleComponent> {
}

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