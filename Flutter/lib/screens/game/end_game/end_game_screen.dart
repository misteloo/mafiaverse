import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';
import 'package:mafia/models/game/end_game_result_model.dart';
import 'package:mafia/screens/game/end_game/end_game_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';

class EndGameScreen extends GetView<EndGameScreenController> {
  const EndGameScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);

    return SafeArea(
      child: Scaffold(
        floatingActionButton: GetBuilder<EndGameScreenController>(
          id: 'fab',
          builder: (_) => AnimatedOpacity(
            opacity: controller.visibleFab ? 1 : 0,
            duration: const Duration(milliseconds: 400),
            child: FloatingActionButton.extended(
              heroTag: 'returnToHome',
              onPressed: () {
                controller.returnToHome();
              },
              icon: const Icon(
                Icons.home_rounded,
                color: Colors.black,
              ),
              label: Text(
                'صفجه اصلی',
                style: TextStyle(
                    fontFamily: 'shabnam',
                    fontSize: 14.sp,
                    color: Colors.black),
              ),
            ),
          ),
        ),
        body: PopScope(
          canPop: false,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Stack(
              clipBehavior: Clip.none,
              children: [
                Center(
                  child: Image.asset(
                    'images/end_game_result_background.png',
                    width: MediaQuery.of(context).size.width * 60 / 100,
                    height: MediaQuery.of(context).size.width * 60 / 100,
                  ),
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    const SizedBox(
                      height: 8,
                    ),
                    Text(
                      'جدول امتیازات',
                      style: context.textTheme.bodyLarge,
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    Expanded(
                      child: GetBuilder<EndGameScreenController>(
                        id: 'users',
                        builder: (_) => ListView.builder(
                          itemBuilder: (context, index) =>
                              AnimationConfiguration.staggeredList(
                            position: index,
                            duration: const Duration(milliseconds: 800),
                            child: SlideAnimation(
                              horizontalOffset: 100,
                              child: FadeInAnimation(
                                child: EndGameScreenItem(
                                  user: controller.users[index],
                                  controller: controller,
                                ),
                              ),
                            ),
                          ),
                          itemCount: controller.users.length,
                        ),
                      ),
                    )
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class EndGameScreenItem extends StatelessWidget {
  const EndGameScreenItem(
      {super.key, required this.user, required this.controller});
  final EndGameResultModel user;
  final EndGameScreenController controller;
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8),
      child: SizedBox(
        width: double.infinity,
        child: ClipRRect(
          borderRadius: BorderRadius.circular(16),
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
            child: Container(
              decoration: ShapeDecoration(
                shape: ContinuousRectangleBorder(
                    borderRadius: BorderRadius.circular(40)),
                color: const Color.fromRGBO(102, 102, 102, 0.5),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const SizedBox(
                    height: 16,
                  ),
                  Row(
                    children: [
                      Expanded(
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Column(
                              mainAxisAlignment: MainAxisAlignment.spaceAround,
                              children: [
                                Image.asset(
                                  'images/img_cup.png',
                                  width: 34,
                                  height: 34,
                                ),
                                Text(
                                  '${user.point}',
                                  style: context.textTheme.bodyMedium,
                                )
                              ],
                            ),
                            Column(
                              mainAxisAlignment: MainAxisAlignment.spaceAround,
                              children: [
                                Image.asset(
                                  'images/gold.png',
                                  width: 34,
                                  height: 34,
                                ),
                                Text(
                                  '${user.gold}',
                                  style: context.textTheme.bodyMedium,
                                )
                              ],
                            ),
                            Column(
                              mainAxisAlignment: MainAxisAlignment.spaceAround,
                              children: [
                                Image.asset(
                                  'images/icon_flash.png',
                                  width: 34,
                                  height: 34,
                                ),
                                Text(
                                  '${user.xp}',
                                  style: context.textTheme.bodyMedium,
                                )
                              ],
                            )
                          ],
                        ),
                      ),
                      Expanded(
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            Column(
                              mainAxisAlignment: MainAxisAlignment.spaceAround,
                              children: [
                                SizedBox(
                                  width: 40,
                                  child: Text(
                                    user.user_name,
                                    style: context.textTheme.bodyMedium,
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                  ),
                                ),
                                const SizedBox(
                                  height: 8,
                                ),
                                Image.asset(
                                  'images/${user.role}.png',
                                  width: 40,
                                  height: 40,
                                )
                              ],
                            ),
                            const SizedBox(
                              width: 16,
                            ),
                            CachedNetworkImage(
                              imageUrl: '$appBaseUrl/${user.user_image}',
                              imageBuilder: (context, imageProvider) =>
                                  Container(
                                width: MediaQuery.of(context).size.width *
                                    15 /
                                    100,
                                height: MediaQuery.of(context).size.width *
                                    15 /
                                    100,
                                decoration: ShapeDecoration(
                                  shape: ContinuousRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        MediaQuery.of(context).size.width *
                                            10 /
                                            100),
                                  ),
                                  image: DecorationImage(
                                      image: imageProvider, fit: BoxFit.cover),
                                ),
                              ),
                            ),
                            const SizedBox(
                              width: 16,
                            )
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  IconButton(
                    onPressed: () {
                      showModalBottomSheet(
                        context: context,
                        builder: (context) => SizedBox(
                            height:
                                MediaQuery.of(context).size.height * 40 / 100,
                            child: ReportPlayer(
                              callback: (String value) {
                                Get.back();
                                // send
                                controller.reportPlayer(
                                    value: value, reportId: user.user_id);
                              },
                            )),
                        isScrollControlled: true,
                        backgroundColor: Colors.transparent,
                      );
                    },
                    icon: const Icon(
                      Icons.report_rounded,
                      color: Colors.grey,
                    ),
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

class ReportPlayer extends StatefulWidget {
  const ReportPlayer({super.key, required this.callback});
  final Function(String) callback;

  @override
  State<ReportPlayer> createState() => _ReportPlayerState();
}

class _ReportPlayerState extends State<ReportPlayer> {
  bool word = false;
  bool role = false;
  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: const BorderRadius.only(
        topLeft: Radius.circular(16),
        topRight: Radius.circular(16),
      ),
      child: BackdropFilter(
        filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
        child: Container(
          color: const Color.fromRGBO(102, 102, 102, 0.5),
          child: Column(
            children: [
              const SizedBox(
                width: double.infinity,
                height: 16,
              ),
              Text(
                'گزارش بازیکن',
                style: context.textTheme.bodyMedium,
              ),
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Directionality(
                  textDirection: TextDirection.rtl,
                  child: Text(
                    'درصورت خطای بازیکن در حین بازی ، میتوانید از گزینه های زیر انتخاب و بازیکن را گزارش کنید',
                    textAlign: TextAlign.justify,
                    style: context.textTheme.bodySmall,
                  ),
                ),
              ),
              const SizedBox(
                height: 16,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  Expanded(
                      child: Column(
                    children: [
                      Text(
                        'افشای نقش',
                        style: context.textTheme.bodyMedium,
                      ),
                      const SizedBox(
                        height: 8,
                      ),
                      IconButton(
                        onPressed: () {
                          setState(() {
                            role = !role;
                            word = false;
                          });
                        },
                        icon: Container(
                          padding: const EdgeInsets.all(8),
                          decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(12),
                              border: Border.all(
                                  color: role ? Colors.red : Colors.grey,
                                  width: 2)),
                          child: const Icon(
                            Icons.style_rounded,
                            color: Colors.white,
                          ),
                        ),
                      )
                    ],
                  )),
                  Expanded(
                      child: Column(
                    children: [
                      Text(
                        'بد گویی',
                        style: context.textTheme.bodyMedium,
                      ),
                      const SizedBox(
                        height: 8,
                      ),
                      IconButton(
                        onPressed: () {
                          setState(() {
                            role = false;
                            word = !word;
                          });
                        },
                        icon: Container(
                          padding: const EdgeInsets.all(8),
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(12),
                            border: Border.all(
                                color: word ? Colors.red : Colors.grey,
                                width: 2),
                          ),
                          child: const Icon(Icons.password_rounded,
                              color: Colors.white),
                        ),
                      )
                    ],
                  )),
                ],
              ),
              const SizedBox(
                height: 16,
              ),
              ElevatedButton(
                onPressed: () {
                  if (word || role) {
                    if (word) {
                      widget.callback('word');
                    } else if (role) {
                      widget.callback('role');
                    }
                  }
                },
                style: ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(
                      word || role ? Colors.cyan : Colors.grey),
                ),
                child: Text(
                  'ارسال گزارش',
                  style: context.textTheme.bodyMedium,
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
