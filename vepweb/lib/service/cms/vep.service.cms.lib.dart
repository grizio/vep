library vep.service.cms;

import 'package:angular/angular.dart';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:vepweb/model/vep.model.lib.dart';
import 'dart:async';
import 'dart:convert';
import 'package:jsonx/jsonx.dart' as jsonx;
import 'dart:html';
import 'package:logging/logging.dart';

part 'ContactService.dart';
part 'PageService.dart';

class VepServiceCmsModule extends Module {
  VepServiceCmsModule() {
    bind(ContactService);
    bind(PageService);
  }
}