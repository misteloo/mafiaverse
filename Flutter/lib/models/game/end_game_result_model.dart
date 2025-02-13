// ignore_for_file: non_constant_identifier_names

class EndGameResultModel {
  String user_id;
  String user_name;
  String user_image;
  dynamic point;
  String role;
  String side;
  dynamic xp;
  bool winner;
  dynamic gold;
  bool talking = false;

  EndGameResultModel(
      {required this.gold,
      required this.point,
      required this.role,
      required this.side,
      required this.user_id,
      required this.user_image,
      required this.user_name,
      required this.winner,
      required this.xp});

  factory EndGameResultModel.fromJson(Map<String, dynamic> json) {
    return EndGameResultModel(
      gold: json['gold'],
      point: json['point'],
      role: json['role'],
      side: json['side'],
      user_id: json['user_id'],
      user_image: json['user_image'],
      user_name: json['user_name'],
      winner: json['winner'],
      xp: json['xp'],
    );
  }
}
