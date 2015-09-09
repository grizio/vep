part of vep.component.common.field;

/// This component provides the user to enter a date value.
@Component(
    selector: 'input-date',
    templateUrl: '/packages/vepweb/component/common/field/input-date.html',
    useShadowDom: false)
class InputDateComponent extends InputComponent<DateTime> {
  String _type;

  @NgAttr('type')
  String get type => _type;

  set type(String type) {
    if (dateConstants.allTypes.contains(type)) {
      _type = type;
    } else {
      throw 'The type $type is not valid for date. Authorized types = [${dateConstants.allTypes.join(',')}].';
    }
  }

  @NgOneWay('min')
  DateTime min;

  @NgOneWay('max')
  DateTime max;

  @NgOneWay('step')
  int step;

  String get innerValue => value != null ? new DateFormat(dateConstants.displayFormatsDT[type]).format(value) : null;

  set innerValue(String innerValue) {
    try {
      if (stringUtils.isNotBlank(innerValue)) {
        value = new DateFormat(dateConstants.displayFormatsDT[type]).parse(innerValue);
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
        new DateFormat(dateConstants.displayFormatsDT[type]).parse(innerValue);
      }
    } catch (e) {
      errors.add('Le format de la date est invalide (requis: ${dateConstants.displayFormatsDT[type]}).');
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
        'format': dateConstants.displayFormatsDP[ctx.type],
        'lang': 'fr',
        'onChangeDateTime': (DateTime date, a, b) {
          if (date != null) {
            ctx.innerValue = new DateFormat(dateConstants.displayFormatsDT[ctx.type]).format(date);
          }
        }
      };

      if (ctx.type == dateConstants.typeDate) {
        options.addAll({
          'formatDate': dateConstants.devFormatDP[dateConstants.typeDate],
          'datepicker': true,
          'timepicker': false
        });
      } else if (ctx.type == dateConstants.typeTime) {
        options.addAll({
          'formatTime': dateConstants.devFormatDP[dateConstants.typeTime],
          'datepicker': false,
          'timepicker': true,
          'timepickerScrollbar': false
        });
      } else if (ctx.type == dateConstants.typeDatetime) {
        options.addAll({
          'formatDate': dateConstants.devFormatDP[dateConstants.typeDatetime],
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
        if (ctx.type == dateConstants.typeDate) {
          if (ctx.min != null) {
            options['minDate'] = new DateFormat(dateConstants.devFormatDT[dateConstants.typeDate]).format(ctx.min);
          }
          if (ctx.max != null) {
            options['maxDate'] = new DateFormat(dateConstants.devFormatDT[dateConstants.typeDate]).format(ctx.max);
          }
        } else if (ctx.type == dateConstants.typeTime) {
          if (ctx.min != null) {
            options['minTime'] = new DateFormat(dateConstants.devFormatDT[dateConstants.typeTime]).format(ctx.min);
          }
          if (ctx.max != null) {
            options['maxTime'] = new DateFormat(dateConstants.devFormatDT[dateConstants.typeTime]).format(ctx.max);
          }
        } else if (ctx.type == dateConstants.typeDatetime) {
          if (ctx.min != null) {
            options['minDate'] = new DateFormat(dateConstants.devFormatDT[dateConstants.typeDatetime]).format(ctx.min);
          }
          if (ctx.max != null) {
            options['maxDate'] = new DateFormat(dateConstants.devFormatDT[dateConstants.typeDatetime]).format(ctx.max);
          }
        }
        jqElement.callMethod('datetimepicker', [new JsObject.jsify(options)]);
      };

      jqElement.callMethod('datetimepicker', [new JsObject.jsify(options)]);
    }
  }
}