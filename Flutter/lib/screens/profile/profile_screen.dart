import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:lottie/lottie.dart';
import 'package:mafia/screens/profile/profile_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';
import 'package:share_plus/share_plus.dart';
import 'package:url_launcher/url_launcher.dart';

class ProfileScreen extends GetView<ProfileScreenController> {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    controller.loadProfile();
    return Scaffold(
      appBar: AppBar(
        iconTheme: const IconThemeData(color: Colors.white),
        title: Text(
          'پروفایل',
          style: context.textTheme.bodyLarge,
        ),
        centerTitle: true,
      ),
      endDrawer: Drawer(
        backgroundColor: Colors.transparent,
        child: ClipRRect(
          child: BackdropFilter(
            filter: ImageFilter.blur(
              sigmaX: 5,
              sigmaY: 5,
            ),
            child: Container(
              color: const Color.fromRGBO(102, 102, 102, 0.5),
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  SizedBox(
                    height: MediaQuery.of(context).size.height * 20 / 100,
                    child: Center(
                      child: LottieBuilder.asset(
                        'assets/night.json',
                        width: 100,
                        height: 100,
                      ),
                    ),
                  ),
                  InkWell(
                    onTap: () async {
                      await Share.shareWithResult(controller.shareContent());
                    },
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        Text(
                          'معرفی به دوستان',
                          style: context.textTheme.bodyMedium,
                        ),
                        const SizedBox(
                          width: 10,
                        ),
                        const Icon(
                          Icons.people,
                          color: Colors.white,
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  InkWell(
                    onTap: () async {
                      var uri = Uri(scheme: 'https', host: webSite);
                      await launchUrl(uri,
                          mode: LaunchMode.externalApplication);
                    },
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        Text(
                          'وب سایت',
                          style: context.textTheme.bodyMedium,
                        ),
                        const SizedBox(
                          width: 10,
                        ),
                        const Icon(
                          Icons.link_rounded,
                          color: Colors.white,
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  InkWell(
                    onTap: () async {
                      await launchUrl(Uri.parse('https://$telegramChannel'),
                          mode: LaunchMode.externalApplication);
                    },
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        Text(
                          'کانال تلگرام',
                          style: context.textTheme.bodyMedium,
                        ),
                        const SizedBox(
                          width: 10,
                        ),
                        const Icon(
                          Icons.telegram_rounded,
                          color: Colors.white,
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  InkWell(
                    onTap: () {
                      controller.navigateToReportBug();
                    },
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        Text(
                          'گزارش خطا',
                          style: context.textTheme.bodyMedium,
                        ),
                        const SizedBox(
                          width: 10,
                        ),
                        const Icon(
                          Icons.bug_report_rounded,
                          color: Colors.white,
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  InkWell(
                    onTap: () async {
                      controller.exit();
                    },
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        Text(
                          'خروج از برنامه',
                          style: context.textTheme.bodyMedium,
                        ),
                        const SizedBox(
                          width: 10,
                        ),
                        const Icon(
                          Icons.exit_to_app_rounded,
                          color: Colors.white,
                        ),
                      ],
                    ),
                  ),
                  const Spacer(),
                  Obx(() {
                    return Directionality(
                      textDirection: TextDirection.rtl,
                      child: Text('نسخه ${controller.appVersionName}'),
                    );
                  }),
                  const SizedBox(
                    height: 16,
                  )
                ],
              ),
            ),
          ),
        ),
      ),
      drawerDragStartBehavior: DragStartBehavior.down,
      body: GetBuilder<ProfileScreenController>(
        id: 'userData',
        builder: (_) => controller.profile == null
            ? Center(
                child: LoadingAnimationWidget.prograssiveDots(
                    color: Colors.white, size: 26),
              )
            : SingleChildScrollView(
                child: AnimationLimiter(
                  child: Column(
                    children: AnimationConfiguration.toStaggeredList(
                      duration: const Duration(milliseconds: 500),
                      childAnimationBuilder: (widget) => SlideAnimation(
                        horizontalOffset: 100,
                        child: FadeInAnimation(child: widget),
                      ),
                      children: [
                        _accountInfo(context),
                        const SizedBox(
                          height: 8,
                        ),
                        _vip(context),
                        const SizedBox(
                          height: 16,
                        ),
                        Divider(
                          height: 2,
                          color: Colors.grey.shade700,
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Image.asset(
                              'images/img_chart.png',
                              width: 30,
                              height: 30,
                            ),
                            const SizedBox(
                              width: 8,
                            ),
                            Text(
                              'رتبه بندی',
                              style: context.textTheme.titleMedium,
                            ),
                          ],
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        _ranking(context),
                        const SizedBox(
                          height: 8,
                        ),
                        Divider(
                          height: 2,
                          color: Colors.grey.shade700,
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Image.asset(
                              'images/img_statistic.webp',
                              width: 30,
                              height: 30,
                            ),
                            const SizedBox(
                              width: 8,
                            ),
                            Text(
                              'آمار و عملکرد',
                              style: context.textTheme.titleMedium,
                            ),
                          ],
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        _statistic(context)
                      ],
                    ),
                  ),
                ),
              ),
      ),
    );
  }

  Widget _vip(BuildContext context) {
    return Column(
      children: [
        Divider(
          height: 2,
          color: Colors.grey.shade700,
        ),
        const SizedBox(
          height: 8,
        ),
        Text(
          'اشتراک ویژه',
          style: context.textTheme.titleMedium,
        ),
        Image.asset(
          'images/vip.webp',
          width: 150,
          height: 150,
          color: controller.profile!.vip == true
              ? Colors.transparent
              : Colors.grey,
        ),
        const SizedBox(
          height: 16,
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            Text(
              '${controller.profile?.vip_until}    روز اشتراک باقی مونده   ',
              style: context.textTheme.bodyMedium,
            ),
          ],
        )
      ],
    );
  }

  Widget _ranking(BuildContext context) {
    return Column(
      children: [
        Row(
          children: [
            Expanded(
                child: Center(
                    child: Text(
              'فصلی',
              style: context.textTheme.bodyMedium,
            ))),
            Expanded(
                child: Center(
                    child: Text(
              'هفتگی',
              style: context.textTheme.bodyMedium,
            ))),
            Expanded(
                child: Center(
                    child: Text(
              'روزانه',
              style: context.textTheme.bodyMedium,
            ))),
          ],
        ),
        const SizedBox(
          height: 10,
        ),
        Row(
          children: [
            Expanded(
                child: Center(
                    child: Text('${controller.profile?.session_rank}',
                        style: context.textTheme.bodyLarge))),
            Expanded(
                child: Center(
                    child: Text('${controller.profile?.weekly_rank}',
                        style: context.textTheme.bodyLarge))),
            Expanded(
                child: Center(
                    child: Text('${controller.profile?.daily_rank}',
                        style: context.textTheme.bodyLarge)))
          ],
        )
      ],
    );
  }

  Widget _statistic(BuildContext context) {
    return Column(
      children: [
        Column(
          children: [
            Row(
              mainAxisSize: MainAxisSize.max,
              children: [
                Expanded(
                  child: Center(
                    child: Text(
                      'درصد برد کلی',
                      style: context.textTheme.bodyMedium,
                    ),
                  ),
                ),
                Expanded(
                  child: Center(
                    child: Text(
                      'مجموع بازی ها',
                      style: context.textTheme.bodyMedium,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: 16,
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  child: Center(
                    child: Stack(
                      alignment: Alignment.center,
                      children: [
                        SizedBox(
                          width: 60,
                          height: 60,
                          child: CircularProgressIndicator(
                            value: double.parse(controller.totalWinPercent()) /
                                100.0,
                            color: Colors.cyan,
                            strokeCap: StrokeCap.round,
                          ),
                        ),
                        Center(
                          child: Text(
                            controller.totalWinPercent(),
                            style: context.textTheme.bodyMedium,
                          ),
                        )
                      ],
                    ),
                  ),
                ),
                Expanded(
                  child: Center(
                    child: Text(
                      '${controller.profile?.game_as_citizen + controller.profile?.game_as_mafia}',
                      style: context.textTheme.bodyLarge,
                    ),
                  ),
                ),
              ],
            )
          ],
        ),
        const SizedBox(
          height: 16,
        ),
        Text(
          'آمار ساید',
          style: context.textTheme.titleMedium,
        ),
        const SizedBox(
          height: 16,
        ),
        Column(
          children: [
            Row(
              children: [
                Expanded(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Image.asset(
                        'images/mafia_hat.png',
                        width: 30,
                        height: 30,
                      ),
                      const SizedBox(
                        width: 8,
                      ),
                      Text(
                        'ساید مافیا',
                        style: context.textTheme.bodyMedium,
                      )
                    ],
                  ),
                ),
                Expanded(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Image.asset(
                        'images/citizen_hat.png',
                        width: 30,
                        height: 30,
                      ),
                      const SizedBox(
                        width: 8,
                      ),
                      Text(
                        'ساید شهر',
                        style: context.textTheme.bodyMedium,
                      )
                    ],
                  ),
                )
              ],
            ),
            const SizedBox(
              height: 10,
            ),
            Row(
              children: [
                Expanded(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        '${controller.profile?.game_as_mafia}',
                        style: context.textTheme.bodyLarge,
                      ),
                      const SizedBox(
                        width: 10,
                      ),
                      Directionality(
                        textDirection: TextDirection.rtl,
                        child: Text(
                          'تعداد بازی :',
                          style: context.textTheme.titleMedium,
                        ),
                      )
                    ],
                  ),
                ),
                Expanded(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        '${controller.profile?.game_as_citizen}',
                        style: context.textTheme.bodyLarge,
                      ),
                      const SizedBox(
                        width: 10,
                      ),
                      Directionality(
                        textDirection: TextDirection.rtl,
                        child: Text(
                          'تعداد بازی :',
                          style: context.textTheme.titleMedium,
                        ),
                      )
                    ],
                  ),
                )
              ],
            ),
            const SizedBox(
              height: 10,
            ),
            Row(
              children: [
                Expanded(
                  child: Center(
                    child: Text(
                      'درصد برد',
                      style: context.textTheme.titleMedium,
                    ),
                  ),
                ),
                Expanded(
                  child: Center(
                    child: Text(
                      'درصد برد',
                      style: context.textTheme.titleMedium,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: 10,
            ),
            Row(
              children: [
                Expanded(
                  child: Center(
                    child: Stack(
                      alignment: Alignment.center,
                      children: [
                        SizedBox(
                          width: 60,
                          height: 60,
                          child: CircularProgressIndicator(
                            color: Colors.cyan,
                            strokeCap: StrokeCap.round,
                            value: double.parse(
                                  controller.mafiaSideWinRate(),
                                ) /
                                100.0,
                          ),
                        ),
                        Text(
                          controller.mafiaSideWinRate(),
                          style: context.textTheme.bodyMedium,
                        )
                      ],
                    ),
                  ),
                ),
                Expanded(
                  child: Center(
                    child: Stack(
                      alignment: Alignment.center,
                      children: [
                        SizedBox(
                          width: 60,
                          height: 60,
                          child: CircularProgressIndicator(
                            color: Colors.cyan,
                            strokeCap: StrokeCap.round,
                            value: double.parse(
                                  controller.citizenSideWinRate(),
                                ) /
                                100.0,
                          ),
                        ),
                        Text(
                          controller.citizenSideWinRate(),
                          style: context.textTheme.bodyMedium,
                        )
                      ],
                    ),
                  ),
                )
              ],
            )
          ],
        )
      ],
    );
  }

  Widget _accountInfo(BuildContext context) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            Expanded(
              child: Center(
                child: CachedNetworkImage(
                  imageUrl: '$appBaseUrl/${controller.profile?.avatar}',
                  imageBuilder: (context, imageProvider) => Container(
                    width: MediaQuery.of(context).size.width * 20 / 100,
                    height: MediaQuery.of(context).size.width * 20 / 100,
                    decoration: ShapeDecoration(
                      shape: ContinuousRectangleBorder(
                        borderRadius: BorderRadius.circular(
                            MediaQuery.of(context).size.width * 13 / 100),
                        side: const BorderSide(color: Colors.grey),
                      ),
                      image: DecorationImage(
                          image: imageProvider, fit: BoxFit.cover),
                    ),
                  ),
                  placeholder: (context, url) => Container(
                    width: MediaQuery.of(context).size.width * 20 / 100,
                    height: MediaQuery.of(context).size.width * 20 / 100,
                    decoration: ShapeDecoration(
                        shape: ContinuousRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              MediaQuery.of(context).size.width * 13 / 100),
                          side: const BorderSide(color: Colors.grey),
                        ),
                        color: Colors.grey),
                  ),
                ),
              ),
            ),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'نام کاربری',
                    style: context.textTheme.titleMedium,
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Text(
                    // 'mmd',
                    '${controller.profile?.name}',
                    style: context.textTheme.bodyLarge,
                    overflow: TextOverflow.ellipsis,
                  )
                ],
              ),
            ),
            Expanded(
              child: Column(
                children: [
                  Text(
                    'شماره تماس',
                    style: context.textTheme.titleMedium,
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Text(
                    '${controller.profile?.phone}',
                    style: context.textTheme.bodyLarge,
                  )
                ],
              ),
            ),
          ],
        ),
        // edit profile
        ElevatedButton(
          onPressed: () {
            controller.navigateToEditProfile();
          },
          child: Text(
            'ویرایش پروفایل',
            style: context.textTheme.bodyMedium,
          ),
        ),

        const SizedBox(
          height: 16,
        ),
        Row(
          children: [
            Expanded(
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Image.asset(
                    'images/img_cup.png',
                    width: 50,
                    height: 50,
                  ),
                  const SizedBox(
                    width: 8,
                  ),
                  Text(
                    'رنک',
                    style: context.textTheme.bodyMedium,
                  )
                ],
              ),
            ),
            Expanded(
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Image.asset(
                    'images/icon_flash.png',
                    width: 50,
                    height: 50,
                  ),
                  const SizedBox(
                    width: 8,
                  ),
                  Text(
                    'لول',
                    style: context.textTheme.bodyMedium,
                  )
                ],
              ),
            ),
          ],
        ),
        const SizedBox(
          height: 10,
        ),
        Row(
          children: [
            Expanded(
              child: Center(
                child: Text(
                  '${controller.profile?.rank}',
                  style: context.textTheme.bodyLarge,
                ),
              ),
            ),
            Expanded(
              child: Center(
                child: Text(
                  '${controller.profile?.xp}',
                  style: context.textTheme.bodyLarge,
                ),
              ),
            ),
          ],
        )
      ],
    );
  }
}
