import 'package:get/get.dart';
import 'package:socket_io_client/socket_io_client.dart';

import '../constants/address.dart';

class SocketService extends GetxService {
  Future<Socket> init() async {
    return io(
        appBaseUrl,
        OptionBuilder().setTransports(['websocket']) // for Flutter or Dart VM
            .build());
  }
}
