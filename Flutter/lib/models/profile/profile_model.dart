// ignore_for_file: non_constant_identifier_names

class ProfileModel {
  String name;
  String phone;
  String avatar;
  dynamic vip;
  dynamic vip_until;
  dynamic win;
  dynamic lose;
  dynamic abdon;
  dynamic com_report;
  dynamic role_report;
  dynamic game_as_mafia;
  dynamic win_as_mafia;
  dynamic game_as_citizen;
  dynamic win_as_citizen;
  dynamic xp;
  dynamic rank;
  dynamic daily_rank;
  dynamic weekly_rank;
  dynamic session_rank;

  ProfileModel(
      {required this.name,
      required this.vip,
      required this.vip_until,
      required this.phone,
      required this.avatar,
      required this.win,
      required this.lose,
      required this.abdon,
      required this.com_report,
      required this.role_report,
      required this.game_as_mafia,
      required this.win_as_mafia,
      required this.game_as_citizen,
      required this.win_as_citizen,
      required this.xp,
      required this.rank,
      required this.daily_rank,
      required this.weekly_rank,
      required this.session_rank});

  factory ProfileModel.fromJson(dynamic json) {
    return ProfileModel(
        name: json['idenity']['name'],
        vip: json['vip'],
        vip_until: json['vip_until'],
        phone: json['idenity']['phone'],
        avatar: json['avatar']['avatar'],
        win: json['points']['win'],
        lose: json['points']['lose'],
        abdon: json['points']['abdon'],
        com_report: json['points']['com_report'],
        role_report: json['points']['role_report'],
        game_as_mafia: json['games_result']['game_as_mafia'],
        win_as_mafia: json['games_result']['win_as_mafia'],
        game_as_citizen: json['games_result']['game_as_citizen'],
        win_as_citizen: json['games_result']['win_as_citizen'],
        xp: json['ranking']['xp'],
        rank: json['ranking']['rank'],
        daily_rank: json['session_rank']['day'],
        weekly_rank: json['session_rank']['week'],
        session_rank: json['session_rank']['session']);
  }
}
