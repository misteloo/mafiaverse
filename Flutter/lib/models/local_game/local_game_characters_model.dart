class LocalGameCharactersModel {
  int id;
  String side;
  String icon;
  String name;
  String description;
  bool multi;

  LocalGameCharactersModel(
      {required this.description,
      required this.icon,
      required this.id,
      required this.multi,
      required this.name,
      required this.side});

  factory LocalGameCharactersModel.fromJson(dynamic json) {
    return LocalGameCharactersModel(
        description: json['description'],
        icon: json['icon'],
        id: json['id'],
        multi: json['multi'],
        name: json['name'],
        side: json['side']);
  }
}
