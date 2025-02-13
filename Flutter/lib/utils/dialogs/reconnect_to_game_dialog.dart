import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/get_utils.dart';

class ReconnectToGameDialog extends StatelessWidget {
  const ReconnectToGameDialog({
    super.key,
    required this.character,
    required this.exitCallback,
    required this.reconnectCallback,
  });
  final Function() exitCallback;
  final Function() reconnectCallback;
  final String? character;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: PopScope(
        canPop: true,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const SizedBox(
              width: double.infinity,
            ),
            FittedBox(
              child: ClipRRect(
                borderRadius: BorderRadius.circular(16),
                child: BackdropFilter(
                  filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
                  child: Column(
                    children: [
                      Container(
                        width: MediaQuery.of(context).size.width * 75 / 100,
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(16),
                          color: Colors.white30,
                        ),
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Visibility(
                              visible: character != null ? true : false,
                              child: Image.asset(
                                'images/$character.png',
                                width: 75,
                                height: 75,
                              ),
                            ),
                            const SizedBox(
                              height: 16,
                            ),
                            Text(
                              'شما بازی ناتموم داری ، میخوای به بازی برگردی ؟',
                              style: context.textTheme.bodyMedium,
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(
                              height: 16,
                            ),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceAround,
                              children: [
                                ElevatedButton(
                                    onPressed: () {
                                      exitCallback();
                                    },
                                    child: Text(
                                      'خروج',
                                      style: context.textTheme.bodyMedium,
                                    )),
                                const SizedBox(
                                  width: 16,
                                ),
                                ElevatedButton(
                                    onPressed: () {
                                      reconnectCallback();
                                    },
                                    child: Text(
                                      'ادامه',
                                      style: context.textTheme.bodyMedium,
                                    )),
                              ],
                            )
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
