part of vep.component.common.field;

typedef Future<List<Choice<String>>> InputSearchFunction(String criteria);

typedef Future<Choice<String>> InputSearchInverseFunction(String value);

/// This component provides the user to search a value.
@Component(
    selector: 'input-search',
    templateUrl: '/packages/vepweb/component/common/field/input-search.html',
    useShadowDom: false)
class InputSearchComponent extends InputComponent<String> {
  bool working = false;
  bool isInvalid = false;

  String _displayedValue;

  String get displayedValue => _displayedValue;

  set displayedValue(String displayedValue) {
    _displayedValue = displayedValue;
    startTimer();
  }

  Timer timer;
  Timer timerValid;
  Future searching;

  @NgOneWay('search')
  InputSearchFunction search;

  @NgOneWay('inverse-search')
  InputSearchInverseFunction inverseSearch;

  @NgOneWay('timer-wait')
  int timerWait = 1000;

  bool _isNormalSet = false;

  @override
  set value(String newValue) {
    isInvalid = false;
    _isNormalSet = true;
    super.value = newValue == '' ? null : newValue;
    _isNormalSet = false;
  }

  @override
  void forceSetValue(String newValue) {
    super.forceSetValue(newValue);
    if (!_isNormalSet) {
      searching = searching != null ? searching : new Future.value();
      searching = searching.then((_){
        return inverseSearch(newValue).then((Choice choice){
          value = choice.value;
          _displayedValue = choice.label;
        });
      });
    }
  }

  List<Choice> choices = [];

  @override
  bool verify() {
    super.verify();
    if (isInvalid) {
      errors.add('Aucun élément ne correspond à votre recherche');
    }
    return errors.isEmpty;
  }

  // TODO: PROBLEM DE TIMER !!!!!!!
  void valid(Choice<String> givenChoice, [Event event]) {
    if (event != null) {
      event.preventDefault();
    }
    if (timerValid != null) {
      timerValid.cancel();
      timerValid = null;
    }
    if (givenChoice == null) {
      timerValid = new Timer(new Duration(milliseconds: 500), () {
        working = true;
        isInvalid = false;
        search(displayedValue).then((List<Choice<String>> choices) {
          working = false;
          if (choices != null && choices.isNotEmpty) {
            valid(choices.first);
          } else {
            value = null;
            isInvalid = true;
          }
          verify();
        });
        timerValid.cancel();
        timerValid = null;
      });
    } else {
      value = givenChoice.value;
      displayedValue = givenChoice.label;
      verify();
    }
  }

  void invalid() {
    value = null;
  }

  void reloadChoices() {
    Future future = searching != null ? searching : new Future.value();
    searching = future.then((_) {
      search(displayedValue).then((_) {
        choices = _;
      });
    });
  }

  void startTimer() {
    endTimer();
    timer = new Timer(new Duration(milliseconds: timerWait), () {
      reloadChoices();
      endTimer();
    });
  }

  void endTimer() {
    if (timer != null) {
      if (timer.isActive) {
        timer.cancel();
      }
      timer = null;
    }
  }
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: 'input.input-search')
class InputSearchDecorator extends InputDecorator {
  InputSearchDecorator(Element element) : super(element);
}