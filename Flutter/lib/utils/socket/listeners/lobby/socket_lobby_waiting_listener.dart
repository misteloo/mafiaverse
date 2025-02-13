abstract class SocketWaitingLobbyListener {
  /// new message to lobby
  void onWaitingLobbyMessage(dynamic data);

  /// lobby detail
  void onLobbyDetail(dynamic data);

  /// update users
  void onLobbyUpdateUsers(dynamic data);

  /// kicked player
  void onKickedPlayer();

  /// lobby removed by moderator
  void onLobbyRemoved();

  /// socket error
  void onError(dynamic data);

  /// game created
  void onGameStarted();

  /// players speaking
  void onLobbySpeechStatus(dynamic data);
}
