// ignore_for_file: non_constant_identifier_names

class UserLeaderBoardModel {
  String name;
  String avatar;
  String user_id;
  dynamic session_rank;
  dynamic win;
  dynamic lose;
  int rate;
  dynamic prize;

  UserLeaderBoardModel(
      {required this.avatar,
      required this.lose,
      required this.name,
      required this.prize,
      required this.rate,
      required this.session_rank,
      required this.user_id,
      required this.win});

  factory UserLeaderBoardModel.fromJson(dynamic json) {
    return UserLeaderBoardModel(
        avatar: json['avatar']['avatar'],
        lose: json['lose'],
        name: json['idenity']['name'],
        prize: json['prize'],
        rate: json['rate'],
        session_rank: json['session_rank'],
        user_id: json['user_id'],
        win: json['win']);
  }
}
