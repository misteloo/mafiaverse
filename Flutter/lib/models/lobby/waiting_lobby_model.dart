// ignore_for_file: non_constant_identifier_names

class LobbyWaitingMessageModel {
  String avatar;
  String name;
  String user_id;
  bool is_system;
  bool is_creator;
  bool self;
  String msg;

  LobbyWaitingMessageModel({
    required this.avatar,
    required this.user_id,
    required this.self,
    required this.is_creator,
    required this.is_system,
    required this.name,
    required this.msg,
  });

  factory LobbyWaitingMessageModel.fromJson(dynamic json, String userId) {
    return LobbyWaitingMessageModel(
        avatar: json['sender']['avatar'],
        user_id: json['sender']['user_id'],
        self: json['sender']['user_id'] == userId,
        is_creator: json['sender']['is_creator'],
        is_system: json['sender']['is_system'],
        name: json['sender']['name'],
        msg: json['msg']);
  }
}
