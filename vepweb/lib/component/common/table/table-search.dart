part of vep.component.common.table;

/// This component is used to display a table where the user can search objects
/// and manipulate them in terms of developer configuration.
@Component(
    selector: 'table-search',
    templateUrl: '/packages/vepweb/component/common/table/table-search.html',
    useShadowDom: false)
class TableSearchComponent implements ScopeAware, AttachAware {
  TableSearchDecorator tableSearchDecorator;

  @NgOneWay('max-result')
  int maxResult;

  @NgOneWay('show-pages')
  bool showPages;

  @NgOneWay('filter-in')
  bool filterIn;

  int _page = 1;

  @NgTwoWay('page')
  int get page => _page;

  set page(int page) {
    _page = page == null ? 1 : page;
    // Usage of timer avoids errors because model of parent scope is not reloaded yet.
    timerReload(new Duration(milliseconds: 1));
  }

  @NgOneWay('page-max')
  int pageMaxFormParent = null;

  int get pageMax => pageMaxFormParent != null ? pageMaxFormParent : data == null ? 1 : max(0, ((data.length - 1) / maxResult).floor()) + 1;

  TableSearchContext context;

  TableDescriptor get tableDescriptor => context.tableDescriptor;

  Scope _scope;

  Scope get scope => _scope;

  @override
  void set scope(Scope scope) {
    _scope = scope;
    var currentScope = scope;
    while (currentScope != null && !(currentScope.context is TableSearchContext)) {
      currentScope = currentScope.parentScope;
    }
    if (currentScope != null) {
      context = currentScope.context;
    } else {
      throw 'The component table-search is not include into a scope with a TableSearchContext.';
    }
  }

  bool loading = false;
  Future<Map<String, Object>> _dataFuture;
  Timer timer = null;

  List<Map<String, Object>> data = [];
  bool fetchedData = false;

  @override
  void attach() {
    reloadData();
  }

  void reloadData() {
    if (tableDescriptor != null) {
      if (_dataFuture == null) {
        loading = true;
        if (tableSearchDecorator != null) {
          // Trick to force refresh of table.
          // TODO: find a proper way
          tableSearchDecorator.element.click();
        }
        _dataFuture = context.search(new Map.unmodifiable(filter.internal)).then((_) {
          data = _;
          _dataFuture = null;
          loading = false;
          fetchedData = true;
          if (tableSearchDecorator != null) {
            // Trick to force refresh of table.
            // TODO: find a proper way
            tableSearchDecorator.element.click();
          }
        });
      }
    }
  }

  MapFilter _filter = null;

  MapFilter get filter {
    if (tableDescriptor != null) {
      if (_filter == null) {
        if (context != null) {
          _filter = new MapFilter(tableDescriptor, this);
          return _filter;
        } else {
          return new MapFilter(null, this);
        }
      } else {
        return _filter;
      }
    } else {
      return new MapFilter(null, this);
    }
  }

  List<Map<String, Object>> get filteredData {
    if (tableDescriptor != null) {
      List<Map<String, Object>> filtered;
      if (filterIn) {
        if (!fetchedData && !loading) {
          reloadData();
        }
        var columnsToCheck = tableDescriptor.columns.where((_) => filter.containsKey(_.code) && filter[_.code] != null);
        var textColumns = columnsToCheck.where((_) => [ColumnTypes.text, ColumnTypes.link].contains(_.type));
        // We filter only when checkbox is checked, otherwise, we do not include the checkbox in filter.
        var checkboxColumns = columnsToCheck.where((_) => _.type == ColumnTypes.checkbox && filter[_.code] == true);
        var numberColumns = columnsToCheck.where((_) => [ColumnTypes.integer].contains(_.type) && !(filter[_.code] as num).isNaN);
        var dateColumns = columnsToCheck.where((_) => [ColumnTypes.date].contains(_.type) && filter[_.code] != [null, null]);
        filtered = [];
        int index = 0;
        for (Map<String, Object> row in data) {
          if (textColumns.every((_) => (row[_.code] as String).contains(filter[_.code])) &&
          checkboxColumns.every((_) => row[_.code] == filter[_.code]) &&
          numberColumns.every((_) => row[_.code].toString().contains(filter[_.code].toString())) &&
          filteredDataCheckByDate(dateColumns, row)) {
            row['_index'] = index;
            filtered.add(row);
          }
          index++;
        }
      } else {
        filtered = data;
      }

      return filteredDataPage(filtered);
    } else {
      return [];
    }
  }

