part of vep.component.common.form;

/// This class describes components containing a [FormStepsComponentContainer].
/// All component using the previous one should extends (or implements) this class.
/// In the other cases, the [FormStepsComponentContainer] could have unwanted behaviors.
abstract class FormStepsComponentContainer extends FormComponentContainer<FormStepsComponent> {
}

/// This class describes a form having several steps before posting information.
/// The form should contain several steps which can be inserted into or removed from the flow.
/// Each step is describe as a [FormStepComponent].
@Component(
    selector: 'form-steps',
    templateUrl: '/packages/vepweb/component/common/form/form-steps.html',
    useShadowDom: false
)
class FormStepsComponent extends FormComponent {
  @NgTwoWay('current')
  String current;

  List<FormStepComponent> steps = [];

  FormStepsDecorator formStepsDecorator;

  FormStepComponent operator [](String name) => steps.firstWhere((_) => _.name == name, orElse: () => null);

  @override
  bool get isValid => availableSteps.every((_) => _.isValid);

  @override
  void propagateErrors(Map<String, List<String>> errors) => steps.forEach((_) => _.propagateErrors(errors));

  @override
  void updateFieldFromModel(String fieldName) {
    steps.forEach((_) => _.updateFromModel(dataProxy, fieldName));
  }

  @override
  void updateAllFieldsFromModel() {
    for (var step in steps) {
      step.updateAllFromModel(dataProxy);
    }
  }

  FormStepComponent get currentStep => this[current];

  List<FormStepComponent> get availableSteps {
    List<FormStepComponent> result = [];
    for (FormStepComponent step in steps) {
      if (step.when) {
        result.add(step);
      }
    }
    return result;
  }

  int get currentStepIndex {
    int index = 0;
    for (var step in availableSteps) {
      if (step.name == current) {
        return index;
      }
      index++;
    }
    return -1;
  }

  bool get isFirstStep => currentStepIndex == 0;

  bool get isNotFirstStep => !isFirstStep;

  bool get isLastStep => currentStepIndex == availableSteps.length - 1;

  bool get isNotLastStep => !isLastStep;

  bool get isCurrentStepValid => currentStep != null && currentStep.isStepValid;

  bool get isCurrentStepNotValid => !isCurrentStepValid;

  void previous(Event e) {
    if (prevent(e)) {
      return;
    }
    if (formStepsDecorator != null) {
      formStepsDecorator.addShadow();
    }
    var future = new Future.value();
    if (currentStep.hasPrevious) {
      if (currentStep.leavingToPreviousStep != null) {
        future = currentStep.leavingToPreviousStep();
      }
    }
    future.then((_) {
      var previousStep = null;
      for (var step in availableSteps) {
        if (step.name == current) {
          current = previousStep;
          break;
        }
        previousStep = step.name;
      }
      var future = new Future.value();
      if (currentStep != null && currentStep.comingFromNextStep != null) {
        future = currentStep.comingFromNextStep();
      }
      return future;
    }).whenComplete(() {
      if (formStepsDecorator != null) {
        formStepsDecorator.removeShadow();
      }
    });
  }

  void next(Event e) {
    if (prevent(e)) {
      return;
    }
    if (formStepsDecorator != null) {
      formStepsDecorator.addShadow();
    }
    var isValidFuture = new Future.value(true);
    if (currentStep.validateNextStep != null) {
      isValidFuture = currentStep.validateNextStep();
    }
    isValidFuture.then((isValid) {
      var future = new Future.value();
      if (isValid) {
        if (currentStep.hasNext) {
          if (currentStep.leavingToNextStep != null) {
            future = currentStep.leavingToNextStep();
          }
          return future.then((_) {
            var nextStep = null;
            for (var step in availableSteps.reversed) {
              if (step.name == current) {
                current = nextStep;
                break;
              }
              nextStep = step.name;
            }
            var future = new Future.value();
            if (currentStep != null && currentStep.comingFromPreviousStep != null) {
              future = currentStep.comingFromPreviousStep();
            }
            return future;
          });
        }
        return future;
      }
    }).whenComplete(() {
      if (formStepsDecorator != null) {
        formStepsDecorator.removeShadow();
      }
    });
  }

  @override
  Future submit(Event e) {
    if (prevent(e)) {
      return new Future.value();
    }
    if (formStepsDecorator != null) {
      formStepsDecorator.addShadow();
    }
    var isValidFuture = new Future.value(true);
    if (currentStep.validateNextStep != null) {
      isValidFuture = currentStep.validateNextStep();
    }
    return isValidFuture.then((isValid) {
      if (isValid) {
        return super.submit(null);
      } else {
        return new Future.value();
      }
    }).whenComplete(() {
      if (formStepsDecorator != null) {
        formStepsDecorator.removeShadow();
      }
    });
  }

  @override
  Future onFormErrorInner() {
    for (final step in steps) {
      if (step.when && step.isStepNotValid) {
        current = step.name;
        break;
      }
    }
    return new Future.value();
  }

  bool prevent(Event e) {
    if (e != null) {
      e.preventDefault();
      if (e.target is Element && (e.target as Element).classes.contains('disabled')) {
        return true;
      }
    }
    return false;
  }
}

/// This class si used to manipulate DOM adding or removing an element blocking the user to change data.
/// It avoids the user to change data during a step change or after sending it.
@Decorator(selector: '.form-steps')
class FormStepsDecorator implements ScopeAware {
  final Element element;
  DivElement shadow;

  @override
  set scope(Scope scope) {
    var ctx = utils.getContext(scope, (_) => _ is FormStepsComponentContainer) as FormStepsComponentContainer;
    if (ctx != null) {
      ctx.form.formStepsDecorator = this;
    }
  }

  FormStepsDecorator(this.element);

  void addShadow() {
    if (shadow == null) {
      shadow = new DivElement();
      shadow.classes.add('form-steps-shadow');
      element.append(shadow);
    }
  }

  void removeShadow() {
    if (shadow != null) {
      shadow.remove();
      shadow = null;
    }
  }
}