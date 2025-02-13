import 'package:mafia/models/lobby/game/lobby_game_player_model.dart';

class LobbyGameScreenCreatorMessageModel {
  String content;
  LobbyGamePlayerModel player;

  LobbyGameScreenCreatorMessageModel({
    required this.player,
    required this.content,
  });
}
