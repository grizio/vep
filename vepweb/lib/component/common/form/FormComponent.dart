part of vep.component.common.form;

abstract class FormComponent<A> extends FieldContainer<A> {
  @NgTwoWay('processing')
  bool processing = false;

  @NgTwoWay('done')
  bool done = false;

  void submit(Event e) {
    e.preventDefault();
    if (isValid) {
      processing = true;
      var data = (formData as FormDataProxy).innerObject;
      onSubmit(data).then((result) {
        if (result.isSuccess) {
          done = true;
          onDone(result);
        } else {
          if (result is HttpResultErrors) {
            setErrors(result);
          } else if (result is HttpResultError) {
            setError(result);
          }
          processing = false;
        }
      });
    }
  }

  Future<HttpResult> onSubmit(A data);

  void onDone(HttpResult httpResult){}
}