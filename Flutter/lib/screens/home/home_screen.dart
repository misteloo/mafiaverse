import 'dart:ui';
import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter_expandable_fab/flutter_expandable_fab.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:lottie/lottie.dart';
import 'package:mafia/screens/home/home_screen_controller.dart';
import 'package:mafia/utils/extension/extensions.dart';
import 'package:mafia/utils/widget/custom_snack.dart';

class HomeScreen extends GetView<HomeScreenController> {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // font
    ScreenUtil.init(context);
    // ignore: deprecated_member_use
    return HomeScreenHolder(controller: controller);
  }
}

class HomeScreenHolder extends StatefulWidget {
  const HomeScreenHolder({super.key, required this.controller});
  final HomeScreenController controller;
  @override
  State<HomeScreenHolder> createState() => _HomeScreenHolderState();
}

class _HomeScreenHolderState extends State<HomeScreenHolder>
    with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      widget.controller.checkSocketConnection();
    } else if (state == AppLifecycleState.paused) {}
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        floatingActionButtonLocation: ExpandableFab.location,
        floatingActionButton: ExpandableFab(
          type: ExpandableFabType.fan,
          openButtonBuilder: RotateFloatingActionButtonBuilder(
            child: const Icon(
              Icons.menu,
              color: Colors.black,
            ),
            fabSize: ExpandableFabSize.regular,
            foregroundColor: Colors.black,
            backgroundColor:
                context.theme.floatingActionButtonTheme.backgroundColor,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(40),
            ),
          ),
          closeButtonBuilder: RotateFloatingActionButtonBuilder(
            child: const Icon(
              Icons.close,
              color: Colors.black,
            ),
            fabSize: ExpandableFabSize.regular,
            foregroundColor: Colors.black,
            backgroundColor:
                context.theme.floatingActionButtonTheme.backgroundColor,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(40),
            ),
          ),
          children: [
            FloatingActionButton.extended(
              heroTag: 'fabLocalGame',
              onPressed: () {
                showCustomSnack(
                    context: context,
                    title: 'محدود شده',
                    body: 'این بخش توسط سازنده برنامه محدود شده');
                // widget.controller.navigateToLocalGame();
              },
              icon: const Icon(
                Icons.local_activity_rounded,
                color: Colors.black,
              ),
              label: const Text(
                'بازی حضوری',
                style: TextStyle(
                    fontFamily: 'shabnam', fontSize: 14, color: Colors.black),
              ),
            ),
            GetBuilder<HomeScreenController>(
              id: 'findMatchLoading',
              builder: (_) => FloatingActionButton.extended(
                heroTag: 'btnFindMatch',
                onPressed: () {
                  // check mic permission
                  widget.controller.microphonePermissionGranted(
                    (callback) async {
                      if (!callback) {
                        showCustomSnack(
                            context: context,
                            title: 'خطا',
                            body: 'دسترسی میکروفون برای بازی داده نشده');
                        return;
                      }

                      // disable double click on find match
                      if (widget.controller.findMatchLoading) return;
                      var checkVersion =
                          await widget.controller.checkAppVersion();
                      // if app require to update prevent to find game
                      if (!checkVersion) return;
                      widget.controller.userHasEnoughGoldToFindMatch(
                        callback: (hasEnough) async {
                          if (!hasEnough) {
                            showCustomSnack(
                                context: context,
                                title: 'خطا',
                                body: 'سکه شما کافی نیست');
                            return;
                          }

                          // prevent find match when serving require to update
                          if (widget.controller.serverUpdate) {
                            showCustomSnack(
                                context: context,
                                title: 'خطا',
                                body: 'سرور در حال به روز رسانی');
                          } else {
                            widget.controller.findMatch();
                          }
                        },
                      );
                    },
                  );
                },
                extendedTextStyle: const TextStyle(
                    fontSize: 14, color: Colors.black, fontFamily: 'shabnam'),
                label: GetBuilder<HomeScreenController>(
                  id: 'freeGame',
                  builder: (_) => widget.controller.freeGame
                      ? Text(
                          'بازی رایگان',
                          style: TextStyle(
                              fontFamily: 'shabnam',
                              fontSize: 14.sp,
                              color: Colors.black),
                        )
                      : Text(
                          'بازی آنلاین',
                          style: TextStyle(
                              fontFamily: 'shabnam',
                              fontSize: 14.sp,
                              color: Colors.black),
                        ),
                ),
                icon: Stack(
                  alignment: Alignment.center,
                  children: [
                    Visibility(
                      visible: !widget.controller.findMatchLoading,
                      child: const Icon(
                        Icons.search_rounded,
                        color: Colors.black,
                      ),
                    ),
                    Visibility(
                      visible: widget.controller.findMatchLoading,
                      child: LoadingAnimationWidget.prograssiveDots(
                          color: Get.isDarkMode ? Colors.white : Colors.black,
                          size: 26),
                    )
                  ],
                ),
              ),
            )
          ],
        ),
        body: Stack(
          children: [
            Container(
              width: double.infinity,
              height: double.infinity,
              decoration: const BoxDecoration(
                  image: DecorationImage(
                      image: AssetImage('images/home_background.jpg'),
                      fit: BoxFit.cover)),
            ),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // gold and gem
                  GetBuilder<HomeScreenController>(
                      id: 'userAsset', builder: (_) => userAssets(context)),

                  // update server announcement
                  GetBuilder<HomeScreenController>(
                      id: 'serverUpdate',
                      builder: (_) => serverUpdateAnnouncement(context)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget userAssets(BuildContext? context) {
    return SizedBox(
      width: double.infinity,
      child: ClipRRect(
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: Container(
            padding: const EdgeInsets.all(4),
            decoration: ShapeDecoration(
                shape: ContinuousRectangleBorder(
                  borderRadius: BorderRadius.circular(30),
                ),
                gradient: const LinearGradient(colors: [
                  Color.fromRGBO(102, 102, 102, 0.5),
                  Color.fromRGBO(110, 110, 110, 0.5),
                ])),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    LottieBuilder.asset(
                      'assets/coins.json',
                      width: 50,
                      height: 50,
                    ),
                    const SizedBox(
                      width: 8.0,
                    ),
                    Text(
                      widget.controller.userGoldCount.numberSeparator(),
                      style: context?.textTheme.bodyLarge,
                    ),
                  ],
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    // update app
                    GetBuilder<HomeScreenController>(
                        id: 'appUpdate',
                        builder: (_) => appRequireUpdate(context)),
                    IconButton(
                      onPressed: () {
                        widget.controller.navigateToMessage();
                      },
                      icon: const Icon(
                        Icons.messenger_rounded,
                        color: Colors.white,
                      ),
                    ),

                    IconButton(
                      onPressed: () {
                        widget.controller.navigateToLearn();
                      },
                      icon: const Icon(
                        Icons.style_rounded,
                        color: Colors.white,
                      ),
                    )
                  ],
                )
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget appRequireUpdate(BuildContext? context) {
    return Visibility(
      visible: widget.controller.appRequireUpdate,
      child: IconButton(
          onPressed: () {
            BotToast.showText(text: 'برنامه نیاز به آپدیت داره');
          },
          icon: const Icon(
            Icons.download,
            color: Colors.white,
          )),
    );
  }

  Widget serverUpdateAnnouncement(BuildContext? context) {
    return AnimatedSize(
      duration: const Duration(milliseconds: 400),
      child: Visibility(
        visible: widget.controller.serverUpdate,
        replacement: SizedBox(
          width: Get.width,
        ),
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
              child: Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(16),
                  color: const Color.fromRGBO(105, 105, 105, 0.5),
                ),
                child: Directionality(
                  textDirection: TextDirection.rtl,
                  child: Column(
                    children: [
                      Text(
                        'اطلاعیه',
                        style: context?.textTheme.bodyLarge,
                      ),
                      const SizedBox(
                        height: 8.0,
                      ),
                      Text(
                        'سرور در ساعت 6 صبح به مدت 30 دقیقه برای به روز رسانی از دسترس خارج خواهد شد',
                        style: context?.textTheme.bodyMedium,
                        textAlign: TextAlign.center,
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
