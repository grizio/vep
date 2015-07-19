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

  @NgTwoWay('page')
  int page = 1;

  int get pageMax => _data == null ? 1 : max(0, ((_data.length - 1) / maxResult).floor()) + 1;

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

  List<Map<String, Object>> get data {
    if (_data == null && _dataFuture == null) {
      _dataFuture = context.search(filter).then((_) {
        _data = _;
        _dataFuture = null;
      });
    }
    return _data == null ? [] : _data;
  }

  Map<String, Object> filter = {};

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
      if (listUtilities.isEmpty(filtered) || (page - 1) * maxResult > filtered.length) {
        return [];
      } else {
        return filtered.sublist((page - 1) * maxResult, min(page * maxResult, filtered.length));
      }
    } else {
      return filtered;
    }
  }

  void onFilterChange() {
    if (!filterIn) {
      _data = null;
    }
  }

  void onChange(index, field, value) {
    _data[index][field] = value;
    context.onChange(_data[index]);
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

  void enable() {
    _active = true;
  }

  void disable() {
    _active = false;
  }

  ColumnDescriptor(this._code, this._name, this._type, {bool active:false, bool hasFilter:false, String url:null}) {
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
}