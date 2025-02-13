import 'package:bot_toast/bot_toast.dart';
import 'package:either_dart/either.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/profile/profile_model.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:package_info_plus/package_info_plus.dart';

class ProfileScreenController extends GetxController {
  ProfileScreenController() {
    bindVersionName();
  }
  // injections
  final SharedPrefManager _shared = Get.find();
  final ApiRepository _api = Get.find();
  final HashRouting _router = Get.find();

  // variables
  ProfileModel? profile;
  var appVersionName = ''.obs;

  void navigateToReportBug() {
    Get.toNamed(_router.getRoute('report_bug'));
  }

  void navigateToEditProfile() async {
    Get.toNamed(
      _router.getRoute('edit_profile'),
      arguments: {'profile': profile},
    )?.then((value) {
      if (value != null) {
        loadProfile();
      }
    });
  }

  Future<void> bindVersionName() async {
    var info = await PackageInfo.fromPlatform();
    appVersionName.value = info.version;
  }

  String _userToken() {
    return _shared.readString('token')!;
  }

  String _userId() {
    return _shared.readString('user_id')!;
  }

  String shareContent() {
    return 'من تو بازی عصر مافیا بازی میکنم ، کد معرف من ${_userId()} با کد معرف من توام ثبت نام کن تا دوتایی سکه جایزه بگیریم';
  }

  String totalWinPercent() {
    if (profile?.win_as_mafia == 0 && profile?.win_as_citizen == 0) {
      return '0.0';
    }

    if (profile?.win_as_mafia == 0 && profile?.win_as_citizen != 0) {
      return (((profile?.win_as_citizen * 100.0) / profile?.game_as_citizen)
              as double)
          .toStringAsFixed(2);
    }

    if (profile?.win_as_mafia != 0 && profile?.win_as_citizen == 0) {
      return (((profile?.win_as_mafia * 100.0) / profile?.game_as_mafia)
              as double)
          .toStringAsFixed(2);
    }
    return (((profile?.win_as_mafia + profile?.win_as_citizen) *
            100.0 /
            (profile?.game_as_citizen + profile?.game_as_mafia)) as double)
        .toStringAsFixed(2);
  }

  String mafiaSideWinRate() {
    if (profile?.game_as_mafia == 0) {
      return '0.0';
    }

    var call =
        (((profile?.win_as_mafia * 100.0) / profile?.game_as_mafia) as double);

    return call.toStringAsFixed(2);
  }

  String citizenSideWinRate() {
    if (profile?.game_as_citizen == 0) {
      return '0.0';
    }

    var call = (((profile?.win_as_citizen * 100.0) / profile?.game_as_citizen)
        as double);

    return call.toStringAsFixed(2);
  }

  void loadProfile() async {
    Map body = {'token': _userToken()};
    Either response = await _api.myProfile(body);

    response.fold((left) {
      BotToast.showText(
          text: 'عدم ارتباط', duration: const Duration(seconds: 3));
    }, (right) {
      profile = ProfileModel.fromJson(right.data['data']);
      update(['userData']);
    });
  }

  void exit() async {
    await _shared.removeData('token');
    await _shared.removeData('user_id');

    await Get.offAllNamed(_router.getRoute('login'),
        arguments: {'exitAccount': true});
  }
}
