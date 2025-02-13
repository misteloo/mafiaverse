import 'package:get/get.dart';
import 'package:mafia/utils/socket/listeners/reconnect/socket_reconnect_listeners.dart';
import 'package:socket_io_client/socket_io_client.dart';

class SocketReconnectService extends GetxService {
  final Socket _socket = Get.find();

  Future<SocketReconnectService> init() async {
    return this;
  }

  void emit(String key, dynamic value) {
    _socket.emit(key, value);
  }

  void addListener(SocketReconnectListener listener) {
    _socket.on('reconnect_data', (data) => listener.onReconnect(data));
  }
}
