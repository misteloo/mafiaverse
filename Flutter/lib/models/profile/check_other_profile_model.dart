// ignore_for_file: non_constant_identifier_names

class CheckOtherProfileModel {
  CheckOtherProfilePoint point;
  CheckOtherProfileGameResult gameResult;
  CheckOtherProfileModerator moderatorStatus;
  CheckOtherProfileModel(
      {required this.gameResult,
      required this.moderatorStatus,
      required this.point});

  factory CheckOtherProfileModel.fromJson(dynamic json) {
    return CheckOtherProfileModel(
        gameResult: CheckOtherProfileGameResult.fromJson(json['games_result']),
        moderatorStatus: CheckOtherProfileModerator.fromJson(json['moderator']),
        point: CheckOtherProfilePoint.fromJson(json['points']));
  }
}

class CheckOtherProfilePoint {
  int win;
  int lose;

  CheckOtherProfilePoint({required this.win, required this.lose});

  factory CheckOtherProfilePoint.fromJson(dynamic json) {
    return CheckOtherProfilePoint(win: json['win'], lose: json['lose']);
  }
}

class CheckOtherProfileGameResult {
  int game_as_mafia;
  int win_as_mafia;
  int win_as_citizen;
  int game_as_citizen;

  CheckOtherProfileGameResult(
      {required this.game_as_citizen,
      required this.game_as_mafia,
      required this.win_as_citizen,
      required this.win_as_mafia});

  factory CheckOtherProfileGameResult.fromJson(dynamic json) {
    return CheckOtherProfileGameResult(
        game_as_citizen: json['game_as_citizen'],
        game_as_mafia: json['game_as_mafia'],
        win_as_citizen: json['win_as_citizen'],
        win_as_mafia: json['win_as_mafia']);
  }
}

class CheckOtherProfileModerator {
  dynamic cnt;
  dynamic avg;

  CheckOtherProfileModerator({required this.avg, required this.cnt});

  factory CheckOtherProfileModerator.fromJson(dynamic json) {
    return CheckOtherProfileModerator(avg: json['avg'], cnt: json['cnt']);
  }
}
