library vep.model.session;

import 'package:jsonx/jsonx.dart' as jsonx;
import 'package:vepweb/model/vep.model.lib.dart';
import 'package:intl/intl.dart';

part 'Session.dart';
part 'SessionSearch.dart';

void prepareJsonx() {
  prepareJsonxSessionSearch();
}