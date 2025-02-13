import 'package:get/get.dart';
import 'package:mafia/utils/socket/listeners/lobby/socket_lobby_waiting_listener.dart';
import 'package:socket_io_client/socket_io_client.dart';

class SocketWaitingLobbyService extends GetxService {
  Future<SocketWaitingLobbyService> init() async => this;

  // injection
  final Socket _socket = Get.find();

  final List<String> emits = [
    'waiting_lobby_new_message',
    'lobby_detail',
    'update_lobby_users',
    'kicked_from_lobby',
    'lobby_removed',
    'error',
    'custom_game_created',
    'lobby_speech_status'
  ];
  void emit({required dynamic event, required dynamic data}) {
    _socket.emit(event, data);
  }

  Future<void> clearSockets() async {
    return await Future.forEach(emits, (element) => _socket.off(element));
  }

  void addListener(SocketWaitingLobbyListener listener) async {
    await clearSockets();
    _socket.on('waiting_lobby_new_message',
        (data) => listener.onWaitingLobbyMessage(data));
    _socket.on('lobby_detail', (data) => listener.onLobbyDetail(data));
    _socket.on(
        'update_lobby_users', (data) => listener.onLobbyUpdateUsers(data));
    _socket.on('kicked_from_lobby', (data) => listener.onKickedPlayer());
    _socket.on('lobby_removed', (data) => listener.onLobbyRemoved());
    _socket.on('error', (data) => listener.onError(data));
    _socket.on('custom_game_created', (data) => listener.onGameStarted());
    _socket.on(
        'lobby_speech_status', (data) => listener.onLobbySpeechStatus(data));
  }
}
