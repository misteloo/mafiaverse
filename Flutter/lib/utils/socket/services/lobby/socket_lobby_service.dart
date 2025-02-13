import 'package:get/get.dart';
import 'package:mafia/utils/socket/listeners/lobby/socket_lobby_listener.dart';
import 'package:socket_io_client/socket_io_client.dart';

class SocketLobbyService extends GetxService {
  Future<SocketLobbyService> init() async => this;

  final Socket _socket = Get.find();

  final _items = ['lobby_join_result', 'err'];

  void emit(String event, dynamic data) {
    _socket.emit(event, data);
  }

  Future<void> removeListeners() async {
    return await Future.forEach(_items, (element) => _socket.off(element));
  }

  void addListener(SocketLobbyListener listener) async {
    await removeListeners();
    _socket.on('err', (data) => listener.onError(data));
    _socket.on('lobby_join_result', (data) => listener.onLobbyJoinResult(data));
  }
}
