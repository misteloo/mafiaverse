import 'dart:ui';

import 'package:bot_toast/bot_toast.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:lottie/lottie.dart';
import 'package:mafia/models/local_game/local_game_my_character_model.dart';
import 'package:mafia/models/local_game/local_game_users_model.dart';
import 'package:mafia/screens/local_game/local_game_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';
import 'package:mafia/utils/widget/flip_card.dart';
import 'package:permission_handler/permission_handler.dart';

class LocalGameScreen extends GetView<LocalGameScreenController> {
  const LocalGameScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      resizeToAvoidBottomInset: true,
      appBar: AppBar(
        centerTitle: true,
        title: Text(
          'بازی حضوری',
          style: context.textTheme.bodyLarge,
        ),
        iconTheme: const IconThemeData(color: Colors.white),
        leading: BackButton(
          color: Colors.white,
          onPressed: () {
            controller.removeLocalGame();
            Get.back();
          },
        ),
      ),
      body: PopScope(
        onPopInvoked: (didPop) {
          controller.removeLocalGame();
        },
        child: Stack(
          children: [
            Center(
              child: Image.asset(
                'images/coffee_cup.png',
                width: 150,
                height: 150,
              ),
            ),
            Column(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                GetBuilder<LocalGameScreenController>(
                  id: 'gameStarted',
                  builder: (_) => controller.gameStarted &&
                          controller.localGameUsers.isNotEmpty
                      // show users to moderator
                      ? Expanded(
                          child: ListView.builder(
                            itemBuilder: (context, index) => _users(
                                controller.localGameUsers[index], context),
                            itemCount: controller.localGameUsers.length,
                          ),
                        )
                      // show character to player
                      : Visibility(
                          visible: controller.gameStarted,
                          child: Expanded(
                            child: MyCharacterLocalGame(
                              character: controller.myCharacter,
                            ),
                          ),
                        ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: GetBuilder<LocalGameScreenController>(
                    id: 'panel',
                    builder: (_) => !controller.gamePanel
                        ? Row(
                            mainAxisAlignment: MainAxisAlignment.spaceAround,
                            children: [
                              FloatingActionButton.extended(
                                heroTag: 'joinToGame',
                                onPressed: () async {
                                  if (await Permission.camera.status.isDenied) {
                                    Permission.camera.request().then((value) {
                                      if (value == PermissionStatus.granted) {
                                        showModalBottomSheet(
                                          context: context,
                                          isScrollControlled: true,
                                          builder: (context) {
                                            return EnterPlayerName(
                                              playerName: (name) {
                                                Get.back();
                                                Future.delayed(
                                                    const Duration(seconds: 1),
                                                    () {
                                                  controller
                                                      .navigateToQrScanner(
                                                          name);
                                                });
                                              },
                                            );
                                          },
                                        );
                                      }
                                    });
                                  } else {
                                    if (context.mounted) {
                                      showModalBottomSheet(
                                        context: context,
                                        isScrollControlled: true,
                                        builder: (context) {
                                          return EnterPlayerName(
                                            playerName: (name) {
                                              Get.back();
                                              controller
                                                  .navigateToQrScanner(name);
                                            },
                                          );
                                        },
                                      );
                                    }
                                  }
                                },
                                label: const Text(
                                  'پیوستن به بازی',
                                  style: TextStyle(
                                      fontFamily: 'shabnam',
                                      color: Colors.black,
                                      fontSize: 14),
                                ),
                              ),
                              FloatingActionButton.extended(
                                heroTag: 'createGame',
                                onPressed: () {
                                  Get.dialog(CreateLocalGame(
                                    playerCount: (count) {
                                      controller.createLocalGame(count);
                                    },
                                  ));
                                },
                                label: const Text(
                                  'ساخت بازی',
                                  style: TextStyle(
                                      fontFamily: 'shabnam',
                                      color: Colors.black,
                                      fontSize: 14),
                                ),
                              ),
                            ],
                          )
                        : FloatingActionButton.extended(
                            onPressed: () {
                              controller.removeLocalGame();
                            },
                            label: Text(
                              'خروج از بازی',
                              style: TextStyle(
                                  fontFamily: 'shabnam',
                                  fontSize: 14.sp,
                                  color: Colors.black),
                            ),
                          ),
                  ),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }

  Widget _users(LocalGameUserModel model, BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(16),
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: Container(
            color: const Color.fromRGBO(102, 102, 102, 0.5),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  '${model.playerCard}',
                  style: context.textTheme.titleMedium,
                ),
                const SizedBox(
                  width: 16,
                ),
                Text(
                  model.name,
                  style: context.textTheme.bodyMedium,
                ),
                const SizedBox(
                  width: 16,
                ),
                CachedNetworkImage(
                  imageUrl: '$appBaseUrl/${model.avatar}',
                  imageBuilder: (context, imageProvider) => Container(
                    margin: const EdgeInsets.all(8),
                    width: MediaQuery.of(context).size.width * 20 / 100,
                    height: MediaQuery.of(context).size.width * 20 / 100,
                    decoration: ShapeDecoration(
                      shape: ContinuousRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              MediaQuery.of(context).size.width * 12 / 100),
                          side: const BorderSide(width: 2, color: Colors.grey)),
                      image: DecorationImage(image: imageProvider),
                    ),
                  ),
                  placeholder: (context, url) => Container(
                    margin: const EdgeInsets.all(8),
                    width: MediaQuery.of(context).size.width * 20 / 100,
                    height: MediaQuery.of(context).size.width * 20 / 100,
                    decoration: ShapeDecoration(
                        shape: ContinuousRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                MediaQuery.of(context).size.width * 12 / 100),
                            side:
                                const BorderSide(width: 2, color: Colors.grey)),
                        color: Colors.grey),
                  ),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class MyCharacterLocalGame extends StatefulWidget {
  const MyCharacterLocalGame({super.key, required this.character});
  final LocalGameMyCharacterModel character;

  @override
  State<MyCharacterLocalGame> createState() => _MyCharacterLocalGameState();
}

class _MyCharacterLocalGameState extends State<MyCharacterLocalGame> {
  bool showCard = false;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: FlipCard(
        toggler: showCard,
        backCard: InkWell(
          onTap: () {
            setState(() {
              showCard = !showCard;
            });
          },
          child: ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
              child: Container(
                width: MediaQuery.of(context).size.width * 50 / 100,
                height: MediaQuery.of(context).size.height * 50 / 100,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(16),
                  color: const Color.fromRGBO(102, 102, 102, 0.5),
                ),
                child: const Center(
                  child: Icon(
                    Icons.question_mark_rounded,
                    color: Colors.white,
                  ),
                ),
              ),
            ),
          ),
        ),
        frontCard: InkWell(
          onTap: () {
            setState(() {
              showCard = !showCard;
            });
          },
          child: ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
              child: Container(
                width: MediaQuery.of(context).size.width * 50 / 100,
                height: MediaQuery.of(context).size.height * 50 / 100,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(16),
                  color: const Color.fromRGBO(102, 102, 102, 0.5),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    Text(
                      'نقش شما',
                      style: context.textTheme.titleMedium,
                    ),
                    Text(
                      '${widget.character.name}',
                      style: context.textTheme.bodyLarge,
                    ),
                    CachedNetworkImage(
                      imageUrl: '$appBaseUrl/${widget.character.icon}',
                      imageBuilder: (context, imageProvider) => Container(
                        width: MediaQuery.of(context).size.width * 25 / 100,
                        height: MediaQuery.of(context).size.width * 25 / 100,
                        decoration: ShapeDecoration(
                          shape: ContinuousRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                MediaQuery.of(context).size.width * 13 / 100),
                            side:
                                const BorderSide(color: Colors.grey, width: 2),
                          ),
                          color: Colors.grey.shade700,
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Image(
                            image: imageProvider,
                          ),
                        ),
                      ),
                    )
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class CreateLocalGame extends StatefulWidget {
  const CreateLocalGame({super.key, required this.playerCount});
  final Function(int) playerCount;
  @override
  State<CreateLocalGame> createState() => _CreateLocalGameState();
}

class _CreateLocalGameState extends State<CreateLocalGame> {
  int playerCount = 0;
  @override
  Widget build(BuildContext context) {
    return Center(
      child: SizedBox(
        width: MediaQuery.of(context).size.width * 70 / 100,
        child: ClipRRect(
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
            child: Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(16),
                color: const Color.fromRGBO(102, 102, 102, 0.5),
              ),
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      'تعداد بازیکنای داخل بازی رو مشخص کن',
                      style: context.textTheme.bodyMedium,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Directionality(
                          textDirection: TextDirection.rtl,
                          child: Text(
                            '$playerCount نفر',
                            style: context.textTheme.bodyMedium,
                          ),
                        ),
                        const SizedBox(
                          width: 16,
                        ),
                        FloatingActionButton(
                          heroTag: 'dec',
                          onPressed: () {
                            setState(() {
                              if (playerCount > 0) {
                                playerCount--;
                              }
                            });
                          },
                          mini: true,
                          child: const Icon(
                            Icons.remove,
                            color: Colors.black,
                          ),
                        ),
                        const SizedBox(
                          width: 8,
                        ),
                        FloatingActionButton(
                          heroTag: 'inc',
                          onPressed: () {
                            setState(() {
                              playerCount++;
                            });
                          },
                          mini: true,
                          child: const Icon(
                            Icons.add,
                            color: Colors.black,
                          ),
                        )
                      ],
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    ElevatedButton(
                      onPressed: () {
                        if (playerCount == 0) {
                          BotToast.showText(
                              text: 'با تعداد صفر نمیشه بازی ساخت');
                          return;
                        }
                        // callback
                        widget.playerCount(playerCount);
                        Get.back();
                      },
                      child: Text(
                        'بعدی انتخاب نقش ها',
                        style: context.textTheme.bodyMedium,
                      ),
                    )
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class ShowQrCodeWidget extends StatelessWidget {
  const ShowQrCodeWidget(
      {super.key, required this.qrCodeImage, required this.controller});
  final dynamic qrCodeImage;
  final LocalGameScreenController controller;
  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(16),
      child: PopScope(
        canPop: false,
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: Container(
            color: const Color.fromRGBO(102, 102, 102, 0.5),
            child: Column(
              children: [
                const SizedBox(
                  width: double.infinity,
                ),
                const SizedBox(
                  height: 16,
                ),
                Text(
                  'بازی شما ایجاد شد',
                  style: context.textTheme.bodyLarge,
                ),
                const SizedBox(
                  height: 16,
                ),
                Container(
                  width: MediaQuery.of(context).size.width * 25 / 100,
                  height: MediaQuery.of(context).size.width * 25 / 100,
                  decoration: ShapeDecoration(
                    shape: ContinuousRectangleBorder(
                      borderRadius: BorderRadius.circular(
                          MediaQuery.of(context).size.width * 14 / 100),
                    ),
                    image: DecorationImage(
                      image: MemoryImage(qrCodeImage),
                    ),
                  ),
                ),
                const SizedBox(
                  height: 16,
                ),
                Directionality(
                  textDirection: TextDirection.rtl,
                  child: Padding(
                    padding: const EdgeInsets.only(left: 16.0, right: 16),
                    child: Text(
                      'از بازیکنان بخواهید Qr تولید شده را اسکن کنن. در صورتی که تعداد بازیکنان مد نظر از سمت شما با تعداد بازیکنانی که Qr را اسکن کرده اند برابر شود ، سیستم اجازه شروع بازی و پخش کارت را به شما میدهد.',
                      textAlign: TextAlign.justify,
                      style: context.textTheme.bodyMedium,
                    ),
                  ),
                ),
                Expanded(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(
                        Icons.people_rounded,
                        color: Colors.white,
                      ),
                      const SizedBox(
                        width: 8,
                      ),
                      GetBuilder<LocalGameScreenController>(
                        id: 'localUsers',
                        builder: (_) => Text(
                          '${controller.localGameUsers.length}',
                          style: context.textTheme.bodyLarge,
                        ),
                      ),
                    ],
                  ),
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    ElevatedButton(
                      onPressed: () {
                        controller.localGameUsers.clear();
                        Get.back();
                      },
                      child: Text(
                        'لغو',
                        style: context.textTheme.bodyMedium,
                      ),
                    ),
                    GetBuilder<LocalGameScreenController>(
                      id: 'canStart',
                      builder: (_) => ElevatedButton(
                        onPressed: () {
                          if (!controller.canStartTheGame) {
                            return;
                          }
                          // start
                          controller.startLocalGame();

                          Get.back();
                        },
                        style: ButtonStyle(
                          backgroundColor: controller.canStartTheGame
                              ? const MaterialStatePropertyAll(Colors.cyan)
                              : const MaterialStatePropertyAll(Colors.grey),
                        ),
                        child: Text(
                          'شروع',
                          style: context.textTheme.bodyMedium,
                        ),
                      ),
                    )
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
    );
  }
}

class EnterPlayerName extends StatelessWidget {
  const EnterPlayerName({super.key, required this.playerName});
  final Function(String) playerName;

  @override
  Widget build(BuildContext context) {
    var controller = TextEditingController();
    return Container(
      padding: EdgeInsets.only(
          top: 8, left: 8, right: 8, bottom: Get.mediaQuery.viewInsets.bottom),
      decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          color: context.theme.scaffoldBackgroundColor),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const SizedBox(
            width: double.infinity,
          ),
          Text(
            'ورود به بازی',
            style: context.textTheme.bodyLarge,
          ),
          const SizedBox(
            height: 16,
          ),
          SizedBox(
            width: MediaQuery.of(context).size.width * 70 / 100,
            child: TextField(
              controller: controller,
              decoration: InputDecoration(
                  border: context.theme.inputDecorationTheme.border,
                  labelText: 'اسمت رو بنویس',
                  labelStyle: context.textTheme.bodyMedium,
                  floatingLabelAlignment: FloatingLabelAlignment.center,
                  floatingLabelBehavior: FloatingLabelBehavior.always),
              textAlign: TextAlign.center,
            ),
          ),
          const SizedBox(
            height: 8,
          ),
          ElevatedButton(
            onPressed: () {
              if (controller.text.isEmpty) {
                BotToast.showText(text: 'اسمت رو بنویس');
                return;
              }
              // callback
              playerName(controller.text);
            },
            child: Directionality(
              textDirection: TextDirection.rtl,
              child: Text(
                'اسکن Qr کد بازی',
                style: context.textTheme.bodyMedium,
              ),
            ),
          ),
          const SizedBox(
            height: 8,
          ),
        ],
      ),
    );
  }
}

class LocalGameIdleToStart extends StatelessWidget {
  const LocalGameIdleToStart({super.key, required this.exit});
  final Function() exit;
  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      child: Center(
        child: SizedBox(
          width: MediaQuery.of(context).size.width * 70 / 100,
          child: ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
              child: Container(
                color: const Color.fromRGBO(102, 102, 102, 0.5),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    const SizedBox(
                      height: 16,
                      width: double.infinity,
                    ),
                    LottieBuilder.asset(
                      'assets/anim_loading.json',
                      width: 100,
                      height: 100,
                      animate: true,
                      repeat: true,
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    Text(
                      'منتظر پیوستن بقیه بازیکنا باش',
                      style: context.textTheme.bodyMedium,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    ElevatedButton(
                      onPressed: () {
                        exit();
                      },
                      child: Text(
                        'خروج از بازی',
                        style: context.textTheme.bodyMedium,
                      ),
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
      ),
    );
  }
}
