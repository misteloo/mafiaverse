class NatoNightActModel {
  String username;
  String userId;
  String userImage;
  bool hasGun;
  String? gunKind;
  bool natoAct = false;
  String? guessCharacter;

  NatoNightActModel(
      {required this.username,
      required this.userId,
      required this.userImage,
      this.hasGun = false,
      this.gunKind,
      this.natoAct = false,
      this.guessCharacter});
}