  bool filteredDataCheckByDate(List<ColumnDescriptor> columns, Map<String, Object> row) {
    for (final columnDescriptor in columns) {
      final period = filter[columnDescriptor.code] as List<DateTime>;
      if (period != null && period.length == 2 && (period[0] != null || period[1] != null)) {
        final date = row[columnDescriptor.code] as DateTime;
        if (date == null) {
          return false;
        } else if (period[0] != null && date.isBefore(period[0])) {
          return false;
        } else if (period[1] != null && date.isAfter(period[0])) {

        }
      }
    }
    return true;
  }

  List<Map<String, Object>> filteredDataPage(List<Map<String, Object>> filtered) {
    if (showPages) {
      if (filterIn) {
        if (listUtilities.isEmpty(filtered) || (page - 1) * maxResult > filtered.length) {
          return [];
        } else {
          return filtered.sublist((page - 1) * maxResult, min(page * maxResult, filtered.length));
        }
      } else {
        // It is the container which do the filter.
        return filtered;
      }
    } else {
      return filtered;
    }
  }

  void onFilterChange() {
    if (!filterIn) {
      timerReload();
    }
  }

  void onChange(index, field, value) {
    data[index][field] = value;
    context.onChange(data[index]);
  }

  /// Reloads the timer to update search results, only after a duration.
  /// Useful only when filter-out
  void timerReload([Duration duration=const Duration(seconds: 1)]) {
    if (!filterIn) {
      // We use a timer to update data only 1 second of inactivity.
      if (timer != null) {
        timer.cancel();
      }
      timer = new Timer(duration, timerDone);
    }
  }

  void timerDone() {
    reloadData();
    if (timer != null) {
      timer = null;
    }
  }
}

/// This class has to be used as an interface by component including the table search.
/// It defines some methods needed by [TableSearchComponent].
abstract class TableSearchContext {
  TableDescriptor get tableDescriptor;

  Future<List<Map<String, Object>>> search(Map<String, Object> filters);

  void onChange(Map<String, Object> data);
}

/// Describes a table with columns.
class TableDescriptor {
  List<ColumnDescriptor> _columns;

  List<ColumnDescriptor> get columns => _columns;

  TableDescriptor(List<ColumnDescriptor> columns) {
    _columns = new List.from(columns, growable:false);
  }

  ColumnDescriptor operator [](Object key){
    return _columns.firstWhere((_) => _.code == key, orElse: () => null);
  }
}

/// Describes a column of a table descriptor.
class ColumnDescriptor {
  String _code;

  String get code => _code;

  String _name;

  String get name => _name;

  String _type;

  String get type => _type;

  bool _active;

  bool get active => _active;

  bool _hasFilter;

  bool get hasFilter => _hasFilter;

  String _url;

  String get url => _url;

  List<Choice> choices = [];

  String getLabelOfChoice(Object value) {
    var choice = choices.firstWhere((_) => _.value == value, orElse: () => null);
    if (choice != null) {
      return choice.label;
    } else {
      return '';
    }
  }

  void enable() {
    _active = true;
  }

  void disable() {
    _active = false;
  }

  ColumnDescriptor(this._code, this._name, this._type, {bool active:false, bool hasFilter:false, String url:null, this.choices}) {
    _active = active;
    _hasFilter = hasFilter;
    _url = url;
  }

  String link(Map<String, Object> data) {
    String result = url;
    data.forEach((k, v) {
      if (k != null && v != null) {
        result = result.replaceAll("{" + k + "}", v.toString());
      }
    });
    return result;
  }
}

