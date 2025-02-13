// ignore_for_file: non_constant_identifier_names

class SelectCharacterCharacterModel {
  String? name;
  String? selected_by;
  bool? selected;
  int? id;

  SelectCharacterCharacterModel(
      {required this.id,
      required this.name,
      required this.selected,
      required this.selected_by});

  factory SelectCharacterCharacterModel.fromJson(Map<String, dynamic> json) {
    return SelectCharacterCharacterModel(
      id: json['id'],
      name: json['name'],
      selected: json['selected'],
      selected_by: json['selected_by'],
    );
  }
}
