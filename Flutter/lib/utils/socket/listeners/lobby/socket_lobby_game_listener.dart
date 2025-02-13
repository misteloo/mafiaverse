abstract class SocketLobbyGameListener {
  /// livekit
  void onLivekitToken(dynamic data);

  /// player permissions
  void onPermissionsStatus(dynamic data);

  /// all players status
  void onAllPlayersStatus(dynamic data);

  /// updated specific user
  void onPlayerStatusUpdate(dynamic data);

  /// creator status
  void onCreatorStatus(dynamic data);

  /// players permissions for creator
  void onPlayersPermission(dynamic data);

  /// lobby report
  void onReport(dynamic data);

  /// lobby game event
  void onGameEvent(dynamic data);

  /// room over all
  void onPermissionsOverAllStatus(dynamic data);

  /// creator total messages box when reconnect
  void onCreatorMessageBox(dynamic data);

  /// creator has new message
  void onCreatorNewMessage(dynamic data);

  /// sides
  void onTotalSides(dynamic data);

  /// private speech list
  void onPrivateSpeechList(dynamic data);

  /// private speech end
  void onPrivateSpeechEnd();

  /// live kit private room
  void onLobbyNewSpeechToken(dynamic data);

  /// game end
  void onEndGame();

  /// observer list
  void onObserversList(dynamic data);

  /// game event count
  void onGameEventCount(dynamic data);
}
