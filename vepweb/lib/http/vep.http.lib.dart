library vep.http;

import 'dart:async';
import 'package:angular/angular.dart';
import 'package:logging/logging.dart';
import 'package:jsonx/jsonx.dart' as jsonx;
import 'package:vepweb/constants.dart' as constants;
import 'package:vepweb/errors.dart';
import 'package:http/http.dart' as http;
import 'package:http/browser_client.dart';
import 'package:vepweb/component/main/vep.component.main.lib.dart';
import 'package:crypto/crypto.dart';
import 'dart:convert';

part 'Http.dart';
part 'HttpResult.dart';

class VepHttpModule extends Module {
  VepHttpModule() {
    bind(IHttp, toImplementation: HttpProduction);
    bind(http.Client, toFactory: () => new BrowserClient());
  }
}