abstract class ColumnTypes {
  static const String text = 'text';
  static const String checkbox = 'checkbox';
  static const String link = 'link';
  static const String integer = 'integer';
  static const String choices = 'choices';
  static const String date = 'date';
}

class MapFilter {
  final TableDescriptor tableDescriptor;
  final TableSearchComponent context;
  final Map<String, Object> internal = {};

  MapFilter(this.tableDescriptor, this.context);

  Object operator [](Object key) {
    if (tableDescriptor != null) {
      var column = tableDescriptor[key];
      if (column != null) {
        if (column.type == ColumnTypes.choices) {
          var value = internal[key];
          var i = 0;
          for (var choice in column.choices) {
            if (choice.value == value) {
              return i.toString();
            }
            i++;
          }
          return "-1";
        } else if (column.type == ColumnTypes.date) {
          internal.putIfAbsent(key, () => [null, null]);
        }
      }
    }
    return internal[key];
  }

  void operator []=(String key, Object value) {
    if (tableDescriptor != null) {
      var column = tableDescriptor[key];
      if (column != null) {
        if (column.type == ColumnTypes.choices) {
          var intValue = int.parse(value);
          if (intValue >= 0) {
            internal[key] = column.choices[intValue].value;
          } else {
            internal[key] = null;
          }
        } else {
          internal[key] = value;
        }
      } else if (key.length > 2) {
        var columnName = key.substring(0, key.length - 2);
        column = tableDescriptor[columnName];
        if (column != null && column.type == ColumnTypes.date) {
          int index = int.parse(key.substring(key.length - 1));
          DateTime dtValue;
          if (value == null) {
            dtValue = null;
          } else if (value is DateTime) {
            dtValue = value;
          } else {
            try {
              dtValue = new DateFormat(dateConstants.displayFormatsDP[dateConstants.typeDate]).parse(value);
            } catch (e) {
              try {
                dtValue = new DateFormat(dateConstants.devFormatDP[dateConstants.typeDate]).parse(value);
              } catch (e2) {
                dtValue = null;
              }
            }
          }
          (this[columnName] as List<DateTime>)[index] = dtValue;
        }
      }
    }
    context.timerReload();
  }

  // Delegates to internal

  bool containsValue(Object value) => internal.containsValue(value);

  bool containsKey(Object key) => internal.containsKey(key);

  Object remove(Object key) => internal.remove(key);

  void clear() => internal.clear();

  void forEach(void f(String key, Object value)) => internal.forEach(f);

  Iterable<String> get keys => internal.keys;

  Iterable<Object> get values => internal.values;

  int get length => internal.length;

  bool get isEmpty => internal.isEmpty;

  bool get isNotEmpty => internal.isNotEmpty;
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: '.table-search')
class TableSearchDecorator implements ScopeAware {
  final Element element;

  TableSearchDecorator(this.element);

  void set scope(Scope scope) {
    final ctx = utils.getContext(scope, (_) => _ is TableSearchComponent) as TableSearchComponent;
    if (ctx != null) {
      ctx.tableSearchDecorator = this;
    }
  }
}

/// Decorates the input with attributes in terms of developer configuration.
@Decorator(selector: '.table-search-criteria-date')
class TableSearchCriteriaDateDecorator implements ScopeAware {
  Element element;

  Scope _scope;

  Scope get scope => _scope;

  set scope(Scope scope) {
    _scope = scope;
    init();
  }

  TableSearchCriteriaDateDecorator(this.element);

  void init() {
    final jqElement = context['\$'].apply([element]);
    final ctx = utils.getContext(scope, (_) => _ is TableSearchComponent) as TableSearchComponent;
    final options = {
      'format': dateConstants.displayFormatsDP[dateConstants.typeDate],
      'lang': 'fr',
      'formatDate': dateConstants.devFormatDP[dateConstants.typeDate],
      'datepicker': true,
      'timepicker': false,
      'onChangeDateTime': (DateTime date, JsObject input, b) {
        final name = element.getAttribute('name');
        ctx.filter[name] = date;
        element.blur();
      }
    };

    jqElement.callMethod('datetimepicker', [new JsObject.jsify(options)]);
  }
}