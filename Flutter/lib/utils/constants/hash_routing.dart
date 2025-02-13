import 'dart:math';

import 'package:get/get.dart';

class HashRouting extends GetxService {
  late Map<String, String> routs;
  static const _chars =
      'AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890';
  final Random _rnd = Random();
  String getRandomString(int length) => String.fromCharCodes(Iterable.generate(
      length, (_) => _chars.codeUnitAt(_rnd.nextInt(_chars.length))));

  Future<HashRouting> init() async {
    routs = {
      'splash': '/splash',
      'login': '/login',
      'signup': '/signup',
      'confirm_code': '/confirm_code',
      'main_btm_nav_holder': '/main_btm_nav_holder',
      'home': '/home',
      'profile': '/profile',
      'shop': '/shop',
      'leader_board': '/leader_board',
      'find_match': '/find_match',
      'select_character': '/select_character',
      'nato_game': '/nato_game',
      'night_act': '/night_act',
      'reconnect': '/reconnect',
      'end_game_result': '/end_game_result',
      'learn': '/learn',
      'edit_profile': '/edit_profile',
      'local_game': '/local_game',
      'select_deck': '/select_deck',
      'unread_message': '/unread_message',
      'report_bug': '/report_bug',
      'lobby': '/lobby',
      'create_lobby': '/create_lobby',
      'lobby_waiting_room': '/lobby_waiting_room',
      'lobby_game': '/lobby_game',
    };
    return this;
  }

  String getRoute(String key) => routs[key]!;

  String reverseRoute(String value) =>
      routs.keys.firstWhere((element) => routs[element] == value);
}
