part of vep.service.session;

@Injectable()
class ReservationService {
  static final logger = new Logger('vep.service.sesson.ReservationService');

  final IHttp http;

  ReservationService(this.http);

  Future<HttpResult> create(String theater, String session, ReservationForm reservation) {
    return http.post('/reservation/${theater}/${session}', reservation);
  }

  Future<List<String>> findReservedPlacesPlan(String theater, String session) {
    return http.get('/reservation/${theater}/${session}/plan').then((_){
      if (_.isSuccess) {
        return _.entity['seats'];
      } else {
        return null;
      }
    });
  }

  Future<int> findReservedPlacesNumber(String theater, String session) {
    return http.get('/reservation/${theater}/${session}/number').then((_){
      if (_.isSuccess) {
        return _.entity;
      } else {
        return 0;
      }
    });
  }
}