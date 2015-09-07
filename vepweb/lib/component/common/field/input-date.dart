part of vep.component.common.field;

/// This component provides the user to enter a date value.
@Component(
    selector: 'input-date',
    templateUrl: '/packages/vepweb/component/common/field/input-date.html',
    useShadowDom: false)
class InputDateComponent extends InputComponent<DateTime> {
  static const typeDate = 'date';
  static const typeTime = 'time';
  static const typeDatetime = 'datetime';

  /// Date formats for display.
  static const displayFormatsDT = const<String, String>{
    typeDate: 'dd/MM/yyyy',
    typeTime: 'HH:mm',
    typeDatetime: 'dd/MM/yyyy HH:mm'
  };

  /// Date formats for inner values (with datetimepicker)
  static const displayFormatsDP = const<String, String>{
    typeDate: 'd/m/Y',
    typeTime: 'H:i',
    typeDatetime: 'd/m/Y H:i'
  };

  /// Date format for outer attributes ([min] and [max]).
  static const devFormatDP = const<String, String>{
    typeDate: 'Y-m-d',
    typeTime: 'H:i',
    typeDatetime: 'Y-m-d'
  };

  /// Date format for outer attributes ([min] and [max]).
  static const devFormatDT = const<String, String>{
    typeDate: 'yyyy-MM-dd',
    typeTime: 'HH:mm',
    typeDatetime: 'yyyy-MM-dd'
  };

  String _type;

  @NgAttr('type')
  String get type => _type;

  set type(String type) {
    if ([typeDate, typeDatetime, typeTime].contains(type)) {
      _type = type;
    } else {
      throw 'The type $type is not valid for date. Authorized types = [$typeDate, $typeDatetime, $typeTime].';
    }
  }

  @NgOneWay('min')
  DateTime min;

  @NgOneWay('max')
  DateTime max;

  @NgOneWay('step')
  int step;

  String get innerValue => value != null ? new DateFormat(displayFormatsDT[type]).format(value) : null;

  set innerValue(String innerValue) {
    try {
      if (stringUtils.isNotBlank(innerValue)) {
        value = new DateFormat(displayFormatsDT[type]).parse(innerValue);
      } else {
        value = null;
      }
    } catch (e) {
      value = null;
    }
  }

  bool verify() {
    super.verify();
    try {
      if (stringUtils.isNotBlank(innerValue)) {
        new DateFormat(displayFormatsDT[type]).parse(innerValue);
      }
    } catch (e) {
      errors.add('Le format de la date est invalide (requis: ${displayFormatsDT[type]}).');
    }
    return errors.isEmpty;
  }
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: 'input.input-date')
class InputDateDecorator extends InputDecorator {
  InputDateDecorator(Element element) : super(element);

  set scope(Scope scope) {
    super.scope = scope;
    init();
  }

  @override
  void includeAttributes(Scope scope) {
    super.includeAttributes(scope);
    var ctx = utils.getContext(scope, (_) => _ is InputDateComponent) as InputDateComponent;
    if (ctx != null) {
      addAttribute('min', ctx.min != null ? ctx.min.toIso8601String() : null);
      addAttribute('max', ctx.max != null ? ctx.max.toIso8601String() : null);
    }
  }

  void init() {
    if (scope.context is InputDateComponent) {
      final ctx = scope.context as InputDateComponent;
      final jqElement = context['\$'].apply([element]);
      var options = {
        'format': InputDateComponent.displayFormatsDP[ctx.type],
        'lang': 'fr',
        'onChangeDateTime': (DateTime date, a, b) {
          if (date != null) {
            ctx.innerValue = new DateFormat(InputDateComponent.displayFormatsDT[ctx.type]).format(date);
          }
        }
      };

      if (ctx.type == InputDateComponent.typeDate) {
        options.addAll({
          'formatDate': InputDateComponent.devFormatDP[InputDateComponent.typeDate],
          'datepicker': true,
          'timepicker': false
        });
      } else if (ctx.type == InputDateComponent.typeTime) {
        options.addAll({
          'formatTime': InputDateComponent.devFormatDP[InputDateComponent.typeTime],
          'datepicker': false,
          'timepicker': true,
          'timepickerScrollbar': false
        });
      } else if (ctx.type == InputDateComponent.typeDatetime) {
        options.addAll({
          'formatDate': InputDateComponent.devFormatDP[InputDateComponent.typeDatetime],
          'datepicker': true,
          'timepicker': true,
          'timepickerScrollbar': false
        });
      }

      if (ctx.step != null) {
        options['step'] = ctx.step;
      }

      options['onShow'] = ([a, b, c]) {
        var options = {};
        if (ctx.type == InputDateComponent.typeDate) {
          if (ctx.min != null) {
            options['minDate'] = new DateFormat(InputDateComponent.devFormatDT[InputDateComponent.typeDate]).format(ctx.min);
          }
          if (ctx.max != null) {
            options['maxDate'] = new DateFormat(InputDateComponent.devFormatDT[InputDateComponent.typeDate]).format(ctx.max);
          }
        } else if (ctx.type == InputDateComponent.typeTime) {
          if (ctx.min != null) {
            options['minTime'] = new DateFormat(InputDateComponent.devFormatDT[InputDateComponent.typeTime]).format(ctx.min);
          }
          if (ctx.max != null) {
            options['maxTime'] = new DateFormat(InputDateComponent.devFormatDT[InputDateComponent.typeTime]).format(ctx.max);
          }
        } else if (ctx.type == InputDateComponent.typeDatetime) {
          if (ctx.min != null) {
            options['minDate'] = new DateFormat(InputDateComponent.devFormatDT[InputDateComponent.typeDatetime]).format(ctx.min);
          }
          if (ctx.max != null) {
            options['maxDate'] = new DateFormat(InputDateComponent.devFormatDT[InputDateComponent.typeDatetime]).format(ctx.max);
          }
        }
        jqElement.callMethod('datetimepicker', [new JsObject.jsify(options)]);
      };

      jqElement.callMethod('datetimepicker', [new JsObject.jsify(options)]);
    }
  }
}