import 'package:get/get.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/game/in_game_user_model.dart';
import 'package:mafia/models/game/mafia_visitation_model.dart';
import 'package:mafia/utils/constants/game_character.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/socket/listeners/reconnect/socket_reconnect_listeners.dart';

import '../../../models/game/in_game_user_identity_model.dart';
import '../../../utils/socket/socket_manager.dart';

class ReconnectScreenController extends GetxController
    implements SocketReconnectListener {
  // inject
  final SharedPrefManager _shared = Get.find();
  final SocketManager _socket = Get.find();
  final HashRouting _routing = Get.find();

  String get getUserId => _shared.readString('user_id')!;

  void reconnect() {
    Future.delayed(const Duration(milliseconds: 200), () {
      Map<String, String> json = {'op': 'reconnect'};
      _socket.emit('game_handle', json);
    });
  }

  @override
  void onReconnect(data) async {
    var parse = data['data'];
    List<dynamic> jsonUsersData = parse['users_data'];
    String event = parse['game_event'];
    List<dynamic> jsonGameAction = parse['game_action'];
    String roomId = parse['room_id'];
    String character = parse['character'];
    bool gunStatus = parse['gun_status'];
    List<dynamic> jsonMafiaList = parse['mafia_list'];
    parse['join_type']; // is string
    parse['roles']; // is dynamic list
    String gameId = parse['game_id'];

    Map<String, String> findCharacter =
        GameCharacter().getCharacter('nato', character);
    List<InGameUserIdentityModel> usersIdentity = jsonUsersData
        .map<InGameUserIdentityModel>(
            (json) => InGameUserIdentityModel.fromJson(json))
        .toList();

    String characterImage = findCharacter['image']!;
    String characterName = findCharacter['name']!;

    List<InGameUserModel> users =
        await _bindUserModel(usersIdentity, jsonGameAction);

    List<MafiaVisitationModel> mafiaList = await _bindMafiaList(jsonMafiaList);
    // navigate
    Future.delayed(const Duration(milliseconds: 500), () {
      Get.offNamed(_routing.getRoute('nato_game'), arguments: {
        'characterName': characterName,
        'characterImage': characterImage,
        'serverCharacter': character,
        'users': users,
        'gameEvent': event,
        'gameId': gameId,
        'roomId': roomId,
        'userId': getUserId,
        'gunStatus': gunStatus,
        'mafiaList': mafiaList,
        'fromReconnect': true
      });
    });
  }

  @override
  void onInit() {
    super.onInit();
    // add listener
    _socket.getReconnectSocket.addListener(this);
  }
}

Future<List<MafiaVisitationModel>> _bindMafiaList(List<dynamic> mafia) async {
  if (mafia.isEmpty) {
    return List<MafiaVisitationModel>.empty();
  } else {
    return mafia
        .map<MafiaVisitationModel>(
          (it) => MafiaVisitationModel.fromJson(it, 'nato'),
        )
        .toList();
  }
}

Future<List<InGameUserModel>> _bindUserModel(
    List<InGameUserIdentityModel> usersIdentity,
    List<dynamic> jsonGameAction) async {
  return jsonGameAction.map<InGameUserModel>((it) {
    String userId = it['user_id'];
    dynamic userStatus = it['user_status'];
    return InGameUserModel(
        userIdentity:
            usersIdentity.singleWhere((element) => element.user_id == userId))
      ..connected = userStatus['is_connected']
      ..alive = userStatus['is_alive']
      ..speech = userStatus['is_talking'];
  }).toList();
}
