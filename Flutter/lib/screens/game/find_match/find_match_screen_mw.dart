import 'package:audioplayers/audioplayers.dart';
import 'package:get/get.dart';

class FindMatchScreenMw extends GetMiddleware {
  final AudioPlayer _audioPlayer = AudioPlayer();
  @override
  void onPageDispose() async {
    await _audioPlayer.stop();
    super.onPageDispose();
  }

  @override
  GetPage? onPageCalled(GetPage? page) {
    _audioPlayer.play(AssetSource('find_match.mp3'));

    _audioPlayer.onPlayerStateChanged.listen((event) {
      if (event == PlayerState.completed) {
        _audioPlayer.play(AssetSource('find_match.mp3'));
      }
    });
    return super.onPageCalled(page);
  }
}
