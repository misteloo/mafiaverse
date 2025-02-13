// ignore_for_file: non_constant_identifier_names

class LobbyGamePlayerPermissionsModel {
  String user_id;
  int user_index;
  bool speech;
  bool listen;
  bool hand_rise;
  bool day_act;
  bool chat;
  bool like_dislike;
  bool challenge;
  bool last_move_card;

  LobbyGamePlayerPermissionsModel({
    required this.challenge,
    required this.chat,
    required this.day_act,
    required this.hand_rise,
    required this.last_move_card,
    required this.like_dislike,
    required this.listen,
    required this.speech,
    required this.user_id,
    required this.user_index,
  });

  factory LobbyGamePlayerPermissionsModel.fromJson(dynamic json) {
    return LobbyGamePlayerPermissionsModel(
        challenge: json['challenge'],
        chat: json['chat'],
        day_act: json['day_act'],
        hand_rise: json['hand_rise'],
        last_move_card: json['last_move_card'],
        like_dislike: json['like_dislike'],
        listen: json['listen'],
        speech: json['speech'],
        user_id: json['user_id'],
        user_index: json['user_index']);
  }
}
