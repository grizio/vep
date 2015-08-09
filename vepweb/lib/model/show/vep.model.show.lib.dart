library vep.model.show;

import 'package:jsonx/jsonx.dart' as jsonx;

part 'Show.dart';

void prepareJsonx() {
  jsonx.jsonToObjects[ShowSearchResponse] = (json) {
    var showSearchResponse = new ShowSearchResponse();
    showSearchResponse.pageMax = json['pageMax'];
    showSearchResponse.shows = (json['shows'] as List<Map<String, Object>>).map((showJson){
      var show = new Show();
      show.canonical = showJson['canonical'];
      show.title = showJson['title'];
      show.author = showJson['author'];
      show.director = showJson['director'];
      show.company = showJson['company'];
      return show;
    }).toList();
    return showSearchResponse;
  };
}