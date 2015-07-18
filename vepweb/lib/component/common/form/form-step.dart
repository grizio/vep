part of vep.component.common.form;

typedef Future ComingFromPreviousStep();
typedef Future LeavingToPreviousStep();
typedef Future ComingFromNextStep();
typedef Future LeavingToNextStep();
typedef Future<bool> ValidateNextStep();

@Component(
    selector: 'form-step',
    templateUrl: '/packages/vepweb/component/common/form/form-step.html',
    useShadowDom: false
)
class FormStepComponent extends FieldContainer implements ScopeAware {
  @NgAttr('name')
  String name;

  @NgOneWay('label')
  String title;

  @NgOneWayOneTime('comingFromPreviousStep')
  ComingFromPreviousStep comingFromPreviousStep;

  @NgOneWayOneTime('leavingToPreviousStep')
  LeavingToPreviousStep leavingToPreviousStep;

  @NgOneWayOneTime('comingFromNextStep')
  ComingFromNextStep comingFromNextStep;

  @NgOneWayOneTime('leavingToNextStep')
  LeavingToNextStep leavingToNextStep;

  @NgOneWayOneTime('validateNextStep')
  ValidateNextStep validateNextStep;

  @NgOneWay('when')
  bool when;

  Scope _scope;
  Scope get scope => _scope;
  set scope(Scope scope) {
    var ctx = utils.getContext(scope, FormStepsComponentContainer) as FormStepsComponentContainer;
    if (ctx != null) {
      ctx.form.steps.add(this);
      formStepsComponent = ctx.form;
    }
  }

  FormStepsComponent formStepsComponent;

  bool get isVisible => formStepsComponent.current == name;

  bool get hasPrevious => formStepsComponent.availableSteps.isNotEmpty && formStepsComponent.availableSteps.first.name != name;

  bool get hasNotPrevious => !hasPrevious;

  bool get hasNext => formStepsComponent.availableSteps.isNotEmpty && formStepsComponent.availableSteps.last.name != name;

  bool get hasNotNext => !hasNext;

  bool get isStepValid => fields.every((_) => _.verify());

  bool get isStepNotValid => !isStepValid;
}