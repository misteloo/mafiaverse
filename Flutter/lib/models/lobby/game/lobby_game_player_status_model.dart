// ignore_for_file: non_constant_identifier_names

class LobbyGamePlayerStatusModel {
  bool alive;
  bool connected;
  bool speech;
  bool hand_rise;
  bool day_act;
  bool like;
  bool dislike;
  bool challenge;
  bool challenge_accepted;
  bool private;
  LobbyGamePlayerStatusModel(
      {required this.alive,
      required this.challenge,
      required this.connected,
      required this.day_act,
      required this.dislike,
      required this.hand_rise,
      required this.like,
      required this.speech,
      required this.challenge_accepted,
      required this.private});

  factory LobbyGamePlayerStatusModel.fromJson(dynamic json) {
    return LobbyGamePlayerStatusModel(
        alive: json['alive'],
        challenge: json['challenge'],
        connected: json['connected'],
        day_act: json['day_act'],
        dislike: json['dislike'],
        hand_rise: json['hand_rise'],
        like: json['like'],
        speech: json['speech'],
        challenge_accepted: json['challenge_accepted'],
        private: json['private']);
  }
}
