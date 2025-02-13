// ignore_for_file: deprecated_member_use

import 'dart:async';
import 'dart:math';
import 'dart:ui';
import 'package:bot_toast/bot_toast.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:circular_countdown_timer/circular_countdown_timer.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_volume_controller/flutter_volume_controller.dart';
import 'package:get/get.dart';
import 'package:lottie/lottie.dart';
import 'package:mafia/models/game/in_game_user_model.dart';
import 'package:mafia/models/game/mafia_visitation_model.dart';
import 'package:mafia/models/game/nato/nato_night_act_model.dart';
import 'package:mafia/screens/game/nato_game/nato_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';
import 'package:mafia/utils/widget/custom_snack.dart';

class NatoScreen extends GetView<NatoScreenController> {
  const NatoScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);

    return SafeArea(
      child: Scaffold(
        body: SizedBox(
          width: double.infinity,
          height: double.infinity,
          child: GetBuilder<NatoScreenController>(
            id: 'gameStarted',
            builder: (_) => controller.gameStarted
                ? WillPopScope(
                    onWillPop: () async {
                      // show exit dialog
                      Get.dialog(
                          ExitFromGameAlert(
                            controller: controller,
                            onBack: () {
                              if (Get.isDialogOpen == true) {
                                controller.returnToHomeScreen();
                              }
                            },
                          ),
                          barrierDismissible: false);
                      return false;
                    },
                    child: Column(
                      children: [
                        // header board
                        Expanded(
                            flex:
                                MediaQuery.of(context).size.height * 10 ~/ 100,
                            child: HeaderBoard(controller: controller)),
                        // game board
                        Expanded(
                            flex:
                                MediaQuery.of(context).size.height * 80 ~/ 100,
                            child: GameBoard(controller: controller)),
                        // action board
                        Expanded(
                            flex:
                                MediaQuery.of(context).size.height * 10 ~/ 100,
                            child: ActionBoard(controller: controller))
                      ],
                    ),
                  )
                : WillPopScope(
                    onWillPop: () async {
                      return false;
                    },
                    child: const Center(
                      child: WaitingOtherPlayerJoin(),
                    ),
                  ),
          ),
        ),
      ),
    );
  }
}

class HeaderBoard extends StatelessWidget {
  const HeaderBoard({super.key, required this.controller});
  final NatoScreenController controller;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Row(
      children: [
        SizedBox(
          width: MediaQuery.of(context).size.width * 5 / 100,
        ),
        // user character
        Expanded(
          flex: 4,
          child: Row(
            children: [
              Align(
                alignment: Alignment.centerLeft,
                child: GetBuilder<NatoScreenController>(
                  id: 'userCharacter',
                  builder: (_) => InkWell(
                    onTap: () {},
                    child: Container(
                      width: MediaQuery.of(context).size.width * 12 / 100,
                      height: MediaQuery.of(context).size.width * 12 / 100,
                      decoration: ShapeDecoration(
                        shape: ContinuousRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              MediaQuery.of(context).size.width * 7 / 100),
                        ),
                        image: DecorationImage(
                            image: AssetImage(
                              controller.characterImage!,
                            ),
                            fit: BoxFit.cover),
                      ),
                    ),
                  ),
                ),
              ),
              const SizedBox(
                width: 8,
              ),
              GetBuilder<NatoScreenController>(
                id: 'userCharacter',
                builder: (_) => Column(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    Text(
                      '${controller.characterName}',
                      style: context.textTheme.bodySmall,
                    ),
                    Row(
                      children: [
                        GestureDetector(
                          onTap: () async {
                            await FlutterVolumeController.lowerVolume(0.1);
                          },
                          child: const Icon(
                            Icons.volume_down,
                            color: Colors.white,
                          ),
                        ),
                        const SizedBox(
                          width: 16,
                        ),
                        GestureDetector(
                          onTap: () async {
                            await FlutterVolumeController.raiseVolume(0.1);
                          },
                          child: const Icon(Icons.volume_up_rounded,
                              color: Colors.white),
                        ),
                      ],
                    )
                  ],
                ),
              )
            ],
          ),
        ),
        // game timer
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              GetBuilder<NatoScreenController>(
                id: 'timer',
                builder: (_) => CircularCountDownTimer(
                  controller: controller.gameTimerController,
                  width: MediaQuery.of(context).size.width * 11 / 100,
                  height: MediaQuery.of(context).size.width * 11 / 100,
                  duration: 0,
                  fillColor: Colors.cyan,
                  ringColor: Colors.transparent,
                  isReverseAnimation: true,
                  isReverse: true,
                  autoStart: false,
                  textStyle: context.textTheme.bodyMedium,
                  strokeCap: StrokeCap.round,
                ),
              ),
            ],
          ),
        ),
        // game event - day gun status - mafia list
        Expanded(
          flex: 4,
          child: Center(
            child: Row(
              children: [
                _showGameEvent(context),
                _dayGun(context),
                _showMafiaList(context)
              ],
            ),
          ),
        ),
        SizedBox(
          width: MediaQuery.of(context).size.width * 5 / 100,
        )
      ],
    );
  }

  Expanded _showGameEvent(BuildContext context) {
    return Expanded(
      child: GetBuilder<NatoScreenController>(
        id: 'mainGameEvent',
        builder: (_) {
          IconData icon;
          if (controller.mainGameEvent == 'day') {
            icon = Icons.light_mode_rounded;
          } else if (controller.mainGameEvent == 'vote') {
            icon = Icons.how_to_vote_rounded;
          } else if (controller.mainGameEvent == 'night') {
            icon = Icons.nightlight_round;
          } else if (controller.mainGameEvent == 'chaos') {
            icon = Icons.workspaces_rounded;
          } else {
            icon = Icons.question_mark_rounded;
          }

          return InkWell(
            onTap: () {
              if (controller.mainGameEvent == 'day') {
                BotToast.showText(
                    text: 'وضعیت بازی ، روز',
                    textStyle: context.textTheme.bodyMedium!);
              } else if (controller.mainGameEvent == 'vote') {
                BotToast.showText(
                    text: 'وضعیت بازی ، رای گیری',
                    textStyle: context.textTheme.bodyMedium!);
              } else if (controller.mainGameEvent == 'night') {
                BotToast.showText(
                    text: 'وضعیت بازی ، شب',
                    textStyle: context.textTheme.bodyMedium!);
              } else if (controller.mainGameEvent == 'chaos') {
                BotToast.showText(
                    text: 'وضعیت بازی ، کی آس',
                    textStyle: context.textTheme.bodyMedium!);
              }
            },
            splashColor: Colors.transparent,
            child: Ink(
              child: Icon(
                icon,
                color: Colors.white,
              ),
            ),
          );
        },
      ),
    );
  }

  Expanded _dayGun(BuildContext context) {
    return Expanded(
      child: GetBuilder<NatoScreenController>(
        id: 'dayGunStatus',
        builder: (_) => !controller.dayGunAvailable
            ? const Center()
            : InkWell(
                onTap: () {
                  if (!controller.checkSelfAlive()) {
                    BotToast.showText(text: 'شما زنده نیستی');
                    return;
                  }
                  if (controller.mainGameEvent != 'day') {
                    BotToast.showText(
                        text: 'اسلحه فقط تو روز کار میکنه',
                        textStyle: context.textTheme.bodyMedium!);
                    return;
                  }
                  if (controller.dayGunAvailable) {
                    BotToast.showText(
                        text: 'روی اسلحه نگهدار تا فعال بشه',
                        textStyle: context.textTheme.bodyMedium!);
                  } else {
                    BotToast.showText(
                        text: 'اسلحت تیری نداره',
                        textStyle: context.textTheme.bodyMedium!);
                  }
                },
                onLongPress: () {
                  if (!controller.checkSelfAlive()) {
                    return;
                  }
                  if (controller.mainGameEvent != 'day') {
                    return;
                  }

                  if (!controller.dayGunAvailable) {
                    return;
                  }

                  // active day gun
                  controller.activeDayGun();
                  // msg
                  showCustomSnack(
                    context: context,
                    title: 'اسلحه فعال شد',
                    body:
                        'روی بازیکن مدنظرت نگهدار تا شلیک کنی ، زمان کمه زود یکیو انتخاب کن',
                    duration: const Duration(seconds: 5),
                  );
                },
                splashColor: Colors.transparent,
                child: Ink(
                  child: Image.asset(
                    'images/day_gun.png',
                    color: Colors.grey,
                    width: 24,
                    height: 24,
                  ),
                ),
              ),
      ),
    );
  }

  Expanded _showMafiaList(BuildContext context) {
    return Expanded(
      child: GetBuilder<NatoScreenController>(
        id: 'mafiaList',
        builder: (_) => controller.mafiaList.isNotEmpty
            ? InkWell(
                onTap: () {
                  controller.mafiaToggleView();
                },
                splashColor: Colors.transparent,
                child: Ink(
                  child: GetBuilder<NatoScreenController>(
                    id: 'mafiaToggle',
                    builder: (_) => Icon(
                      controller.mafiaListVisibilityToggling
                          ? Icons.visibility_off_rounded
                          : Icons.visibility,
                      color: Colors.grey,
                    ),
                  ),
                ),
              )
            : const Center(),
      ),
    );
  }
}

