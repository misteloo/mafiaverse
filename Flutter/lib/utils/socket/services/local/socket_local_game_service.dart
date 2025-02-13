import 'package:get/get.dart';
import 'package:mafia/utils/socket/listeners/local/socket_local_game_listeners.dart';
import 'package:socket_io_client/socket_io_client.dart';

class SocketLocalGameService extends GetxService {
  final Socket _socket = Get.find();
  final List<String> _socketList = [
    'local_game_started',
    'get_deck',
    'users_join',
    'error',
    'prv_deck',
    'cart',
    'users'
  ];
  Future<SocketLocalGameService> init() async {
    return this;
  }

  void emit(String key, dynamic value) {
    _socket.emit(key, value);
  }

  void addListener(SocketLocalGameListener listener) async {
    await _clearSocket();
    _socket.on(
        'local_game_started', (data) => listener.onLocalGameStarted(data));
    _socket.on('get_deck', (data) => listener.onGetDeck(data));
    _socket.on('users_join', (data) => listener.onUsersJoin(data));
    _socket.on('error', (data) => listener.onError(data));
    _socket.on('prv_deck', (data) => listener.onPrvDeck(data));
    _socket.on('cart', (data) => listener.onCard(data));
    _socket.on('users', (data) => listener.onUsers(data));
  }

  Future<void> _clearSocket() async {
    return await Future.forEach(_socketList, (element) => _socket.off(element));
  }
}
