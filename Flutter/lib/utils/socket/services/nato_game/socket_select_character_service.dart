import 'package:get/get.dart';
import 'package:mafia/utils/socket/listeners/nato_game/socket_select_character_listener.dart';
import 'package:socket_io_client/socket_io_client.dart';

class SocketSelectCharacterService extends GetxService {
  final Socket _socket = Get.find();
  Future<SocketSelectCharacterService> init() async {
    return this;
  }

  final List<String> _items = [
    'characters',
    'random_character',
    'selected_character',
    'users_turn',
    'your_turn',
    'abandon',
  ];

  void emit(String key, Map? json) {
    _socket.emit(key, json);
  }

  void addListener(SocketSelectCharacterListener listener) async {
    await clearListeners();
    _socket.on('characters', (data) => listener.onCharacters(data));
    _socket.on('random_character', (data) => listener.onRandomCharacter(data));
    _socket.on('users_turn', (data) => listener.onUsersTurnToPick(data));
    _socket.on('your_turn', (data) => listener.onYourTurnToPick());
    _socket.on('abandon', (data) => listener.onAbandon());
  }

  Future<void> clearListeners() async {
    return await Future.forEach(_items, (element) => _socket.off(element));
  }
}
