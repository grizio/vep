library vep.component.common.field;

import 'dart:html';
import 'dart:js';
import 'package:angular/angular.dart';
import 'package:vepweb/component/common/form/vep.component.form.lib.dart';
import 'package:vepweb/component/common/misc/vep.component.common.misc.lib.dart';
import 'package:google_maps/google_maps_places.dart';
import 'package:vepweb/utils.dart' as utils;
import 'package:intl/intl.dart';
import 'package:klang/utilities/string.dart' as stringUtils;
import 'dart:async';
import 'package:vepweb/component/common/date-constants.dart' as dateConstants;


part 'action-row.dart';
part 'Choice.dart';
part 'field.dart';
part 'input.dart';
part 'input-address.dart';
part 'input-date.dart';
part 'input-email.dart';
part 'input-html.dart';
part 'input-integer.dart';
part 'input-multiple-choice.dart';
part 'input-one-choice.dart';
part 'input-password.dart';
part 'input-price.dart';
part 'input-search.dart';
part 'input-text.dart';
part 'valueChange.dart';

class VepComponentCommonFieldsModule extends Module {
  VepComponentCommonFieldsModule() {
    bind(ActionRowComponent);

    bind(InputAddressComponent);
    bind(InputAddressDecorator);

    bind(InputDateComponent);
    bind(InputDateDecorator);

    bind(InputEmailComponent);
    bind(InputEmailDecorator);

    bind(InputHtmlComponent);
    bind(InputHtmlDecorator);

    bind(InputIntegerComponent);
    bind(InputIntegerDecorator);

    bind(InputMultipleChoiceComponent);
    bind(InputMultipleChoiceDecorator);

    bind(InputOneChoiceComponent);
    bind(InputOneChoiceDecorator);

    bind(InputPasswordComponent);
    bind(InputPasswordDecorator);

    bind(InputPriceComponent);
    bind(InputPriceDecorator);

    bind(InputSearchComponent);
    bind(InputSearchDecorator);

    bind(InputTextComponent);
    bind(InputTextDecorator);
  }
}