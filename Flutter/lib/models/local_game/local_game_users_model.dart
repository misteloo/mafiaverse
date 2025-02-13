// ignore_for_file: non_constant_identifier_names

class LocalGameUserModel {
  String user_id;
  String name;
  dynamic playerCard;
  dynamic avatar;
  LocalGameUserModel(
      {required this.name,
      required this.playerCard,
      required this.user_id,
      required this.avatar});

  factory LocalGameUserModel.fromJson(dynamic json) {
    return LocalGameUserModel(
        name: json['name'],
        playerCard: json['cart'],
        user_id: json['user_id'],
        avatar: json['avatar']);
  }
}
