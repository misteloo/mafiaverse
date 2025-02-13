import 'package:flutter/widgets.dart';
import 'package:get/get.dart';
import 'package:mafia/screens/lobby/lobby_screen.dart';
import 'package:mafia/screens/home/home_screen.dart';
import 'package:mafia/screens/leader_board/leader_board_screen.dart';
import 'package:mafia/screens/profile/profile_screen.dart';
import 'package:mafia/screens/shop/shop_screen.dart';

class MainBottomNavHolderController extends GetxController {
  // instances
  int bottomNavIndex = 2;

  final List<Widget> _pages = [
    const ShopScreen(),
    const LeaderBoardScreen(),
    const HomeScreen(),
    const LobbyScreen(),
    const ProfileScreen()
  ];

  Widget loadPage(int index) {
    return _pages[index];
  }
}
