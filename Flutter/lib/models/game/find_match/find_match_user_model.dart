// ignore_for_file: non_constant_identifier_names

class FindMatchUserModel {
  String user_image;
  String user_id;

  FindMatchUserModel({required this.user_id, required this.user_image});

  factory FindMatchUserModel.fromJson(Map<String, dynamic> json) {
    return FindMatchUserModel(
      user_id: json['user_id'],
      user_image: json['user_image'],
    );
  }
}
