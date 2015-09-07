part of vep.component.common.form;

/// Describes a function called when the user come into current step from the previous one.
typedef Future ComingFromPreviousStep();

/// Describes a function called when the user leave current step to the previous one.
typedef Future LeavingToPreviousStep();

/// Describes a function called when the user come into current step from the next one.
typedef Future ComingFromNextStep();

/// Describes a function called when the user leave current step to the previous one.
typedef Future LeavingToNextStep();

/// Describes a function to call when the user asks to leave to next step.
/// It will validate if the user can really pass (checking some values in remote server, for instance).
typedef Future<bool> ValidateNextStep();

/// This class describes a step in a [FormStepsComponent].
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

  @NgOneWayOneTime('coming-from-previous-step')
  ComingFromPreviousStep comingFromPreviousStep;

  @NgOneWayOneTime('leaving-to-previous-step')
  LeavingToPreviousStep leavingToPreviousStep;

  @NgOneWayOneTime('coming-from-next-step')
  ComingFromNextStep comingFromNextStep;

  @NgOneWayOneTime('leaving-to-next-step')
  LeavingToNextStep leavingToNextStep;

  @NgOneWayOneTime('validate-next-step')
  ValidateNextStep validateNextStep;

  @NgOneWay('when')
  bool when;

  Scope _scope;

  Scope get scope => _scope;

  set scope(Scope scope) {
    var ctx = utils.getContext(scope, (_) => _ is FormStepsComponentContainer) as FormStepsComponentContainer;
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

  bool get isStepValid => fields.every((_){
    _.verify();
    return _.isValid;
  });

  bool get isStepNotValid => !isStepValid;
}