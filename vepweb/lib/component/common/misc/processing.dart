part of vep.component.common.misc;

@Component(
    selector: 'processing',
    templateUrl: '/packages/vepweb/component/common/misc/processing.html',
    useShadowDom: false)
class ProcessingComponent {
  bool _inProgress;

  Timer timer;

  String state;

  @NgAttr('text-processing')
  String textProcessing;

  @NgAttr('text-done')
  String textDone;

  @NgOneWay('duration')
  int duration;

  bool get inProgress => inProgress;

  @NgOneWay('in-progress')
  set inProgress(bool value) {
    if (value == null) {
      _inProgress = false;
      stopTimer();
    } else if (_inProgress == null) {
      _inProgress = value;
      if (value) {
        state = 'processing';
      } else {
        state = 'none';
      }
      stopTimer();
    } else if (_inProgress && !value) {
      _inProgress = false;
      state = 'done';
      startTimer(true);
    } else if (!_inProgress && value) {
      _inProgress = true;
      state = 'processing';
      stopTimer();
    }
  }

  void startTimer([bool replace=false]) {
    if (replace) {
      stopTimer();
    }
    if (timer == null) {
      timer = new Timer(new Duration(milliseconds: duration), () {
        state = 'none';
      });
    }
  }

  void stopTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }
}