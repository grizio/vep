library vep.model;

export 'user/vep.model.user.lib.dart';
export 'cms/vep.model.cms.lib.dart';
export 'theater/vep.model.theater.lib.dart';
export 'company/vep.model.company.lib.dart';
export 'show/vep.model.show.lib.dart' hide prepareJsonx;

import 'show/vep.model.show.lib.dart' as show;

prepareJsonx() {
  show.prepareJsonx();
}