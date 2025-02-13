import 'package:livekit_client/livekit_client.dart';
import 'package:mafia/utils/constants/address.dart';

class LiveKitManager {
  Room get createRoom => Room();

  Future<void> connectToRoom(Room room, String token) async {
    await room.disconnect();
    Future.delayed(const Duration(seconds: 1), () async {
      await room.connect(kitUrl, token);
    });
  }

  Future<void> disConnectRoom(Room room) async {
    await room.disconnect();
    return Future<void>.value(null);
  }

  Future<void> dispose(Room room) async {
    await room.dispose();
    return Future<void>.value(null);
  }

  void participantSpeaking(Room room, bool speaking) {
    room.localParticipant?.setMicrophoneEnabled(speaking);
  }
}
