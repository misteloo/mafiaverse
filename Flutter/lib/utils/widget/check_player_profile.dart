import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mafia/models/profile/check_other_profile_model.dart';

class CheckPlayerProfile extends StatelessWidget {
  const CheckPlayerProfile({super.key, required this.profile});
  final CheckOtherProfileModel profile;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return ClipRRect(
      borderRadius: BorderRadius.circular(16),
      child: BackdropFilter(
        filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
        child: Container(
          color: const Color.fromRGBO(102, 102, 102, 0.5),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const SizedBox(
                    height: 16,
                    width: double.infinity,
                  ),
                  Text(
                    'اطلاعات پروفایل',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 16,
                    width: double.infinity,
                  ),
                  Text(
                    'آمار کلی',
                    style: context.textTheme.titleMedium,
                  ),
                  const SizedBox(
                    height: 8,
                    width: double.infinity,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      Expanded(
                        child: Column(
                          children: [
                            Text(
                              'تعداد برد',
                              style: context.textTheme.titleLarge,
                            ),
                            const SizedBox(
                              height: 8,
                            ),
                            Text(
                              profile.point.win.toString(),
                              style: TextStyle(
                                  fontFamily: 'shabnam',
                                  fontSize: 20.sp,
                                  color: Colors.white),
                            )
                          ],
                        ),
                      ),
                      Expanded(
                        child: Column(
                          children: [
                            Text(
                              'تعداد باخت',
                              style: context.textTheme.titleLarge,
                            ),
                            const SizedBox(
                              height: 8,
                            ),
                            Text(
                              profile.point.lose.toString(),
                              style: TextStyle(
                                  fontFamily: 'shabnam',
                                  fontSize: 20.sp,
                                  color: Colors.white),
                            )
                          ],
                        ),
                      )
                    ],
                  ),
                  const Divider(),
                  Text(
                    'آمار بازی',
                    style: context.textTheme.titleMedium,
                  ),
                  const SizedBox(
                    height: 8,
                    width: double.infinity,
                  ),
                  Row(
                    children: [
                      Expanded(
                          child: Center(
                        child: Text(
                          'ساید مافیا',
                          style: context.textTheme.titleMedium,
                        ),
                      )),
                      Expanded(
                          child: Center(
                        child: Text(
                          'ساید شهر',
                          style: context.textTheme.titleMedium,
                        ),
                      )),
                    ],
                  ),
                  const SizedBox(
                    height: 8,
                    width: double.infinity,
                  ),
                  Row(
                    children: [
                      Expanded(
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Text(
                              profile.gameResult.game_as_mafia.toString(),
                              style: context.textTheme.bodyLarge,
                            ),
                            Text(
                              'مجموع بازی',
                              style: context.textTheme.titleMedium,
                            ),
                          ],
                        ),
                      ),
                      Expanded(
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Text(
                              profile.gameResult.game_as_citizen.toString(),
                              style: context.textTheme.bodyLarge,
                            ),
                            Text(
                              'مجموع بازی',
                              style: context.textTheme.titleMedium,
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(
                    height: 8,
                    width: double.infinity,
                  ),
                  Row(
                    children: [
                      Expanded(
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Text(
                              profile.gameResult.win_as_mafia.toString(),
                              style: context.textTheme.bodyLarge,
                            ),
                            Text(
                              'تعداد برد',
                              style: context.textTheme.titleMedium,
                            ),
                          ],
                        ),
                      ),
                      Expanded(
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Text(
                              profile.gameResult.win_as_citizen.toString(),
                              style: context.textTheme.bodyLarge,
                            ),
                            Text(
                              'تعداد برد',
                              style: context.textTheme.titleMedium,
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  const Divider(),
                  const SizedBox(
                    height: 8,
                  ),
                  Text(
                    'آمار گردانندگی',
                    style: context.textTheme.titleMedium,
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      Expanded(
                        child: Column(
                          children: [
                            Text(
                              'میانگین امتیاز',
                              style: context.textTheme.titleMedium,
                            ),
                            const SizedBox(
                              height: 8,
                            ),
                            Text(
                              double.parse(
                                      (profile.moderatorStatus.avg).toString())
                                  .toStringAsFixed(1),
                              style: TextStyle(
                                  fontFamily: 'shabnam',
                                  fontSize: 20.sp,
                                  color: Colors.white),
                            )
                          ],
                        ),
                      ),
                      Expanded(
                        child: Column(
                          children: [
                            Text(
                              'تعداد بازی',
                              style: context.textTheme.titleMedium,
                            ),
                            const SizedBox(
                              height: 8,
                            ),
                            Text(
                              profile.moderatorStatus.cnt.toString(),
                              style: TextStyle(
                                  fontFamily: 'shabnam',
                                  fontSize: 20.sp,
                                  color: Colors.white),
                            )
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(
                    height: 16,
                  )
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
