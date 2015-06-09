part of vep.component.common.link;

@Component(
    selector: 'pagination',
    templateUrl: '/packages/vepweb/component/common/link/pagination.html',
    useShadowDom: false)
class PaginationComponent {
  @NgOneWay('centered')
  bool centered;

  @NgTwoWay('page')
  int page = 1;

  @NgOneWay('pageMax')
  int pageMax;

  List<int> get pageList {
    if (pageMax < 15) {
      return new List.generate(pageMax, (i) => i + 1);
    } else if (page <= 7) {
      var result = new List.generate(max(5, page + 2), (i) => i + 1);
      result.add(0);
      for (int i = pageMax - 5; i <= pageMax; i++) {
        result.add(i);
      }
      return result;
    } else if (page >= pageMax - 7) {
      var result = new List.generate(5, (i) => i + 1);
      result.add(0);
      for (int i = min(pageMax - 5, page - 2); i <= pageMax; i++) {
        result.add(i);
      }
      return result;
    } else {
      var result = new List.generate(5, (i) => i + 1);
      if (page != 8) {
        result.add(0);
      }
      for (int i = page - 2; i <= page + 2; i++) {
        result.add(i);
      }
      if (page != pageMax - 8) {
        result.add(0);
      }
      for (int i = pageMax - 5; i <= pageMax; i++) {
        result.add(i);
      }
      return result;
    }
  }
}