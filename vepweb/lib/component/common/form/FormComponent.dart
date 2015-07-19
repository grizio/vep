part of vep.component.common.form;

typedef Future OnFormLoaded();

typedef Future<HttpResult> OnFormSubmit(Object data);

typedef Future OnFormValid(HttpResult httpResult);

typedef Future OnFormError(HttpResult httpResult);

typedef Future OnFormDone();

abstract class FormComponentContainer<A extends FormComponent> {
  A form;
}

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
    var ctx = utils.getContext(scope, FormComponentContainer) as FormComponentContainer;
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
              propagateErrors({'_root_': [(result as HttpResultError).errorMessage]});
            } else if (result is HttpResultErrors) {
              propagateErrors((result as HttpResultErrors).errorMessages);
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