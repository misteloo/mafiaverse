// ignore_for_file: non_constant_identifier_names

class InGameUserIdentityModel {
  String user_name;
  String user_id;
  int index;
  String user_image;

  InGameUserIdentityModel(
      {required this.user_id,
      required this.user_image,
      required this.index,
      required this.user_name});

  factory InGameUserIdentityModel.fromJson(Map<String, dynamic> json) {
    return InGameUserIdentityModel(
        user_id: json['user_id'],
        user_image: json['user_image'],
        index: json['index'],
        user_name: json['user_name']);
  }
}
