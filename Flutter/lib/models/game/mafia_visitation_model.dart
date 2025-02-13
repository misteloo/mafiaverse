import 'package:mafia/utils/constants/game_character.dart';

class MafiaVisitationModel {
  String userId;
  String characterName;
  String characterImage;

  MafiaVisitationModel(
      {required this.userId,
      required this.characterImage,
      required this.characterName});

  factory MafiaVisitationModel.fromJson(
      Map<String, dynamic> json, String scenario) {
    var role = GameCharacter().getCharacter(scenario, json['role']);
    return MafiaVisitationModel(
        userId: json['user_id'],
        characterImage: role['image']!,
        characterName: role['name']!);
  }
}
