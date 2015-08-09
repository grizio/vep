part of vep.component.common.table;

/// This component is used to display a table where the user can search objects
/// and manipulate them in terms of developer configuration.
@Component(
    selector: 'table-search',
    templateUrl: '/packages/vepweb/component/common/table/table-search.html',
    useShadowDom: false)
class TableSearchComponent implements ScopeAware {
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

  int get pageMax => pageMaxFormParent != null ? pageMaxFormParent : _data == null ? 1 : max(0, ((_data.length - 1) / maxResult).floor()) + 1;

  TableSearchContext context;

  TableDescriptor get tableDescriptor => context.tableDescriptor;

  void set scope(Scope scope) {
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

  Future<Map<String, Object>> _dataFuture;
  List<Map<String, Object>> _data;
  Timer timer = null;

  List<Map<String, Object>> get data {
    if (_data == null && _dataFuture == null) {
      _dataFuture = context.search(new Map.unmodifiable(filter.internal)).then((_) {
        _data = _;
        _dataFuture = null;
      });
    }
    return _data == null ? [] : _data;
  }

  MapFilter _filter = null;

  MapFilter get filter {
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
  }

  List<Map<String, Object>> get filteredData {
    List<Map<String, Object>> filtered;
    if (filterIn) {
      var columnsToCheck = tableDescriptor.columns.where((_) => filter.containsKey(_.code) && filter[_.code] != null);
      var textColumns = columnsToCheck.where((_) => [ColumnTypes.text, ColumnTypes.link].contains(_.type));
      // We filter only when checkbox is checked, otherwise, we do not include the checkbox in filter.
      var checkboxColumns = columnsToCheck.where((_) => _.type == ColumnTypes.checkbox && filter[_.code] == true);
      var numberColumns = columnsToCheck.where((_) => [ColumnTypes.integer].contains(_.type) && !(filter[_.code] as num).isNaN);
      filtered = [];
      int index = 0;
      for (Map<String, Object> row in data) {
        if (textColumns.every((_) => (row[_.code] as String).contains(filter[_.code])) &&
        checkboxColumns.every((_) => row[_.code] == filter[_.code]) &&
        numberColumns.every((_) => row[_.code].toString().contains(filter[_.code].toString()))) {
          row['_index'] = index;
          filtered.add(row);
        }
        index++;
      }
    } else {
      filtered = data;
    }

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
    _data[index][field] = value;
    context.onChange(_data[index]);
  }

  void timerReload([Duration duration=const Duration(seconds: 1)]) {
    // We use a timer to update data only 1 second of inactivity.
    if (timer != null) {
      timer.cancel();
    }
    timer = new Timer(duration, timerDone);
  }

  void timerDone() {
    _data = null;
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