class GameBoard extends StatelessWidget {
  const GameBoard({super.key, required this.controller});
  final NatoScreenController controller;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Stack(
      clipBehavior: Clip.none,
      alignment: Alignment.center,
      children: [
        // game table
        _gameTable(context),
        // left side
        _leftSideUsers(context),
        // right side
        _rightSideUsers(context)
      ],
    );
  }

  Widget _rightSideUsers(BuildContext context) {
    return Align(
      alignment: Alignment.topRight,
      child: GetBuilder<NatoScreenController>(
        id: 'users',
        builder: (_) {
          var rightUsers = controller.users
              .getRange(controller.users.length > 5 ? 5 : 0,
                  controller.users.length > 5 ? controller.users.length : 0)
              .toList();
          return Column(
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: List.generate(
              rightUsers.length,
              (index) => GetBuilder<NatoScreenController>(
                id: rightUsers[index].userIdentity.user_id,
                builder: (_) {
                  return Player(
                    index: index + 6,
                    user: rightUsers[index],
                    right: true,
                    controller: controller,
                  );
                },
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _leftSideUsers(BuildContext context) {
    return Align(
      alignment: Alignment.topLeft,
      child: GetBuilder<NatoScreenController>(
        id: 'users',
        builder: (_) {
          var leftUsers = controller.users
              .getRange(
                  0, controller.users.length > 5 ? 5 : controller.users.length)
              .toList();

          return Column(
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: List.generate(
              leftUsers.length,
              (index) => GetBuilder<NatoScreenController>(
                id: leftUsers[index].userIdentity.user_id,
                builder: (_) {
                  return Player(
                    index: index + 1,
                    user: leftUsers[index],
                    controller: controller,
                  );
                },
              ),
            ),
          );
        },
      ),
    );
  }

  Container _gameTable(BuildContext context) {
    return Container(
      width: MediaQuery.of(context).size.width / 2,
      margin: const EdgeInsets.only(top: 16, bottom: 16),
      decoration: ShapeDecoration(
        shape: ContinuousRectangleBorder(
          borderRadius: BorderRadius.circular(70),
        ),
        image: const DecorationImage(
            image: AssetImage('images/game_table.jpg'), fit: BoxFit.cover),
      ),
    );
  }
}

class ActionBoard extends StatelessWidget {
  const ActionBoard({super.key, required this.controller});
  final NatoScreenController controller;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return GetBuilder<NatoScreenController>(
        id: 'actionBoard',
        builder: (_) {
          if (controller.gameEvent == 'action') {
            return _action(context, controller.gameEvent);
          } else if (controller.gameEvent == 'speech') {
            return _speech(context, controller.gameEvent);
          } else if (controller.gameEvent == 'vote') {
            return _vote(context, controller.gameEvent);
          } else if (controller.gameEvent == 'night') {
            return _nightIdle(context);
          } else if (controller.gameEvent == 'dead') {
            return _dead();
          } else {
            return const Center();
          }
        });
  }

  Widget _nightIdle(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        LottieBuilder.asset('assets/night.json'),
        Text('منتظر باش تا بیدارشی', style: context.textTheme.bodyMedium)
      ],
    );
  }

  Widget _speech(BuildContext context, String action) {
    return AnimatedOpacity(
      opacity: action == 'speech' ? 1 : 0,
      duration: const Duration(milliseconds: 500),
      child: Row(
        children: [
          // next player
          Expanded(
            child: Center(
              child: GetBuilder<NatoScreenController>(
                id: 'btnNextPlayer',
                builder: (_) => FloatingActionButton(
                  mini: true,
                  heroTag: 'fabNextPlayer',
                  onPressed: () {
                    if (controller.nextPlayerAvailable == false) {
                      return;
                    }
                    controller.nextSpeech();
                  },
                  backgroundColor: controller.nextPlayerAvailable == false
                      ? Colors.grey
                      : context.theme.floatingActionButtonTheme.backgroundColor,
                  child: Icon(
                    Icons.navigate_next_rounded,
                    color: controller.nextPlayerAvailable == false
                        ? Colors.grey.shade800
                        : Colors.black,
                  ),
                ),
              ),
            ),
          ),
          // mic status
          Expanded(
            child: Center(
              child: Obx(
                () => FloatingActionButton(
                  mini: true,
                  heroTag: 'btnMicStatus',
                  onPressed: () {
                    controller.livekitMicToggle();
                  },
                  backgroundColor:
                      controller.micToggle.value ? Colors.green : Colors.grey,
                  child: Icon(
                    controller.micToggle.value
                        ? Icons.mic_rounded
                        : Icons.mic_off_rounded,
                    color: controller.micToggle.value
                        ? Colors.white
                        : Colors.black,
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _action(BuildContext context, String action) {
    return AnimatedOpacity(
        opacity: action == 'action' ? 1 : 0,
        duration: const Duration(milliseconds: 500),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            // challenge request
            Expanded(
              child: Center(
                child: Obx(
                  () => !controller.activeChallengeRequestTimer.value
                      ? InkWell(
                          splashColor: Colors.transparent,
                          onTap: () {
                            if (!controller.getMyDetails.canTakeChallenge) {
                              return;
                            }
                            controller.selfUserActions(challengeRequest: true);
                          },
                          child: Center(
                            child: Container(
                              padding: const EdgeInsets.all(8),
                              decoration: BoxDecoration(
                                  borderRadius: BorderRadius.circular(16),
                                  color:
                                      controller.getMyDetails.canTakeChallenge
                                          ? Colors.cyan
                                          : Colors.grey),
                              child: Text(
                                controller.getMyDetails.canTakeChallenge
                                    ? 'چالش'
                                    : 'چالش',
                                style: TextStyle(
                                    color:
                                        controller.getMyDetails.canTakeChallenge
                                            ? Colors.white
                                            : Colors.black),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        )
                      : CircularCountDownTimer(
                          controller:
                              controller.challengeRequestTimerController,
                          width: MediaQuery.of(context).size.width * 9 / 100,
                          height: MediaQuery.of(context).size.width * 9 / 100,
                          duration: 10,
                          fillColor: Colors.cyan,
                          ringColor: Colors.transparent,
                          autoStart: true,
                          isTimerTextShown: false,
                          isReverseAnimation: true,
                          strokeCap: StrokeCap.round,
                          onComplete: () {
                            controller.activeChallengeRequestTimer.value =
                                false;
                            controller.challengeRequestTimerController.reset();
                          },
                        ),
                ),
              ),
            ),
            // dislike
            Expanded(
              child: Center(
                child: Obx(
                  () => !controller.userLikeDislikeActive.value
                      ? FloatingActionButton(
                          mini: true,
                          heroTag: 'btnDislike',
                          onPressed: () {
                            controller.selfUserActions(dislike: true);
                          },
                          child: const Icon(
                            Icons.thumb_down_alt_rounded,
                            color: Colors.black,
                          ),
                        )
                      : CircularCountDownTimer(
                          width: MediaQuery.of(context).size.width * 9 / 100,
                          height: MediaQuery.of(context).size.width * 9 / 100,
                          duration: 5,
                          fillColor: Colors.cyan,
                          ringColor: Colors.transparent,
                          autoStart: true,
                          isTimerTextShown: false,
                          isReverseAnimation: true,
                          strokeCap: StrokeCap.round,
                          onComplete: () {
                            controller.userLikeDislikeActive(false);
                          },
                        ),
                ),
              ),
            ),
            // like
            Expanded(
              child: Center(
                child: Obx(
                  () => !controller.userLikeDislikeActive.value
                      ? FloatingActionButton(
                          mini: true,
                          heroTag: 'btnLike',
                          onPressed: () {
                            controller.selfUserActions(like: true);
                          },
                          child: const Icon(
                            Icons.thumb_up_alt_rounded,
                            color: Colors.black,
                          ),
                        )
                      : CircularCountDownTimer(
                          width: MediaQuery.of(context).size.width * 9 / 100,
                          height: MediaQuery.of(context).size.width * 9 / 100,
                          duration: 5,
                          fillColor: Colors.cyan,
                          ringColor: Colors.transparent,
                          autoStart: true,
                          strokeCap: StrokeCap.round,
                          isTimerTextShown: false,
                          isReverseAnimation: true,
                          onComplete: () {
                            controller.userLikeDislikeActive(false);
                          },
                        ),
                ),
              ),
            )
          ],
        ));
  }

  Widget _vote(BuildContext context, String action) {
    return AnimatedOpacity(
      opacity: action == 'vote' ? 1 : 0,
      duration: const Duration(milliseconds: 500),
      child: Center(
        child: FloatingActionButton(
          mini: true,
          onPressed: () {
            controller.voteToPlayer();
          },
          child: const Icon(
            Icons.back_hand_rounded,
            color: Colors.black,
          ),
        ),
      ),
    );
  }

  Widget _dead() {
    return Center(
      child: Image.asset('images/img_rip.png'),
    );
  }
}

class Player extends StatelessWidget {
  const Player({
    super.key,
    required this.index,
    required this.user,
    required this.controller,
    this.right = false,
  });
  final int index;
  final InGameUserModel user;
  final bool right;
  final NatoScreenController controller;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);

    var widgetHolder = [
      Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // player index
          Text(
            index.toString(),
          ),
          Center(
            child: Stack(
              children: [
                // player avatar and name
                InkWell(
                  onTap: () {},
                  splashColor: Colors.transparent,
                  onLongPress: () {
                    if (!controller.dayGunAvailable) {
                      BotToast.showText(
                          text: 'اسلحت تیری نداره',
                          textStyle: context.textTheme.bodyMedium!);
                      return;
                    }
                    if (controller.mainGameEvent != 'day') {
                      BotToast.showText(
                          text: 'اسلحه فقط در روز و هنگام صحبت فعاله',
                          textStyle: context.textTheme.bodyMedium!);
                      return;
                    }

                    if (!controller.dayGunActive) {
                      BotToast.showText(
                          text: 'رو اسلحه نگهدار تا فعال شه',
                          textStyle: context.textTheme.bodyMedium!);
                      return;
                    }

                    if (user.userIdentity.user_id == controller.getMyUserId()) {
                      BotToast.showText(
                          text: 'نمیتونی خودتو انتخاب کنی',
                          textStyle: context.textTheme.bodyMedium!);
                      return;
                    }

                    // shot
                    controller.shotWithDayGun(user.userIdentity.user_id);
                  },
                  child: Hero(
                    tag: user.userIdentity.user_id,
                    child: CachedNetworkImage(
                      imageUrl: '$appBaseUrl/${user.userIdentity.user_image}',
                      imageBuilder: (context, imageProvider) => Container(
                        width: MediaQuery.of(context).size.width * 15 / 100,
                        height: MediaQuery.of(context).size.width * 15 / 100,
                        decoration: ShapeDecoration(
                          shape: ContinuousRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                MediaQuery.of(context).size.width * 10 / 100),
                            side: BorderSide(
                                width: 2,
                                color: user.speech
                                    ? Colors.cyan
                                    : Colors.transparent),
                          ),
                          image: DecorationImage(
                              image: imageProvider, fit: BoxFit.cover),
                        ),
                        child: Stack(
                          children: [
                            // as mafia character
                            _showIfMafia(context),
                            // speech
                            _speech(context),
                            // disconnect
                            _disconnect(context),
                            // dead
                            _dead(),
                            // get targeted from day gun
                            Visibility(
                              visible: user.targeted,
                              child: GettingShot(target: user.targeted),
                            ),
                            // shot to player on day
                            DayShootingToPlayer(
                              shot: user.shot,
                              right: right,
                            ),
                            // on vote
                            _onVote(context),
                            // on waiting to hand shake
                            _waitingToHandShake(context),
                            // chaos hand shake
                            _chaosHandShake(context),
                          ],
                        ),
                      ),
                      placeholder: (context, url) => Container(
                        width: MediaQuery.of(context).size.width * 13 / 100,
                        height: MediaQuery.of(context).size.width * 13 / 100,
                        decoration: ShapeDecoration(
                            shape: ContinuousRectangleBorder(
                              borderRadius: BorderRadius.circular(
                                  MediaQuery.of(context).size.width * 7 / 100),
                            ),
                            color: Colors.grey),
                      ),
                    ),
                  ),
                )
              ],
            ),
          ),
          const SizedBox(
            height: 2,
          ),
          // user name or speech types
          _userName(context),
        ],
      ),
      // in game board view
      Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // like or dislike
          _likeDislike(),
          // hand raise
          _handRaise(),
          // target cover
          _targetCoverVolunteer(context),
          // bullet type
          _bulletType(context),
          // challenge
          _challenge(context),
          // chaos hand shake announcement
          _chaosAnnounceHandShakeToSpecificPlayer(context, right)
        ],
      )
    ];
    return SizedBox(
      child: Row(
        mainAxisAlignment:
            right ? MainAxisAlignment.end : MainAxisAlignment.start,
        children: [
          SizedBox(
            width: MediaQuery.of(context).size.width * 5 / 100,
          ),
          right ? widgetHolder[1] : widgetHolder[0],
          SizedBox(
            width: MediaQuery.of(context).size.width * 5 / 100,
          ),
          right ? widgetHolder[0] : widgetHolder[1],
          SizedBox(
            width: MediaQuery.of(context).size.width * 5 / 100,
          )
        ],
      ),
    );
  }

  AnimatedOpacity _waitingToHandShake(BuildContext context) {
    return AnimatedOpacity(
      opacity: user.turnToHandShake ? 1 : 0,
      duration: const Duration(milliseconds: 200),
      child: Container(
        padding: const EdgeInsets.all(8.0),
        decoration: ShapeDecoration(
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * 10 / 100),
              side: BorderSide(width: 2, color: Colors.grey.shade900),
            ),
            color: Colors.cyan),
        child: const Center(
          child: Icon(
            Icons.av_timer_rounded,
            color: Colors.black,
          ),
        ),
      ),
    );
  }

  AnimatedOpacity _chaosHandShake(BuildContext context) {
    return AnimatedOpacity(
      opacity: user.availableHandShake ? 1 : 0,
      duration: const Duration(milliseconds: 200),
      child: Visibility(
        visible: user.availableHandShake,
        child: Center(
          child: FloatingActionButton(
            heroTag: null,
            onPressed: () {
              controller.chaosVote(user.userIdentity.user_id);
            },
            child: const Icon(
              Icons.handshake_rounded,
              color: Colors.black,
            ),
          ),
        ),
      ),
    );
  }

  AnimatedOpacity _chaosAnnounceHandShakeToSpecificPlayer(
      BuildContext context, bool right) {
    var leftSide = Row(
      children: [
        Column(
          children: [
            Text(
              user.handShakeTo != null
                  ? (user.handShakeTo!.userIdentity.index + 1).toString()
                  : '',
              style: context.textTheme.bodySmall,
            ),
            user.handShakeTo != null
                ? CachedNetworkImage(
                    imageUrl:
                        '$appBaseUrl/${user.handShakeTo?.userIdentity.user_image}',
                    imageBuilder: (context, imageProvider) => Container(
                      width: MediaQuery.of(context).size.width * 15 / 100,
                      height: MediaQuery.of(context).size.width * 15 / 100,
                      decoration: ShapeDecoration(
                        shape: ContinuousRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              MediaQuery.of(context).size.width * 10 / 100),
                        ),
                        image: DecorationImage(
                            image: imageProvider, fit: BoxFit.cover),
                      ),
                    ),
                  )
                : const Center(),
            SizedBox(
              width: MediaQuery.of(context).size.width * 15 / 100,
              child: Text(
                user.handShakeTo != null
                    ? user.handShakeTo!.userIdentity.user_name
                    : '',
                style: context.textTheme.bodySmall,
                overflow: TextOverflow.ellipsis,
                maxLines: 1,
                textAlign: TextAlign.center,
              ),
            )
          ],
        ),
      ],
    );

    var rightSide = Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        Column(
          children: [
            Text(
              user.handShakeTo != null
                  ? (user.handShakeTo!.userIdentity.index + 1).toString()
                  : '',
              style: context.textTheme.bodySmall,
            ),
            user.handShakeTo != null
                ? CachedNetworkImage(
                    imageUrl:
                        '$appBaseUrl/${user.handShakeTo?.userIdentity.user_image}',
                    imageBuilder: (context, imageProvider) => Container(
                      width: MediaQuery.of(context).size.width * 13 / 100,
                      height: MediaQuery.of(context).size.width * 13 / 100,
                      decoration: ShapeDecoration(
                        shape: ContinuousRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              MediaQuery.of(context).size.width * 7 / 100),
                        ),
                        image: DecorationImage(
                            image: imageProvider, fit: BoxFit.cover),
                      ),
                    ),
                  )
                : const Center(),
            SizedBox(
              width: MediaQuery.of(context).size.width * 13 / 100,
              child: Text(
                user.handShakeTo != null
                    ? user.handShakeTo!.userIdentity.user_name
                    : '',
                style: context.textTheme.bodySmall,
                overflow: TextOverflow.ellipsis,
                maxLines: 1,
                textAlign: TextAlign.center,
              ),
            )
          ],
        ),
        SizedBox(
          width: MediaQuery.of(context).size.width * 5 / 100,
        ),
        FloatingActionButton(
          onPressed: () {},
          mini: true,
          child: const Icon(
            Icons.arrow_back_ios_rounded,
            color: Colors.black,
          ),
        ),
      ],
    );
    return AnimatedOpacity(
      opacity: user.handShakeTo != null ? 1 : 0,
      duration: const Duration(milliseconds: 400),
      child: Visibility(
          visible: user.handShakeTo != null ? true : false,
          child: right ? rightSide : leftSide),
    );
  }

  AnimatedOpacity _challenge(BuildContext context) {
    return AnimatedOpacity(
      opacity: user.challengeRequest || user.acceptChallengeRequest ? 1 : 0,
      duration: const Duration(milliseconds: 200),
      child: Visibility(
        visible: user.challengeRequest || user.acceptChallengeRequest,
        child: InkWell(
          onTap: () {
            controller.acceptChallenge(userId: user.userIdentity.user_id);
          },
          child: Container(
            decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(10),
                color:
                    user.acceptChallengeRequest ? Colors.green : Colors.cyan),
            padding: const EdgeInsets.all(8.0),
            child: Center(
              child: Text(
                user.acceptChallengeRequest ? 'چلنجرش منم' : 'چالش میدی؟',
                style: context.textTheme.bodySmall,
              ),
            ),
          ),
        ),
      ),
    );
  }

  AnimatedOpacity _bulletType(BuildContext context) {
    return AnimatedOpacity(
      opacity: user.targetedType != 'not' ? 1 : 0,
      duration: const Duration(milliseconds: 200),
      child: Visibility(
        visible: user.targetedType != 'not' ? true : false,
        child: Container(
          padding: const EdgeInsets.all(4),
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8), color: Colors.cyan),
          child: Center(
            child: Text(
              user.targetedType == 'fighter'
                  ? 'تیرجنگی'
                  : user.targetedType == 'lighter'
                      ? 'تیرمشقی'
                      : '',
              style: context.textTheme.bodySmall,
            ),
          ),
        ),
      ),
    );
  }

  AnimatedOpacity _handRaise() {
    return AnimatedOpacity(
      duration: const Duration(milliseconds: 300),
      opacity: user.handRaise ? 1 : 0,
      child: Visibility(
        visible: user.handRaise,
        child: FloatingActionButton(
          onPressed: () {},
          mini: true,
          child: const Icon(
            Icons.back_hand,
            color: Colors.black,
          ),
        ),
      ),
    );
  }

  AnimatedOpacity _targetCoverVolunteer(BuildContext context) {
    return AnimatedOpacity(
      opacity: user.targetCoverHandRaise || user.acceptHandRaise ? 1 : 0,
      duration: const Duration(milliseconds: 200),
      child: Visibility(
        visible:
            user.targetCoverHandRaise || user.acceptHandRaise ? true : false,
        child: FloatingActionButton(
          onPressed: () {
            controller.selectTargetCoverVolunteer(user.userIdentity.user_id);
          },
          mini: true,
          backgroundColor: !user.acceptHandRaise
              ? Colors.grey
              : context.theme.floatingActionButtonTheme.backgroundColor,
          child: const Icon(
            Icons.back_hand,
            color: Colors.black,
          ),
        ),
      ),
    );
  }

  AnimatedOpacity _likeDislike() {
    return AnimatedOpacity(
      duration: const Duration(milliseconds: 200),
      opacity: user.like || user.disLike ? 1 : 0,
      child: Visibility(
        visible: user.like || user.disLike,
        child: FloatingActionButton(
          onPressed: () {},
          mini: true,
          child: Transform.flip(
            flipY: user.like ? false : true,
            child: Transform(
              alignment: Alignment.center,
              transform: Matrix4.rotationY(right ? pi : 0),
              child: LottieBuilder.asset(
                'assets/anim_like.json',
                repeat: false,
                animate: user.like || user.disLike,
                width: 40,
                height: 40,
              ),
            ),
          ),
        ),
      ),
    );
  }

  GetBuilder<NatoScreenController> _showIfMafia(BuildContext context) {
    return GetBuilder<NatoScreenController>(
      id: 'showMafiaList',
      builder: (_) => Visibility(
        visible: user.mafiaCharacterImg != null
            ? controller.mafiaListVisibilityToggling
                ? true
                : false
            : false,
        child: Container(
          padding: const EdgeInsets.all(8.0),
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * 10 / 100),
              side: BorderSide(
                  width: 2,
                  color:
                      user.speech ? Colors.transparent : Colors.grey.shade900),
            ),
          ),
          child: Center(
            child: user.mafiaCharacterImg != null
                ? Image.asset(user.mafiaCharacterImg!)
                : null,
          ),
        ),
      ),
    );
  }

  Visibility _speech(BuildContext context) {
    return Visibility(
      visible: user.speech,
      child: Container(
        decoration: ShapeDecoration(
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * 10 / 100),
              side: BorderSide(width: 2, color: Colors.grey.shade900),
            ),
            color: Colors.grey.shade900),
        child: const Center(
          child: Icon(
            Icons.mic,
            color: Colors.white,
          ),
        ),
      ),
    );
  }

  Visibility _disconnect(BuildContext context) {
    return Visibility(
      visible: user.alive && !user.connected,
      child: Container(
        decoration: ShapeDecoration(
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * 10 / 100),
              side: BorderSide(width: 2, color: Colors.grey.shade900),
            ),
            color: Colors.grey.shade900),
        child: const Center(
          child: Icon(
            Icons.wifi_off_rounded,
            color: Colors.white,
          ),
        ),
      ),
    );
  }

  AnimatedOpacity _dead() {
    return AnimatedOpacity(
      opacity: user.alive ? 0 : 1,
      duration: const Duration(milliseconds: 0),
      child: Image.asset(
        'images/img_blood.png',
        fit: BoxFit.cover,
      ),
    );
  }

  Widget _userName(BuildContext context) {
    return SizedBox(
      width: MediaQuery.of(context).size.width * 15 / 100,
      child: Text(
        user.speechType != 'none'
            ? user.speechType
            : user.userIdentity.user_name,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
        textAlign: TextAlign.center,
        style: context.textTheme.bodySmall,
      ),
    );
  }

  AnimatedOpacity _onVote(BuildContext context) {
    return AnimatedOpacity(
      opacity: user.vote ? 1 : 0,
      duration: const Duration(milliseconds: 200),
      child: Container(
        padding: const EdgeInsets.all(8.0),
        decoration: ShapeDecoration(
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * 10 / 100),
              side: BorderSide(width: 2, color: Colors.grey.shade900),
            ),
            color: Colors.cyan),
        child: const Center(
          child: Icon(
            Icons.how_to_vote_rounded,
            color: Colors.black,
          ),
        ),
      ),
    );
  }
}

