class LobbyCharacterModel {
  int id;
  String icon;
  String name;
  int count;
  bool selected = false;
  bool custom = false;
  String? customSide;

  LobbyCharacterModel(
      {required this.id,
      required this.icon,
      required this.name,
      this.custom = false,
      this.count = 0,
      this.customSide});

  factory LobbyCharacterModel.fromJson(dynamic json) {
    return LobbyCharacterModel(
        id: json['id'], icon: json['icon'], name: json['name']);
  }
}
