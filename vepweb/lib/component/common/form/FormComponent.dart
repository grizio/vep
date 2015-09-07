part of vep.component.common.form;

typedef Future OnFormLoaded();

typedef Future<HttpResult> OnFormSubmit(Object data);

typedef Future OnFormValid(HttpResult httpResult);

typedef Future OnFormError(HttpResult httpResult);

typedef Future OnFormDone();

/// This class describes components containing a specification on [FormComponent].
/// All component using the previous one should extends (or implements) this class.
/// In the other cases, the specific [FormComponent] could have unwanted behaviors.
abstract class FormComponentContainer<A extends FormComponent> {
  A form;
}

/// This class describes a form component.
/// It provides some controls on the form.
/// It must be extend in specific component in terms of wanted behavior.
abstract class FormComponent implements ScopeAware, AttachAware {
  @NgOneWayOneTime('on-submit')
  OnFormSubmit onFormSubmit;

  @NgOneWayOneTime('on-valid')
  OnFormValid onFormValid;

  @NgOneWayOneTime('on-error')
  OnFormError onFormError;

  @NgOneWayOneTime('on-loaded')
  OnFormLoaded onFormLoaded;

  @NgOneWayOneTime('on-done')
  OnFormDone onFormDone;

  FormDataProxy dataProxy;

  @NgTwoWay('data')
  Object get data => dataProxy;

  set data(Object value) {
    if (value is FormDataProxy) {
      if (value != dataProxy) {
        dataProxy = value;
        updateAllFieldsFromModel();
      }
    } else {
      dataProxy = new FormDataProxy(value, this);
      updateAllFieldsFromModel();
    }
  }

  @override
  set scope(Scope scope) {
    var ctx = utils.getContext(scope, (_) => _ is FormComponentContainer) as FormComponentContainer;
    if (ctx != null) {
      ctx.form = this;
    }
  }

  bool processing = false;
  bool done = false;
  bool initialized = false;

  bool get isValid;

  // The usage of this attribute is because of angular creation cycle :
  // parent set scope -> children set scope -> parent attach -> children attach
  // So we need to inform the parent to wait until all children are attached.
  int fieldsToWait = 0;

  void waitingField() {
    fieldsToWait++;
  }

  void fieldInitialized() {
    fieldsToWait--;
    if (fieldsToWait == 0) {
      initialize();
    }
  }

  @override
  void attach() {
    if (fieldsToWait == 0) {
      initialize();
    }
  }

  void initialize() {
    if (!initialized) {
      var future = null;
      updateAllFieldsFromModel();
      if (onFormLoaded != null) {
        future = onFormLoaded();
      }
      if (future == null) {
        future = new Future.value();
      }
      future.then((_) {
        initialized = true;
      });
    }
  }

  Future submit(Event e) {
    var defaultFuture = new Future.value();
    if (e != null) {
      e.preventDefault();
    }
    processing = true;
    Future future;
    if (isValid) {
      var innerData = (data as FormDataProxy).innerObject;
      future = onFormSubmit != null ? onFormSubmit(innerData) : defaultFuture;
      future = future.then((HttpResult result) {
        if (result != null) {
          if (result.isSuccess) {
            var futureValid = onFormValid != null ? onFormValid(result) : defaultFuture;
            return futureValid.then((_) => done = true);
          } else {
            if (result is HttpResultError) {
              propagateErrors({'_root_': [result.errorMessage]});
            } else if (result is HttpResultErrors) {
              propagateErrors(result.errorMessages);
            }
            return onFormError != null ? onFormError(result) : defaultFuture;
          }
        }
      });
    } else {
      future = defaultFuture;
    }
    return future.whenComplete(() {
      processing = false;
      if (onFormDone != null) {
        onFormDone();
      }
    });


  }

  void propagateErrors(Map<String, List<String>> errors);

  void updateFieldFromModel(String fieldName);

  void updateAllFieldsFromModel();
}

/// This class is an helper used to listen all changes in the model.
/// It will updates naturally the proxied object, then trigger form change if needed.
class FormDataProxy {
  final Object innerObject;
  final FormComponent formComponent;

  FormDataProxy(this.innerObject, this.formComponent);

  @override
  dynamic noSuchMethod(Invocation invocation) {
    InstanceMirror mirror = reflect(innerObject);
    if (invocation.isSetter) {
      var fieldName = MirrorSystem.getName(invocation.memberName);
      fieldName = fieldName.substring(0, fieldName.length - 1); //remove "=" of setter.
      var field = MirrorSystem.getSymbol(fieldName);

      if (mirror.type.declarations.containsKey(field)) {
        Object oldValue = mirror.getField(field).hasReflectee ? mirror.getField(field).reflectee : null;
        Object newValue = invocation.positionalArguments.first;
        if (oldValue != newValue) {
          var result = mirror.setField(field, newValue);
          formComponent.updateFieldFromModel(fieldName);
          return result;
        }
      }
    } else {
      return mirror.delegate(invocation);
    }
    return null;
    // in case of no returning operation
  }
}