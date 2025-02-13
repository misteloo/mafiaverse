class LobbyGameCreatorModel {
  bool speech;
  bool connected;
  String name;
  String avatar;
  bool private;

  LobbyGameCreatorModel(
      {required this.name,
      required this.avatar,
      required this.connected,
      required this.speech,
      required this.private});

  factory LobbyGameCreatorModel.fromJson(dynamic json) {
    return LobbyGameCreatorModel(
        name: json['name'],
        avatar: json['avatar'],
        connected: json['connected'],
        speech: json['speech'],
        private: json['private']);
  }
}
