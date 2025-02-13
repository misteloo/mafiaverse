// ignore_for_file: non_constant_identifier_names

class LobbyListModel {
  String name;
  String scenario;
  int player_cnt;
  String lobby_id;
  List<LobbyListCharacterModel> characters;
  List<LobbyListCardsModel> cards;
  LobbyListCreatorData creator;
  List<String> ban_list = List<String>.empty(growable: true);
  bool started;
  bool private;
  List<String> sides;
  List<LobbyListPlayersData> players;
  bool loading = false;

  LobbyListModel(
      {required this.name,
      required this.lobby_id,
      required this.scenario,
      required this.player_cnt,
      required this.characters,
      required this.cards,
      required this.creator,
      required this.ban_list,
      required this.started,
      required this.sides,
      required this.private,
      required this.players});

  factory LobbyListModel.fromJson(dynamic json) {
    return LobbyListModel(
      name: json['name'],
      lobby_id: json['lobby_id'],
      scenario: json['scenario'],
      player_cnt: json['player_cnt'],
      characters: json['characters']
          .map<LobbyListCharacterModel>(
              (item) => LobbyListCharacterModel.fromJson(item))
          .toList(),
      cards: json['cards']
          .map<LobbyListCardsModel>(
              (item) => LobbyListCardsModel.fromJson(item))
          .toList(),
      creator: LobbyListCreatorData.fromJson(json['creator']),
      ban_list: List<String>.from(json['ban_list']),
      started: json['started'],
      sides: List<String>.from(
        json['sides'],
      ),
      private: json['private'] ?? false,
      players: json['players']
          .map<LobbyListPlayersData>(
              (json) => LobbyListPlayersData.fromJson(json))
          .toList(),
    );
  }
}

class LobbyListCharacterModel {
  String name;
  bool custom;
  int count;
  int id;
  String? custom_side;

  LobbyListCharacterModel(
      {required this.name,
      required this.count,
      required this.custom,
      required this.id,
      required this.custom_side});

  factory LobbyListCharacterModel.fromJson(dynamic json) {
    return LobbyListCharacterModel(
        name: json['name'],
        count: json['count'],
        custom: json['custom'],
        id: json['id'],
        custom_side: json['custom_side']);
  }
}

class LobbyListCardsModel {
  String name;

  LobbyListCardsModel({required this.name});

  factory LobbyListCardsModel.fromJson(dynamic json) {
    return LobbyListCardsModel(name: json['name']);
  }
}

class LobbyListCreatorData {
  String lobby_id;
  String user_id;
  String name;
  String image;
  bool loadingProfile = false;

  LobbyListCreatorData(
      {required this.image,
      required this.lobby_id,
      required this.name,
      required this.user_id});

  factory LobbyListCreatorData.fromJson(dynamic json) {
    return LobbyListCreatorData(
        image: json['image'],
        lobby_id: json['lobby_id'],
        name: json['name'],
        user_id: json['user_id']);
  }
}

class LobbyListPlayersData {
  String user_id;

  LobbyListPlayersData({required this.user_id});

  factory LobbyListPlayersData.fromJson(dynamic json) {
    return LobbyListPlayersData(user_id: json['user_id']);
  }
}
