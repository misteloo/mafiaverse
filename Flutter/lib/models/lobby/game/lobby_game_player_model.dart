// ignore_for_file: non_constant_identifier_names

import 'package:mafia/models/lobby/game/lobby_game_player_status_model.dart';

class LobbyGamePlayerModel {
  String name;
  String user_id;
  String avatar;
  int user_index;
  String character;
  String side;
  LobbyGamePlayerStatusModel status;

  LobbyGamePlayerModel(
      {required this.name,
      required this.user_id,
      required this.avatar,
      required this.side,
      required this.character,
      required this.user_index,
      required this.status});

  factory LobbyGamePlayerModel.fromJson(dynamic json) {
    return LobbyGamePlayerModel(
        name: json['name'],
        user_id: json['user_id'],
        avatar: json['avatar'],
        side: json['side'],
        character: json['character'],
        user_index: json['user_index'],
        status: LobbyGamePlayerStatusModel.fromJson(json['status']));
  }
}
