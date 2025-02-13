import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/screens/game/reconnect/reconnect_screen_controller.dart';

class ReconnectScreen extends GetView<ReconnectScreenController> {
  const ReconnectScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    controller.reconnect();
    return SafeArea(
      child: Scaffold(
        body: PopScope(
          canPop: false,
          child: GestureDetector(
            onHorizontalDragUpdate: (details) {
              if (details.primaryDelta! > 10) {
                return;
              }
            },
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    'دریافت اطلاعات بازی',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  LoadingAnimationWidget.prograssiveDots(
                      color: Colors.white, size: 26)
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
