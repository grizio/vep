part of vep.service.common;

@Injectable()
class GeocodingService {
  Future<LatLng> findLatLngByAddress(String address) {
    final geocoder = new Geocoder();
    final request = new GeocoderRequest();
    request.address = address;
    var completer = new Completer<LatLng>();
    geocoder.geocode(request, (results, status) {
      if (status == GeocoderStatus.OK) {
        completer.complete(results[0].geometry.location);
      } else {
        completer.completeError('Geocode was not successful for the following reason: $status');
      }
    });
    return completer.future;
  }
}