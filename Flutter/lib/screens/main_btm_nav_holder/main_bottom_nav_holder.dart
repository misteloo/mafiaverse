import 'package:flutter/material.dart';
import 'package:flutter_snake_navigationbar/flutter_snake_navigationbar.dart';
import 'package:get/get.dart';
import 'package:mafia/screens/main_btm_nav_holder/main_bottom_nav_holder_controller.dart';

class MainBottomNavHolderScreen extends GetView<MainBottomNavHolderController> {
  const MainBottomNavHolderScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        bottomNavigationBar: GetBuilder<MainBottomNavHolderController>(
          builder: (controller) => SnakeNavigationBar.color(
            behaviour: SnakeBarBehaviour.floating,
            snakeShape: SnakeShape.circle,
            snakeViewColor: Colors.cyan,
            padding: const EdgeInsets.all(6),
            backgroundColor: const Color.fromRGBO(102, 102, 102, 0.5),
            shape: ContinuousRectangleBorder(
                borderRadius: BorderRadius.circular(40)),
            onTap: (index) {
              controller.bottomNavIndex = index;
              controller.update();
            },
            currentIndex: controller.bottomNavIndex,
            selectedItemColor: Colors.white,
            unselectedItemColor: Colors.grey,
            items: [
              const BottomNavigationBarItem(
                  icon: Icon(Icons.shopping_bag_rounded)),
              const BottomNavigationBarItem(
                  icon: Icon(Icons.bar_chart_rounded)),
              BottomNavigationBarItem(
                icon: Image.asset(
                  'images/nato.png',
                  width: 24,
                  height: 24,
                  color: controller.bottomNavIndex != 2 ? Colors.grey : null,
                ),
              ),
              const BottomNavigationBarItem(icon: Icon(Icons.token_rounded)),
              const BottomNavigationBarItem(
                  icon: Icon(Icons.account_box_rounded)),
            ],
          ),
        ),
        body: GetBuilder<MainBottomNavHolderController>(
          builder: (controller) =>
              controller.loadPage(controller.bottomNavIndex),
        ),
      ),
    );
  }
}
