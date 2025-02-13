class LobbyWaitingSpeechModel {
  String user;
  bool speech;

  LobbyWaitingSpeechModel({required this.speech, required this.user});

  factory LobbyWaitingSpeechModel.fromJson(dynamic json) {
    return LobbyWaitingSpeechModel(speech: json['speech'], user: json['user']);
  }
}
