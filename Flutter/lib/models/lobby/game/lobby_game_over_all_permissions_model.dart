// ignore_for_file: non_constant_identifier_names

class LobbyGameOverAllPermissionsModel {
  bool speech;
  bool hand_rise;
  bool like_dislike;
  bool challenge;
  bool chat;

  LobbyGameOverAllPermissionsModel({
    required this.speech,
    required this.hand_rise,
    required this.like_dislike,
    required this.challenge,
    required this.chat,
  });

  factory LobbyGameOverAllPermissionsModel.fromJson(dynamic json) {
    return LobbyGameOverAllPermissionsModel(
        speech: json['speech'],
        hand_rise: json['hand_rise'],
        like_dislike: json['like_dislike'],
        challenge: json['challenge'],
        chat: json['chat']);
  }
}
