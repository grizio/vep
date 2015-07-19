import 'package:angular/application_factory.dart';
import 'package:logging/logging.dart';

import 'package:vepweb/constants.dart' as constants;
import 'package:vepweb/vep.lib.dart';

void main() {
  Logger.root.level = constants.loggerLevel;
  Logger.root.onRecord.listen((LogRecord r) {
    print("[${r.level}][${r.loggerName}][${r.time.toIso8601String()}]${r.message}");
  });

  applicationFactory().addModule(new VepModule()).run();
}
