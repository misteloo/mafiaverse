import 'package:bot_toast/bot_toast.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/leader_board/leader_board_model.dart';

class LeaderBoardScreenController extends GetxController {
  // injections
  final SharedPrefManager _shared = Get.find();
  final ApiRepository _api = Get.find();

  // instances
  final List<LeaderBoardModel> leaderBoardList =
      List<LeaderBoardModel>.empty(growable: true);

  bool? availableLeaderBoard;

  bool loading = true;
  String _userToken() {
    return _shared.readString('token')!;
  }

  void getLeaderBoard() async {
    Map body = {'token': _userToken()};

    var response = await _api.getLeaderBoard(body);
    response.fold((left) {
      BotToast.showText(text: 'خطا در دریافت اطلاعات');
      loading = false;
      update(['loading']);
    }, (right) {
      var availableLeaderBoard = right.data['data']['available'];
      if (!availableLeaderBoard) {
        this.availableLeaderBoard = availableLeaderBoard;
        loading = false;
        update(['loading', 'available']);

        return;
      }
      var ranking = right.data['data']['ranking']
          .map<LeaderBoardModel>((json) => LeaderBoardModel.fromJson(json))
          .toList();
      leaderBoardList.clear();
      leaderBoardList.addAll(ranking);
      loading = false;
      update(['loading', 'ranks']);
      exportTimeLeft((leaderBoardList[0].session_end -
              DateTime.now().millisecondsSinceEpoch) /
          1000.0);
    });
  }

  Future<List<int>> exportTimeLeft(dynamic time) async {
    var day = await _export(time / 86400.0);
    var dayDecimal = _exportDecimal(time / 86400.0);
    var hour = await _export(dayDecimal * 24.0);
    var hourDecimal = _exportDecimal(dayDecimal * 24);
    var min = await _export(hourDecimal * 60.0);
    var minDecimal = _exportDecimal(hourDecimal * 60);
    var sec = await _export(minDecimal * 60);
    return [day, hour, min, sec];
  }

  double _exportDecimal(dynamic num) {
    return double.parse(
        '0${num.toString().substring(_index(num), num.toString().length)}');
  }

  Future<int> _export(dynamic num) async {
    return int.parse(num.toString().substring(0, _index(num)));
  }

  int _index(dynamic num) {
    return num.toString().indexOf('.');
  }
}
