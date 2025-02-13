import 'package:mafia/models/game/in_game_user_identity_model.dart';

class InGameUserModel {
  InGameUserIdentityModel userIdentity;
  bool like;
  bool disLike;
  bool speech;
  bool alive;
  bool challengeRequest;
  bool acceptChallengeRequest;
  bool canTakeChallenge;
  bool connected;
  bool micOpen;
  bool shot;
  bool hasGun;
  bool targeted;
  bool handRaise;
  bool targetCoverHandRaise;
  bool acceptHandRaise;
  bool vote;
  bool availableHandShake;
  bool turnToHandShake;
  InGameUserModel? handShakeTo;
  String targetedType;
  String? mafiaCharacterImg;
  String speechType;

  InGameUserModel(
      {this.like = false,
      this.disLike = false,
      this.alive = true,
      this.speech = false,
      this.connected = true,
      this.challengeRequest = false,
      this.acceptChallengeRequest = false,
      this.canTakeChallenge = false,
      this.micOpen = false,
      this.shot = false,
      this.hasGun = false,
      this.targeted = false,
      this.handRaise = false,
      this.targetCoverHandRaise = false,
      this.acceptHandRaise = false,
      this.vote = false,
      this.availableHandShake = false,
      this.turnToHandShake = false,
      this.targetedType = 'not',
      this.speechType = 'none',
      required this.userIdentity});
}
