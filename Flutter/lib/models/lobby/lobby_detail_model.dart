// ignore_for_file: non_constant_identifier_names

class LobbyDetailModel {
  int player_cnt;
  LobbyDetailPlayerModel creator;
  List<LobbyDetailPlayerModel> players;

  LobbyDetailModel(
      {required this.player_cnt, required this.creator, required this.players});

  factory LobbyDetailModel.fromJson(dynamic json) {
    return LobbyDetailModel(
      player_cnt: json['player_cnt'],
      creator: LobbyDetailPlayerModel.fromJson(json['creator']),
      players: json['players']
          .map<LobbyDetailPlayerModel>(
            (item) => LobbyDetailPlayerModel.fromJson(item),
          )
          .toList() as List<LobbyDetailPlayerModel>,
    );
  }
}

class LobbyDetailPlayerModel {
  String name;
  String user_id;
  String image;

  LobbyDetailPlayerModel(
      {required this.image, required this.name, required this.user_id});

  factory LobbyDetailPlayerModel.fromJson(dynamic json) {
    return LobbyDetailPlayerModel(
        image: json['image'], name: json['name'], user_id: json['user_id']);
  }
}
