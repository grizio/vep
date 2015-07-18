part of vep.component.common.form;

abstract class FormSectionsComponentContainer extends FormComponentContainer<FormSectionsComponent> {
}

@Component(
  selector: 'form-sections',
  templateUrl: '/packages/vepweb/component/common/form/form-sections.html',
  useShadowDom: false
)
class FormSectionsComponent extends FormComponent {
  List<FormSectionComponent> sections = [];

  FormSectionComponent operator[](String name) {
    return sections.firstWhere((_) => _.name == name, orElse: () => null);
  }

  @override
  bool get isValid {
    return sections.every((_) => _.isValid);
  }

  @override
  void propagateErrors(Map<String, List<String>> errors) {
    sections.forEach((_) => _.propagateErrors(errors));
  }

  @override
  void updateFieldFromModel(String fieldName) {
    sections.forEach((_) => _.updateFromModel(dataProxy, fieldName));
  }

  @override
  void updateAllFieldsFromModel() {
    for (var section in sections) {
      section.updateAllFromModel(dataProxy);
    }
  }
}