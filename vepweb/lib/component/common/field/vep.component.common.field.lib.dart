library vep.component.common.field;

import 'dart:html';
import 'dart:mirrors';
import 'dart:async';
import 'dart:js';
import 'package:angular/angular.dart';
import 'package:klang/klang.dart';
import 'package:vepweb/http/vep.http.lib.dart';
import 'package:vepweb/component/common/form/vep.component.form.lib.dart';

part 'action-row.dart';
part 'field.dart';
part 'input.dart';
part 'input-email.dart';
part 'input-html.dart';
part 'input-integer.dart';
part 'input-password.dart';
part 'input-text.dart';
part 'valueChange.dart';

class VepComponentCommonFieldsModule extends Module {
  VepComponentCommonFieldsModule() {
    bind(ActionRowComponent);

    bind(InputEmailComponent);
    bind(InputEmailDecorator);

    bind(InputHtmlComponent);
    bind(InputHtmlDecorator);

    bind(InputIntegerComponent);
    bind(InputIntegerDecorator);

    bind(InputPasswordComponent);
    bind(InputPasswordDecorator);

    bind(InputTextComponent);
    bind(InputTextDecorator);
  }
}