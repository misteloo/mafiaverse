abstract class SocketLocalGameListener {
  void onLocalGameStarted(dynamic data);
  void onGetDeck(dynamic data);
  void onUsersJoin(dynamic data);
  void onError(dynamic data);
  void onPrvDeck(dynamic data);
  void onCard(dynamic data);
  void onUsers(dynamic data);
}
