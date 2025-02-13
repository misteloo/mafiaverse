// ignore_for_file: non_constant_identifier_names

class SelectCharacterUserModel {
  String? user_name;
  String? user_id;
  String? user_image;

  SelectCharacterUserModel(
      {required this.user_id,
      required this.user_image,
      required this.user_name});

  factory SelectCharacterUserModel.fromJson(Map<String, dynamic> json) {
    return SelectCharacterUserModel(
        user_id: json['user_id'],
        user_image: json['user_image'],
        user_name: json['user_name']);
  }
}
