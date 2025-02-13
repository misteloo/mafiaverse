import 'package:get/get.dart';
import 'package:mafia/utils/socket/listeners/lobby/socket_lobby_game_listener.dart';
import 'package:socket_io_client/socket_io_client.dart';

class SocketLobbyGameService extends GetxService {
  final Socket _socket = Get.find();

  Future<SocketLobbyGameService> init() async => this;

  final _items = [
    'livekit_token',
    'permissions_status',
    'all_players_status',
    'player_status_update',
    'creator_status',
    'all_players_status',
    'all_players_permissions',
    'report',
    'game_event',
    'permissions_overall',
    'messages_box',
    'sides_list',
    'private_speech_list',
    'private_speech_end',
    'new_message',
    'lobby_new_speech_token',
    'end_game',
    'observers_list',
    'day_count'
  ];

  void emit(String event, dynamic data) {
    _socket.emit(event, data);
  }

  void addListener(SocketLobbyGameListener listener) async {
    await clearListeners();
    _socket.on('livekit_token', (data) => listener.onLivekitToken(data));
    _socket.on(
        'permissions_status', (data) => listener.onPermissionsStatus(data));
    _socket.on(
        'all_players_status', (data) => listener.onAllPlayersStatus(data));
    _socket.on('creator_status', (data) => listener.onCreatorStatus(data));
    _socket.on(
        'player_status_update', (data) => listener.onPlayerStatusUpdate(data));
    _socket.on('all_players_permissions',
        (data) => listener.onPlayersPermission(data));
    _socket.on('report', (data) => listener.onReport(data));
    _socket.on('game_event', (data) => listener.onGameEvent(data));
    _socket.on('permissions_overall',
        (data) => listener.onPermissionsOverAllStatus(data));
    _socket.on('messages_box', (data) => listener.onCreatorMessageBox(data));
    _socket.on('sides_list', (data) => listener.onTotalSides(data));
    _socket.on(
        'private_speech_list', (data) => listener.onPrivateSpeechList(data));
    _socket.on('private_speech_end', (data) => listener.onPrivateSpeechEnd());
    _socket.on(
      'new_message',
      (data) => listener.onCreatorNewMessage(data),
    );
    _socket.on('lobby_new_speech_token',
        (data) => listener.onLobbyNewSpeechToken(data));

    _socket.on('end_game', (data) => listener.onEndGame());
    _socket.on('observers_list', (data) => listener.onObserversList(data));
    _socket.on('day_count', (data) => listener.onGameEventCount(data));
  }

  Future<void> clearListeners() async {
    return await Future.forEach(_items, (element) => _socket.off(element));
  }
}
