// ignore_for_file: non_constant_identifier_names

import 'package:mafia/models/leader_board/user_leader_board_model.dart';

class LeaderBoardModel {
  String session;
  dynamic session_end;
  List<UserLeaderBoardModel> ranking_list;
  UserLeaderBoardModel user_self;

  LeaderBoardModel(
      {required this.session,
      required this.session_end,
      required this.ranking_list,
      required this.user_self});

  factory LeaderBoardModel.fromJson(dynamic json) {
    return LeaderBoardModel(
        session: json['session'],
        session_end: json['session_end'],
        ranking_list: json['ranking_list']
            .map<UserLeaderBoardModel>(
                (model) => UserLeaderBoardModel.fromJson(model))
            .toList(),
        user_self: UserLeaderBoardModel.fromJson(json['user_self']));
  }
}
