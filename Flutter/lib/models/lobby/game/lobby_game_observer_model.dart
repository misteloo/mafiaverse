// ignore_for_file: non_constant_identifier_names

class LobbyGameObserverModel {
  String name;
  String user_id;
  String image;

  LobbyGameObserverModel(
      {required this.image, required this.name, required this.user_id});

  factory LobbyGameObserverModel.fromJson(dynamic json) {
    return LobbyGameObserverModel(
        image: json['image'], name: json['name'], user_id: json['user_id']);
  }
}