/* //////////////////////////////////////  UTIL CLASSES ///////////////////////////////////////// */

class ExitFromGameAlert extends StatelessWidget {
  const ExitFromGameAlert(
      {super.key, required this.controller, required this.onBack});
  final NatoScreenController controller;
  final Function() onBack;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Center(
        child: FittedBox(
          child: ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
              child: Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(16),
                  color: Colors.white30,
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Image.asset(
                      'images/image_exit.webp',
                      width: MediaQuery.of(context).size.width / 4,
                      height: MediaQuery.of(context).size.width / 4,
                    ),
                    Text(
                      'میخوای از بازی خارج بشی ؟',
                      style: context.textTheme.bodyLarge,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        ElevatedButton(
                          onPressed: () {
                            onBack();
                          },
                          child: Text(
                            'برگشت',
                            style: context.textTheme.bodyMedium,
                          ),
                        ),
                        const SizedBox(
                          width: 16,
                        ),
                        ElevatedButton(
                          onPressed: () {
                            controller.returnToHomeScreen();
                          },
                          child: Text(
                            'خروج',
                            style: context.textTheme.bodyMedium,
                          ),
                        ),
                      ],
                    ),
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

class GettingShot extends StatefulWidget {
  const GettingShot({super.key, required this.target});
  final bool target;

  @override
  State<GettingShot> createState() => _GettingShotState();
}

class _GettingShotState extends State<GettingShot> {
  bool target = false;

  @override
  void initState() {
    super.initState();
    Future.delayed(const Duration(seconds: 2), () {
      setState(() {
        target = false;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      width: MediaQuery.of(context).size.width * 15 / 100,
      height: MediaQuery.of(context).size.width * 15 / 100,
      decoration: ShapeDecoration(
        shape: ContinuousRectangleBorder(
          borderRadius: BorderRadius.circular(
              MediaQuery.of(context).size.width * 5 / 100),
        ),
        color: Colors.red,
      ),
      child: AnimatedOpacity(
        duration: const Duration(milliseconds: 200),
        opacity: target ? 1 : 0,
        onEnd: () {
          setState(() {
            target = true;
          });
        },
        child: Center(
          child: Visibility(
            visible: target,
            child: Image.asset('images/target_image.webp'),
          ),
        ),
      ),
    );
  }
}

class DayShootingToPlayer extends StatefulWidget {
  const DayShootingToPlayer(
      {super.key, required this.shot, required this.right});
  final bool shot;
  final bool right;
  @override
  State<DayShootingToPlayer> createState() => _DayShootingToPlayerState();
}

class _DayShootingToPlayerState extends State<DayShootingToPlayer>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late bool shot;

  @override
  void initState() {
    super.initState();
    shot = widget.shot;

    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );

    _controller.addStatusListener((status) {
      if (status == AnimationStatus.completed) {
        _controller.reverse();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedOpacity(
      opacity: widget.shot ? 1 : 0,
      duration: const Duration(milliseconds: 300),
      onEnd: () {
        if (widget.shot) {
          _controller.forward();
        }
      },
      child: Container(
        width: MediaQuery.of(context).size.width * 15 / 100,
        height: MediaQuery.of(context).size.width * 15 / 100,
        padding: const EdgeInsets.all(8.0),
        decoration: ShapeDecoration(
          shape: ContinuousRectangleBorder(
            borderRadius: BorderRadius.circular(
                MediaQuery.of(context).size.width * 10 / 100),
          ),
          color: Colors.cyan,
        ),
        child: Center(
          child: RotationTransition(
            turns: Tween(begin: 0.0, end: widget.right ? 0.069444 : -0.069444)
                .animate(_controller),
            child: !widget.right
                ? Transform(
                    alignment: Alignment.center,
                    transform: Matrix4.rotationY(pi),
                    child: Image.asset(
                      'images/day_gun.png',
                      fit: BoxFit.cover,
                    ),
                  )
                : Image.asset(
                    'images/day_gun.png',
                    fit: BoxFit.cover,
                  ),
          ),
        ),
      ),
    );
  }
}

class WaitingOtherPlayerJoin extends StatelessWidget {
  const WaitingOtherPlayerJoin({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Center(
      child: PopScope(
        canPop: false,
        child: Container(
          padding: EdgeInsets.all(MediaQuery.of(context).size.width * 8 / 100),
          width: MediaQuery.of(context).size.width * 70 / 100,
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(16),
              color: Colors.grey.shade800),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'بازی تا لحظاتی دیگه شروع میشه ، لطفا منتظر انتخاب بقیه بازیکنا باش',
                textDirection: TextDirection.rtl,
                textAlign: TextAlign.center,
                style: context.textTheme.bodyMedium,
              ),
              const SizedBox(
                height: 16,
              ),
              LottieBuilder.asset(
                'assets/anim_loading.json',
                width: MediaQuery.of(context).size.width * 35 / 100,
                height: MediaQuery.of(context).size.width * 35 / 100,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class MafiaVisitation extends StatelessWidget {
  const MafiaVisitation(
      {super.key, required this.mafiaList, required this.users});
  final List<MafiaVisitationModel> mafiaList;
  final List<InGameUserModel> users;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: PopScope(
        canPop: false,
        child: ClipRRect(
          borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
            child: Container(
              decoration: const BoxDecoration(
                borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
                color: Colors.white30,
              ),
              padding: const EdgeInsets.all(16),
              child: CustomScrollView(
                slivers: [
                  SliverToBoxAdapter(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        Text(
                          'دیدار مافیایی',
                          style: context.textTheme.bodyLarge,
                        ),
                        CircularCountDownTimer(
                          width: MediaQuery.of(context).size.width * 10 / 100,
                          height: MediaQuery.of(context).size.width * 10 / 100,
                          duration: 6,
                          fillColor: Colors.cyan,
                          ringColor: Colors.transparent,
                          strokeCap: StrokeCap.round,
                          isReverseAnimation: true,
                          isReverse: true,
                          textStyle: context.textTheme.bodyMedium,
                          onComplete: () {
                            Get.back();
                          },
                        ),
                      ],
                    ),
                  ),
                  const SliverToBoxAdapter(
                    child: SizedBox(
                      height: 16,
                    ),
                  ),
                  SliverList.builder(
                    itemCount: mafiaList.length,
                    itemBuilder: (context, index) {
                      var user = users.singleWhere((element) =>
                          element.userIdentity.user_id ==
                          mafiaList[index].userId);
                      return Row(
                        children: [
                          Expanded(
                            child: Column(
                              children: [
                                CachedNetworkImage(
                                  imageUrl:
                                      '$appBaseUrl/${user.userIdentity.user_image}',
                                  imageBuilder: (context, imageProvider) =>
                                      Container(
                                    width: MediaQuery.of(context).size.width *
                                        15 /
                                        100,
                                    height: MediaQuery.of(context).size.width *
                                        15 /
                                        100,
                                    margin: const EdgeInsets.all(16),
                                    decoration: ShapeDecoration(
                                      shape: ContinuousRectangleBorder(
                                        borderRadius: BorderRadius.circular(
                                          MediaQuery.of(context).size.width *
                                              10 /
                                              100,
                                        ),
                                      ),
                                      image: DecorationImage(
                                          image: imageProvider,
                                          fit: BoxFit.cover),
                                    ),
                                  ),
                                ),
                                SizedBox(
                                  width: MediaQuery.of(context).size.width *
                                      30 /
                                      100,
                                  child: Text(
                                    user.userIdentity.user_name,
                                    style: context.textTheme.bodyMedium,
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                    textAlign: TextAlign.center,
                                  ),
                                )
                              ],
                            ),
                          ),
                          Expanded(
                            child: Column(
                              children: [
                                Container(
                                  width: MediaQuery.of(context).size.width *
                                      15 /
                                      100,
                                  height: MediaQuery.of(context).size.width *
                                      15 /
                                      100,
                                  margin: const EdgeInsets.all(16),
                                  decoration: ShapeDecoration(
                                    shape: ContinuousRectangleBorder(
                                      borderRadius: BorderRadius.circular(
                                        MediaQuery.of(context).size.width *
                                            10 /
                                            100,
                                      ),
                                    ),
                                    image: DecorationImage(
                                        image: AssetImage(
                                            mafiaList[index].characterImage),
                                        fit: BoxFit.cover),
                                  ),
                                ),
                                Text(
                                  mafiaList[index].characterName,
                                  style: context.textTheme.bodyMedium,
                                )
                              ],
                            ),
                          ),
                        ],
                      );
                    },
                  ),
                  const SliverToBoxAdapter(
                    child: SizedBox(
                      height: 16,
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

class InGameMsgDialog extends StatelessWidget {
  const InGameMsgDialog(
      {super.key, required this.msg, this.user, this.confirmable = false});
  final InGameUserModel? user;
  final String msg;
  final bool confirmable;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return PopScope(
      canPop: false,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: SizedBox(
          width: MediaQuery.of(context).size.width * 60 / 100,
          child: ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
              child: Container(
                color: const Color.fromRGBO(102, 102, 102, 0.5),
                padding: const EdgeInsets.all(8),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    user != null
                        ? CachedNetworkImage(
                            imageUrl:
                                '$appBaseUrl/${user!.userIdentity.user_image}',
                            imageBuilder: (context, imageProvider) => Container(
                              width:
                                  MediaQuery.of(context).size.width * 15 / 100,
                              height:
                                  MediaQuery.of(context).size.width * 15 / 100,
                              decoration: ShapeDecoration(
                                shape: ContinuousRectangleBorder(
                                  borderRadius: BorderRadius.circular(
                                    MediaQuery.of(context).size.width *
                                        10 /
                                        100,
                                  ),
                                ),
                                image: DecorationImage(
                                    image: imageProvider, fit: BoxFit.cover),
                              ),
                            ),
                            placeholder: (context, url) => Container(
                              width:
                                  MediaQuery.of(context).size.width * 15 / 100,
                              height:
                                  MediaQuery.of(context).size.width * 15 / 100,
                              decoration: ShapeDecoration(
                                  shape: ContinuousRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                      MediaQuery.of(context).size.width *
                                          10 /
                                          100,
                                    ),
                                  ),
                                  color: Colors.grey),
                            ),
                          )
                        : Image.asset(
                            'images/icon_in_game_msg.webp',
                            width: 70,
                            height: 70,
                          ),
                    const SizedBox(
                      height: 8,
                    ),
                    Directionality(
                      textDirection: TextDirection.rtl,
                      child: Text(
                        msg,
                        style: context.textTheme.bodyMedium,
                        textAlign: TextAlign.center,
                      ),
                    ),
                    const SizedBox(
                      height: 8,
                    ),
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

class NightIdle extends StatelessWidget {
  const NightIdle({super.key});
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SizedBox(
      height: MediaQuery.of(context).size.height * 35 / 100,
      child: Scaffold(
        backgroundColor: Colors.transparent,
        body: PopScope(
          canPop: false,
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              mainAxisSize: MainAxisSize.max,
              children: [
                Directionality(
                  textDirection: TextDirection.rtl,
                  child: Text(
                    'شب است و مافیا بیدار',
                    style: context.textTheme.bodyLarge,
                    textAlign: TextAlign.center,
                  ),
                ),
                LottieBuilder.asset(
                  'assets/night.json',
                  width: MediaQuery.of(context).size.width * 25 / 100,
                  height: MediaQuery.of(context).size.width * 25 / 100,
                ),
                Text(
                  'منتظر باش تا توی نوبتت بیدارشی',
                  style: context.textTheme.bodyMedium,
                  textAlign: TextAlign.center,
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class NightActTime extends StatefulWidget {
  const NightActTime(
      {super.key,
      required this.availableUses,
      required this.users,
      required this.maxCount,
      required this.timer,
      required this.serverCharacter,
      required this.isMafiaShot,
      required this.onTimeUp,
      required this.onSelectedUsersCallback,
      required this.controller});
  final List<dynamic> availableUses;
  final List<InGameUserModel> users;
  final int timer;
  final int maxCount;
  final String serverCharacter;
  final bool isMafiaShot;
  final Function() onTimeUp;
  final Function(List<NatoNightActModel> selectedUsers) onSelectedUsersCallback;
  final NatoScreenController controller;

  @override
  State<NightActTime> createState() => _NightActTimeState();
}

class _NightActTimeState extends State<NightActTime> {
  List<NatoNightActModel> selected =
      List<NatoNightActModel>.empty(growable: true);
  late List<InGameUserModel> availableUsers;
  int countLeft = 0;
  int realBulletLeft = 1;
  @override
  void initState() {
    // cast
    var castList = widget.availableUses.map((e) => e.toString()).toList();
    availableUsers = widget.users
        .where((element) => castList.contains(element.userIdentity.user_id))
        .toList();

    countLeft = widget.maxCount;

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    var leftUsers = widget.users
        .getRange(0, widget.users.length > 5 ? 5 : widget.users.length)
        .toList();
    var rightUsers = widget.users
        .getRange(widget.users.length > 5 ? 5 : 0,
            widget.users.length > 5 ? widget.users.length : 0)
        .toList();

    return SafeArea(
      child: Scaffold(
        backgroundColor: Colors.grey.shade900,
        body: PopScope(
          canPop: false,
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                Expanded(
                  flex: MediaQuery.of(context).size.height * 17 ~/ 100,
                  child: Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          CircularCountDownTimer(
                            width: MediaQuery.of(context).size.width * 12 / 100,
                            height:
                                MediaQuery.of(context).size.width * 12 / 100,
                            autoStart: true,
                            duration: widget.timer,
                            fillColor: Colors.cyan,
                            ringColor: Colors.transparent,
                            isReverse: true,
                            isReverseAnimation: true,
                            textStyle: context.textTheme.bodyMedium,
                            strokeCap: StrokeCap.round,
                            onComplete: () async {
                              widget.onTimeUp();
                            },
                          ),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                'اکت شب',
                                style: context.textTheme.bodyLarge,
                              ),
                              const SizedBox(
                                width: 16,
                              ),
                              Image.asset(
                                widget.isMafiaShot
                                    ? 'images/godfather.png'
                                    : 'images/${widget.serverCharacter}.png',
                                width: MediaQuery.of(context).size.width *
                                    10 /
                                    100,
                                height: MediaQuery.of(context).size.width *
                                    10 /
                                    100,
                              ),
                            ],
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                Expanded(
                  flex: MediaQuery.of(context).size.height * 80 ~/ 100,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      Expanded(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: List.generate(
                            leftUsers.length,
                            (index) => _playerModel(
                                index,
                                leftUsers[index],
                                1,
                                widget.controller.getMyDetails.userIdentity
                                    .user_id),
                          ),
                        ),
                      ),
                      Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Image.asset('images/citizen_hat.png',
                              width: 50, height: 50),
                          const SizedBox(
                            height: 8,
                          ),
                          Directionality(
                            textDirection: TextDirection.rtl,
                            child: Text(
                              '$countLeft حق انتخاب ',
                              style: context.textTheme.titleMedium,
                            ),
                          ),
                        ],
                      ),
                      Expanded(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: List.generate(
                            rightUsers.length,
                            (index) => _playerModel(
                                index,
                                rightUsers[index],
                                6,
                                widget.controller.getMyDetails.userIdentity
                                    .user_id),
                          ),
                        ),
                      )
                    ],
                  ),
                ),
                Expanded(
                  flex: MediaQuery.of(context).size.height * 20 ~/ 100,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      ...List.generate(
                        selected.length,
                        (index) => Column(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Row(
                              children: [
                                CachedNetworkImage(
                                  imageUrl:
                                      '$appBaseUrl/${selected[index].userImage}',
                                  imageBuilder: (context, imageProvider) =>
                                      Container(
                                    width: MediaQuery.of(context).size.height *
                                        6 /
                                        100,
                                    height: MediaQuery.of(context).size.height *
                                        6 /
                                        100,
                                    decoration: ShapeDecoration(
                                      shape: ContinuousRectangleBorder(
                                        borderRadius: BorderRadius.circular(
                                            MediaQuery.of(context).size.width *
                                                3 /
                                                100),
                                      ),
                                      image: DecorationImage(
                                          image: imageProvider,
                                          fit: BoxFit.cover),
                                    ),
                                  ),
                                ),
                                const SizedBox(
                                  width: 8,
                                ),
                                Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    IconButton(
                                      onPressed: () {
                                        if (selected[index].hasGun &&
                                            selected[index].gunKind ==
                                                'fighter') {
                                          realBulletLeft = 1;
                                        }
                                        setState(() {
                                          selected.removeAt(index);
                                          countLeft =
                                              widget.maxCount - selected.length;
                                        });
                                      },
                                      icon: const Icon(
                                        Icons.delete_rounded,
                                        color: Colors.grey,
                                      ),
                                    ),
                                    SizedBox(
                                      width: MediaQuery.of(context).size.width *
                                          12 /
                                          100,
                                      child: Text(
                                        selected[index].username,
                                        style: context.textTheme.bodySmall,
                                        maxLines: 1,
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                    ),
                                  ],
                                )
                              ],
                            ),
                          ],
                        ),
                      ),
                      const Spacer(),
                      ElevatedButton(
                        onPressed: () {
                          if (selected.isEmpty) {
                            return;
                          }
                          // callback
                          widget.onSelectedUsersCallback(selected);
                        },
                        style: ButtonStyle(
                            backgroundColor: selected.isEmpty
                                ? MaterialStatePropertyAll(Colors.grey.shade700)
                                : context.theme.elevatedButtonTheme.style
                                    ?.backgroundColor),
                        child: Text(
                          'تایید انتخاب',
                          style: TextStyle(
                              fontSize: 14.sp,
                              fontFamily: 'shabnam',
                              color: Colors.white),
                        ),
                      )
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _playerModel(
      int index, InGameUserModel user, int playerIndex, String selfUserId) {
    InGameUserModel? findUser = availableUsers.firstWhereOrNull(
        (element) => element.userIdentity.user_id == user.userIdentity.user_id);
    return InkWell(
      splashColor: Colors.transparent,
      onTap: () {
        if (findUser != null) {
          if (countLeft > 0) {
            if (selected.firstWhereOrNull(
                    (element) => element.userId == user.userIdentity.user_id) ==
                null) {
              // for rifleman
              if (widget.serverCharacter == 'rifleman') {
                Get.bottomSheet(
                  RifleManNightAct(
                    selfUserId: selfUserId,
                    selectedUserId: findUser.userIdentity.user_id,
                    realLeft: realBulletLeft,
                    callback: (fakeBullet, realBullet) {
                      // close open bs
                      Get.back();
                      if (realBullet) {
                        setState(() {
                          realBulletLeft = 0;
                          selected.add(
                            NatoNightActModel(
                                username: user.userIdentity.user_name,
                                userId: user.userIdentity.user_id,
                                userImage: user.userIdentity.user_image,
                                gunKind: 'fighter',
                                hasGun: true),
                          );
                          countLeft--;
                        });
                      }
                      if (fakeBullet) {
                        setState(() {
                          selected.add(
                            NatoNightActModel(
                                username: user.userIdentity.user_name,
                                userId: user.userIdentity.user_id,
                                userImage: user.userIdentity.user_image,
                                gunKind: 'lighter',
                                hasGun: true),
                          );
                          countLeft--;
                        });
                      }
                    },
                  ),
                );
                return;
              }

              // nato
              else if (widget.serverCharacter == 'nato') {
                Get.bottomSheet(NatoGuessNightAct(
                  callback: (character) {
                    setState(() {
                      selected.add(
                        NatoNightActModel(
                          username: user.userIdentity.user_name,
                          userId: user.userIdentity.user_id,
                          userImage: user.userIdentity.user_image,
                          guessCharacter: character,
                          natoAct: true,
                        ),
                      );
                    });
                    return;
                  },
                ));
                return;
              }
              // other users
              else {
                // left user
                setState(() {
                  countLeft--;
                });
              }
              setState(() {
                selected.add(
                  NatoNightActModel(
                    username: user.userIdentity.user_name,
                    userId: user.userIdentity.user_id,
                    userImage: user.userIdentity.user_image,
                  ),
                );
              });
            } else {
              BotToast.showText(
                  text: 'قبلا انتخاب شده',
                  textStyle: context.textTheme.bodyMedium!);
            }
          } else {
            BotToast.showText(
                text: 'نمیتونی بیشتر انتخاب کنی',
                textStyle: context.textTheme.bodyMedium!);
          }
        } else {
          BotToast.showText(
              text: 'این بازیکن مجاز نیست',
              textStyle: context.textTheme.bodyMedium!);
        }
      },
      child: Column(
        children: [
          Text(
            '${index + playerIndex}',
            style: context.textTheme.titleSmall,
          ),
          const SizedBox(
            height: 2,
          ),
          Hero(
            tag: user.userIdentity.user_id,
            child: CachedNetworkImage(
              imageUrl: '$appBaseUrl/${user.userIdentity.user_image}',
              imageBuilder: (context, imageProvider) => Container(
                width: MediaQuery.of(context).size.height * 6 / 100,
                height: MediaQuery.of(context).size.height * 6 / 100,
                decoration: ShapeDecoration(
                  shape: ContinuousRectangleBorder(
                    borderRadius: BorderRadius.circular(
                        MediaQuery.of(context).size.height * 3 / 100),
                    side: BorderSide(
                        width: 2,
                        color: findUser == null ? Colors.red : Colors.cyan),
                  ),
                  image:
                      DecorationImage(image: imageProvider, fit: BoxFit.cover),
                ),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.only(top: 2.0),
            child: SizedBox(
              width: MediaQuery.of(context).size.height * 7 / 100,
              child: Text(
                user.userIdentity.user_name,
                style: context.textTheme.bodySmall,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                textAlign: TextAlign.center,
              ),
            ),
          )
        ],
      ),
    );
  }
}

class MafiaDecision extends StatefulWidget {
  const MafiaDecision(
      {super.key,
      required this.timer,
      required this.onCallback,
      required this.onTimeUp});
  final int timer;
  final Function(bool onNato, bool onShot) onCallback;
  final Function() onTimeUp;

  @override
  State<MafiaDecision> createState() => _MafiaDecisionState();
}

class _MafiaDecisionState extends State<MafiaDecision> {
  bool nato = false;
  bool shot = false;
  late int timeLeft;
  late Timer timer;
  @override
  void initState() {
    super.initState();
    timeLeft = widget.timer;
    timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (timeLeft == 0) {
        timer.cancel();
        // time up callback
        widget.onTimeUp();
      } else {
        setState(() {
          timeLeft--;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);

    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Center(
        child: FittedBox(
          child: ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
              child: Column(
                children: [
                  Row(
                    children: [
                      Container(
                        padding: const EdgeInsets.all(8),
                        width: MediaQuery.of(context).size.width * 15 / 100,
                        height: MediaQuery.of(context).size.width * 15 / 100,
                        decoration: ShapeDecoration(
                          shape: ContinuousRectangleBorder(
                              borderRadius: BorderRadius.circular(
                                MediaQuery.of(context).size.width * 9 / 100,
                              ),
                              side: const BorderSide(
                                  width: 2, color: Colors.grey)),
                        ),
                        child: Image.asset('images/godfather.png'),
                      ),
                      const SizedBox(
                        width: 8,
                      ),
                      Container(
                        padding: const EdgeInsets.all(8),
                        width: MediaQuery.of(context).size.width * 15 / 100,
                        height: MediaQuery.of(context).size.width * 15 / 100,
                        decoration: ShapeDecoration(
                          shape: ContinuousRectangleBorder(
                              borderRadius: BorderRadius.circular(
                                MediaQuery.of(context).size.width * 9 / 100,
                              ),
                              side: const BorderSide(
                                  width: 2, color: Colors.grey)),
                        ),
                        child: Image.asset('images/nato.png'),
                      )
                    ],
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Container(
                    width: MediaQuery.of(context).size.width * 60 / 100,
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(16),
                      color: Colors.white30,
                    ),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Directionality(
                          textDirection: TextDirection.rtl,
                          child: Text(
                            'تصمیم پدرخوانده چیه؟',
                            style: context.textTheme.bodyMedium,
                            textAlign: TextAlign.center,
                          ),
                        ),
                        const SizedBox(
                          height: 16,
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Column(
                              children: [
                                Text(
                                  'ناتویی',
                                  style: context.textTheme.titleMedium,
                                ),
                                InkWell(
                                  onTap: () {
                                    setState(() {
                                      nato = !nato;
                                      shot = false;
                                    });
                                  },
                                  borderRadius: BorderRadius.circular(
                                    MediaQuery.of(context).size.width *
                                        10 /
                                        100,
                                  ),
                                  child: Container(
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
                                              100,
                                        ),
                                      ),
                                      color: nato
                                          ? Colors.cyan.shade700
                                          : Colors.grey.shade700,
                                    ),
                                    child: Image.asset('images/nato.png'),
                                  ),
                                )
                              ],
                            ),
                            const SizedBox(
                              width: 16,
                            ),
                            Column(
                              children: [
                                Text(
                                  'شات',
                                  style: context.textTheme.titleMedium,
                                ),
                                InkWell(
                                  onTap: () {
                                    setState(() {
                                      shot = !shot;
                                      nato = false;
                                    });
                                  },
                                  borderRadius: BorderRadius.circular(
                                    MediaQuery.of(context).size.width *
                                        10 /
                                        100,
                                  ),
                                  child: Container(
                                    padding: const EdgeInsets.all(8),
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
                                              100,
                                        ),
                                      ),
                                      color: shot
                                          ? Colors.cyan.shade700
                                          : Colors.grey.shade700,
                                    ),
                                    child: Image.asset('images/godfather.png'),
                                  ),
                                )
                              ],
                            )
                          ],
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        Directionality(
                          textDirection: TextDirection.rtl,
                          child: Text(
                            '$timeLeft ثانیه تا اتمام تصمیم',
                            style: context.textTheme.titleSmall,
                          ),
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        ElevatedButton(
                          onPressed: () {
                            if (!nato && !shot) {
                              return;
                            }
                            widget.onCallback(nato, shot);
                            timer.cancel();
                          },
                          style: ButtonStyle(
                            backgroundColor: shot || nato
                                ? MaterialStatePropertyAll(Colors.cyan.shade700)
                                : MaterialStatePropertyAll(
                                    Colors.grey.shade700),
                          ),
                          child: Text('تایید',
                              style: context.textTheme.bodyMedium),
                        )
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class NatoGuessNightAct extends StatelessWidget {
  const NatoGuessNightAct({super.key, required this.callback});
  final Function(String character) callback;
  @override
  Widget build(BuildContext context) {
    List<String> characterImages = [
      'doctor.png',
      'detective.png',
      'rifleman.png',
      'commando.png',
      'guard.png'
    ];

    List<String> characters = [
      'doctor',
      'detective',
      'rifleman',
      'commando',
      'guard'
    ];
    ScreenUtil.init(context);
    return SizedBox(
      height: MediaQuery.of(context).size.height * 38 / 100,
      child: Scaffold(
        backgroundColor: Colors.transparent,
        body: Container(
          decoration: const BoxDecoration(
              borderRadius: BorderRadius.only(
                topLeft: Radius.circular(16),
                topRight: Radius.circular(16),
              ),
              color: Colors.white12),
          child: ClipRRect(
            borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(16),
              topRight: Radius.circular(16),
            ),
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 10, sigmaY: 10),
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: [
                    Text(
                      'از بین گزینه های زیر انتخاب کن',
                      style: context.textTheme.bodyMedium,
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    Expanded(
                      child: GridView.builder(
                        gridDelegate:
                            const SliverGridDelegateWithFixedCrossAxisCount(
                                crossAxisCount: 3),
                        itemBuilder: (context, index) => SizedBox(
                          child: Center(
                            child: InkWell(
                              onTap: () {
                                if (Get.isBottomSheetOpen == true) {
                                  Get.back();
                                }
                                callback(characters[index]);
                              },
                              child: Container(
                                padding: const EdgeInsets.all(8),
                                width: MediaQuery.of(context).size.width *
                                    20 /
                                    100,
                                decoration: ShapeDecoration(
                                  shape: ContinuousRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        MediaQuery.of(context).size.width *
                                            10 /
                                            100),
                                    side: BorderSide(
                                        width: 2, color: Colors.cyan.shade800),
                                  ),
                                ),
                                height: MediaQuery.of(context).size.width *
                                    20 /
                                    100,
                                child: Image.asset(
                                    'images/${characterImages[index]}'),
                              ),
                            ),
                          ),
                        ),
                        itemCount: 5,
                      ),
                    ),
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

class RifleManNightAct extends StatefulWidget {
  const RifleManNightAct(
      {super.key,
      required this.realLeft,
      required this.callback,
      required this.selfUserId,
      required this.selectedUserId});
  final Function(
    bool fakeBullet,
    bool realBullet,
  ) callback;
  final int realLeft;
  final String selfUserId;
  final String selectedUserId;
  @override
  State<RifleManNightAct> createState() => _RifleManNightActState();
}

class _RifleManNightActState extends State<RifleManNightAct> {
  bool fake = false;
  bool real = false;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SizedBox(
      height: MediaQuery.of(context).size.height * 35 / 100,
      child: Scaffold(
        backgroundColor: Colors.transparent,
        body: Container(
          decoration: const BoxDecoration(
              borderRadius: BorderRadius.only(
                topLeft: Radius.circular(16),
                topRight: Radius.circular(16),
              ),
              color: Colors.white12),
          child: ClipRRect(
            borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(16),
              topRight: Radius.circular(16),
            ),
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 10, sigmaY: 10),
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  mainAxisSize: MainAxisSize.max,
                  children: [
                    Text(
                      'تفنگدار چه تیری به بازیکن میدی ؟',
                      style: context.textTheme.bodyMedium,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Expanded(
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Text(
                                'تیر جنگی',
                                style: context.textTheme.titleSmall,
                              ),
                              const SizedBox(
                                height: 8.0,
                              ),
                              InkWell(
                                borderRadius: BorderRadius.circular(
                                    MediaQuery.of(context).size.width *
                                        15 /
                                        100),
                                onTap: () {
                                  if (widget.selfUserId ==
                                      widget.selectedUserId) {
                                    BotToast.showText(
                                        text: 'به خودت نمیتونی تیر جنگی بدی');
                                    return;
                                  }
                                  if (widget.realLeft > 0) {
                                    setState(() {
                                      real = !real;
                                      fake = false;
                                    });
                                  } else {
                                    BotToast.showText(
                                        text: 'تیر جنگیت تموم شده',
                                        textStyle:
                                            context.textTheme.bodyMedium!);
                                  }
                                },
                                child: Container(
                                  width: MediaQuery.of(context).size.width *
                                      15 /
                                      100,
                                  height: MediaQuery.of(context).size.width *
                                      15 /
                                      100,
                                  padding: const EdgeInsets.all(8),
                                  decoration: ShapeDecoration(
                                      shape: ContinuousRectangleBorder(
                                        borderRadius: BorderRadius.circular(
                                            MediaQuery.of(context).size.width *
                                                9 /
                                                100),
                                        side: BorderSide(
                                            width: 2,
                                            color: Colors.cyan.shade800),
                                      ),
                                      color: real
                                          ? Colors.cyan.shade800
                                          : Colors.transparent),
                                  child: Image.asset('images/real_bullet.png'),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(
                            width: 20.0,
                          ),
                          Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Text(
                                'تیر مشقی',
                                style: context.textTheme.titleSmall,
                              ),
                              const SizedBox(
                                height: 8.0,
                              ),
                              InkWell(
                                borderRadius: BorderRadius.circular(
                                    MediaQuery.of(context).size.width *
                                        15 /
                                        100),
                                onTap: () {
                                  setState(() {
                                    fake = !fake;
                                    real = false;
                                  });
                                },
                                child: Container(
                                  width: MediaQuery.of(context).size.width *
                                      15 /
                                      100,
                                  height: MediaQuery.of(context).size.width *
                                      15 /
                                      100,
                                  padding: const EdgeInsets.all(8),
                                  decoration: ShapeDecoration(
                                      shape: ContinuousRectangleBorder(
                                        borderRadius: BorderRadius.circular(
                                            MediaQuery.of(context).size.width *
                                                9 /
                                                100),
                                        side: BorderSide(
                                            width: 2,
                                            color: Colors.cyan.shade800),
                                      ),
                                      color: fake
                                          ? Colors.cyan.shade800
                                          : Colors.transparent),
                                  child: Image.asset('images/fake_bullet.png'),
                                ),
                              ),
                            ],
                          )
                        ],
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.only(bottom: 20),
                      child: ElevatedButton(
                        onPressed: () {
                          if (!fake && !real) {
                            return;
                          }
                          // callback
                          widget.callback(fake, real);
                        },
                        style: ButtonStyle(
                          backgroundColor: fake || real
                              ? const MaterialStatePropertyAll(Colors.cyan)
                              : MaterialStatePropertyAll(Colors.grey.shade700),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Text(
                            'تایید',
                            style: context.textTheme.bodyMedium,
                          ),
                        ),
                      ),
                    ),
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

class RequestSpeechOptions extends StatefulWidget {
  const RequestSpeechOptions(
      {super.key,
      required this.timer,
      required this.accepted,
      required this.denied});
  final Function() denied;
  final Function() accepted;
  final int timer;
  @override
  State<RequestSpeechOptions> createState() => _RequestSpeechOptionsState();
}

class _RequestSpeechOptionsState extends State<RequestSpeechOptions> {
  late int timeLeft;
  late Timer timer;
  @override
  void initState() {
    super.initState();
    timeLeft = widget.timer;
    timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (timeLeft == 0) {
        timer.cancel();
        widget.denied();
      } else {
        setState(() {
          timeLeft--;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: PopScope(
        canPop: false,
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
                            Directionality(
                              textDirection: TextDirection.rtl,
                              child: Text(
                                'آیا درخواست تارگت کاور داری ؟',
                                style: context.textTheme.bodyMedium,
                                textAlign: TextAlign.center,
                              ),
                            ),
                            const SizedBox(
                              height: 16,
                            ),
                            Text(
                              'اتمام تا $timeLeft ثانیه دیگه',
                              style: context.textTheme.bodySmall,
                            ),
                            const SizedBox(
                              height: 16,
                            ),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                ElevatedButton(
                                    onPressed: () {
                                      widget.denied();
                                    },
                                    child: Text(
                                      'نه مرسی',
                                      style: context.textTheme.bodyMedium,
                                    )),
                                const SizedBox(
                                  width: 16,
                                ),
                                ElevatedButton(
                                    onPressed: () {
                                      widget.accepted();
                                    },
                                    child: Text(
                                      'اره حتما',
                                      style: context.textTheme.bodyMedium,
                                    ))
                              ],
                            ),
                            const SizedBox(
                              height: 16,
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

  @override
  void dispose() {
    timer.cancel();
    super.dispose();
  }
}

class DetectiveInquiryResponse extends StatelessWidget {
  const DetectiveInquiryResponse({super.key, required this.result});
  final bool result;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: PopScope(
        canPop: false,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const SizedBox(
              width: double.infinity,
            ),
            Image.asset(
              'images/img_msg.webp',
              width: 120,
              height: 120,
            ),
            FittedBox(
              child: ClipRRect(
                borderRadius: BorderRadius.circular(16),
                child: BackdropFilter(
                  filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
                  child: Column(
                    children: [
                      Container(
                        width: MediaQuery.of(context).size.width * 60 / 100,
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(16),
                          color: Colors.white30,
                        ),
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            const SizedBox(
                              height: 10,
                            ),
                            Directionality(
                              textDirection: TextDirection.rtl,
                              child: Text(
                                result
                                    ? 'کاراگاه جواب استعلامت مثبته'
                                    : 'کاراگاه جواب استعلامت منفیه',
                                style: context.textTheme.bodyMedium,
                                textAlign: TextAlign.center,
                              ),
                            ),
                            const SizedBox(
                              height: 16,
                            ),
                            Visibility(
                              visible: result,
                              child: Lottie.asset('assets/anim_like.json',
                                  height: MediaQuery.of(context).size.height *
                                      20 /
                                      100),
                            ),
                            Visibility(
                              visible: !result,
                              child: Transform.flip(
                                flipY: true,
                                child: Lottie.asset('assets/anim_like.json',
                                    height: MediaQuery.of(context).size.height *
                                        20 /
                                        100),
                              ),
                            ),
                            ElevatedButton(
                              onPressed: () {
                                Get.back();
                              },
                              child: Text(
                                'مرسی متوجه شدم',
                                style: context.textTheme.bodyMedium,
                              ),
                            ),
                            const SizedBox(
                              height: 16,
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

class AnnounceNotificationMessage extends StatelessWidget {
  const AnnounceNotificationMessage(
      {super.key, required this.msg, required this.img});
  final String? img;
  final String msg;

  @override
  Widget build(BuildContext context) {
    return Padding(
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
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Visibility(
                  visible: img != null ? true : false,
                  child: Image.asset(
                    '$img',
                    width: 50,
                    height: 50,
                  ),
                ),
                Directionality(
                  textDirection: TextDirection.rtl,
                  child: Text(
                    msg,
                    style: context.textTheme.bodyMedium,
                    textAlign: TextAlign.justify,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class EndGameDialog extends StatelessWidget {
  const EndGameDialog({super.key, required this.mafia, required this.callback});
  final bool mafia;
  final Function(bool exit, bool leaderBoard) callback;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: PopScope(
        canPop: false,
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
                        width: MediaQuery.of(context).size.width * 65 / 100,
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(16),
                          color: Colors.white30,
                        ),
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Image.asset(
                              mafia
                                  ? 'images/godfather.png'
                                  : 'images/citizen.png',
                              width: 75,
                              height: 75,
                            ),
                            const SizedBox(
                              height: 16,
                            ),
                            Text(
                              mafia
                                  ? 'تیم مافیا بازی رو برد'
                                  : 'تیم شهر بازی رو برد',
                              style: context.textTheme.bodyLarge,
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
                                      callback(true, false);
                                    },
                                    child: Text(
                                      'خروج',
                                      style: context.textTheme.bodyMedium,
                                    )),
                                ElevatedButton(
                                    onPressed: () {
                                      callback(false, true);
                                    },
                                    child: Text('امتیازات',
                                        style: context.textTheme.bodyMedium)),
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
