// ignore_for_file: deprecated_member_use, non_constant_identifier_names

import 'dart:ui';

import 'package:bot_toast/bot_toast.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:card_swiper/card_swiper.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_pannable_rating_bar/flutter_pannable_rating_bar.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:lottie/lottie.dart';
import 'package:mafia/models/lobby/game/lobby_game_event_enum.dart';
import 'package:mafia/models/lobby/game/lobby_game_modified_permission_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_observer_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_over_all_permission_callback_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_over_all_permissions_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_player_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_player_permissions_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_screen_creator_message_model.dart';
import 'package:mafia/screens/lobby/game/lobby_game_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';

class LobbyGameScreen extends GetView<LobbyGameScreenController> {
  const LobbyGameScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return WillPopScope(
      onWillPop: () async => false,
      child: SafeArea(
        child: Scaffold(
          resizeToAvoidBottomInset: false,
          body: GetBuilder<LobbyGameScreenController>(
            id: 'game_started',
            builder: (_) => !controller.gameStarted
                ? Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'دریافت اطلاعات بازی',
                          style: context.textTheme.bodyLarge,
                        ),
                        const SizedBox(
                          height: 16,
                        ),
                        LoadingAnimationWidget.prograssiveDots(
                            color: Colors.white, size: 30)
                      ],
                    ),
                  )
                : Column(
                    children: [
                      Expanded(
                          flex: MediaQuery.of(context).size.height * 10 ~/ 100,
                          child: _header(context, controller)),
                      Expanded(
                          flex: MediaQuery.of(context).size.height * 80 ~/ 100,
                          child: _middle()),
                      Expanded(
                          flex: MediaQuery.of(context).size.height * 10 ~/ 100,
                          child: _bottom(context)),
                    ],
                  ),
          ),
        ),
      ),
    );
  }

  Widget _header(BuildContext context, LobbyGameScreenController controller) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Row(
          children: [
            const SizedBox(
              width: 8,
            ),
            GetBuilder<LobbyGameScreenController>(
              id: 'player_type',
              builder: (controller) => controller.isObserver
                  ? Container(
                      width: MediaQuery.of(context).size.width * 13 / 100,
                      height: MediaQuery.of(context).size.width * 13 / 100,
                      decoration: ShapeDecoration(
                        shape: ContinuousRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              MediaQuery.of(context).size.width * 7 / 100),
                        ),
                      ),
                    )
                  // user image
                  : CachedNetworkImage(
                      imageUrl:
                          '$appBaseUrl/${controller.isPlayer ? controller.selfDetail().avatar : controller.creatorModel?.avatar}',
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
                      placeholder: (context, url) => Container(
                        width: MediaQuery.of(context).size.width * 13 / 100,
                        height: MediaQuery.of(context).size.width * 13 / 100,
                        decoration: ShapeDecoration(
                          color: Colors.grey,
                          shape: ContinuousRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                MediaQuery.of(context).size.width * 7 / 100),
                          ),
                        ),
                      ),
                    ),
            ),
            const SizedBox(
              width: 4,
            ),
            GetBuilder<LobbyGameScreenController>(
              id: 'player_type',
              builder: (_) => controller.isObserver || controller.isCreator
                  ? const Center(
                      child: Text('-'),
                    )
                  : GetBuilder<LobbyGameScreenController>(
                      id: 'player_character',
                      builder: (controller) => Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          SizedBox(
                            width: MediaQuery.of(context).size.width * 13 / 100,
                            child: Text(
                              '${controller.character}',
                              style: context.textTheme.bodyMedium,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                          SizedBox(
                            width: MediaQuery.of(context).size.width * 13 / 100,
                            child: Text(
                              controller.side == null
                                  ? '${controller.side}'
                                  : controller.side.toString(),
                              style: context.textTheme.bodyMedium,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                        ],
                      ),
                    ),
            )
          ],
        ),
        Row(
          children: [
            GetBuilder<LobbyGameScreenController>(
              id: 'observer_count',
              builder: (controller) => Text(
                '${controller.observers.length}',
                style: context.textTheme.bodyMedium,
              ),
            ),
            const SizedBox(
              width: 4,
            ),
            IconButton(
                onPressed: () {
                  showModalBottomSheet(
                      context: context,
                      builder: (context) =>
                          ObserverList(observers: controller.observers),
                      isScrollControlled: true,
                      backgroundColor: Colors.transparent);
                },
                icon: const Icon(
                  Icons.visibility_rounded,
                  color: Colors.white,
                )),
            GetBuilder<LobbyGameScreenController>(
              id: 'player_type',
              builder: (_) => Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  const SizedBox(
                    width: 8,
                  ),
                  Visibility(
                    visible: controller.isCreator,
                    child: IconButton(
                      splashRadius: 1,
                      onPressed: () {
                        // clear unread messages
                        controller.clearCreatorUnreadMessages();
                        // show bs
                        showModalBottomSheet(
                          backgroundColor: Colors.transparent,
                          isScrollControlled: true,
                          context: context,
                          builder: (context) => const CreatorMessageBox(),
                        );
                      },
                      icon: GetBuilder<LobbyGameScreenController>(
                        id: 'unread_message',
                        builder: (controller) =>
                            controller.moderatorUnreadMessage
                                ? const Icon(
                                    Icons.mark_email_unread_rounded,
                                    color: Colors.red,
                                  )
                                : const Icon(
                                    Icons.mail_rounded,
                                    color: Colors.white,
                                  ),
                      ),
                    ),
                  ),
                  const SizedBox(
                    width: 8,
                  ),
                  IconButton(
                    splashRadius: 1,
                    tooltip: 'خروج ',
                    onPressed: () {
                      Get.dialog(
                        DialogExitGame(
                          dcCallback: () {
                            Get.back();
                            controller.dc();
                          },
                          leaveCallback: () {
                            Get.back();
                            controller.leaveGame();
                          },
                        ),
                      );
                    },
                    icon: const Icon(Icons.logout_rounded, color: Colors.white),
                  )
                ],
              ),
            ),
          ],
        )
      ],
    );
  }

  Widget _middle() {
    return GetBuilder<LobbyGameScreenController>(
      id: 'player_type',
      builder: (controller) => controller.isPlayer || controller.isObserver
          ? const MiddleScreenPlayer()
          : const MiddleScreenCreator(),
    );
  }

  Widget _bottom(BuildContext context) {
    return GetBuilder<LobbyGameScreenController>(
      id: 'action_panel',
      builder: (controller) => controller.isObserver
          ? Center(
              child: Text(
                'شما به عنوان بیننده وارد شدی',
                style: context.textTheme.bodyMedium,
              ),
            )
          : controller.isPlayer
              ?
              // player bottom permissions
              const BottomScreenPlayer()
              :
              // creator bottom permissions
              BottomScreenCreator(controller: controller),
    );
  }
}

class MiddleScreenPlayer extends StatelessWidget {
  const MiddleScreenPlayer({super.key});

  final int widthSize = 15;
  final int cropSide = 10;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      body: Swiper(
        itemCount: 2,
        autoplay: false,
        loop: false,
        itemBuilder: (context, index) => index == 0
            ? Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Container(
                    padding: const EdgeInsets.all(10),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(color: Colors.grey),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        GetBuilder<LobbyGameScreenController>(
                            id: 'gameEventCount',
                            builder: (controller) =>
                                Text('${controller.gameEventCount}')),
                        const SizedBox(
                          width: 4,
                        ),
                        GetBuilder<LobbyGameScreenController>(
                          id: 'game_event',
                          builder: (controller) {
                            if (controller.gameEvent == null) {
                              return Text(
                                '-',
                                style: context.textTheme.bodyMedium,
                              );
                            } else if (controller.gameEvent ==
                                LobbyGameEvent.day) {
                              return const Icon(
                                Icons.light_mode,
                                color: Colors.white,
                              );
                            } else if (controller.gameEvent ==
                                LobbyGameEvent.night) {
                              return const Icon(
                                Icons.dark_mode_rounded,
                                color: Colors.white,
                              );
                            } else if (controller.gameEvent ==
                                LobbyGameEvent.vote) {
                              return const Icon(
                                Icons.how_to_vote_rounded,
                                color: Colors.white,
                              );
                            } else {
                              return const Icon(
                                Icons.workspaces_rounded,
                                color: Colors.white,
                              );
                            }
                          },
                        ),
                      ],
                    ),
                  ),
                  const Padding(
                    padding: EdgeInsets.only(right: 16.0),
                    child: Align(
                      alignment: Alignment.centerRight,
                      child: Icon(
                        Icons.swipe_right_rounded,
                        color: Colors.white,
                      ),
                    ),
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  _creator(context),
                  const SizedBox(
                    height: 8,
                  ),
                  Expanded(
                    child: GetBuilder<LobbyGameScreenController>(
                      id: 'players',
                      builder: (controller) => GridView.builder(
                        itemCount: controller.players.length,
                        gridDelegate:
                            const SliverGridDelegateWithFixedCrossAxisCount(
                                crossAxisCount: 3),
                        itemBuilder: (context, index) => Center(
                          child: GetBuilder<LobbyGameScreenController>(
                            id: controller.players[index].user_id,
                            builder: (controller) => GestureDetector(
                              onTap: () {
                                if (controller.isPlayer ||
                                    controller.isObserver) {
                                  controller.checkPlayerProfile(
                                      controller.players[index].user_id);
                                }
                              },
                              child:
                                  _players(controller.players[index], context),
                            ),
                          ),
                        ),
                      ),
                    ),
                  )
                ],
              )
            : const PlayerActs(),
      ),
    );
  }

  Widget _creator(BuildContext context) {
    return GetBuilder<LobbyGameScreenController>(
      id: 'creator',
      builder: (controller) => controller.creatorModel == null
          ? const Center()
          : Column(
              children: [
                Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    AnimatedOpacity(
                      opacity: controller.creatorModel!.private ? 1 : 0,
                      duration: const Duration(milliseconds: 250),
                      child: const Icon(
                        Icons.mic_rounded,
                        color: Colors.red,
                      ),
                    ),
                    const SizedBox(
                      width: 4,
                    ),
                    Column(
                      children: [
                        Stack(
                          children: [
                            CachedNetworkImage(
                              imageUrl:
                                  '$appBaseUrl/${controller.creatorModel?.avatar}',
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
                                            9 /
                                            100),
                                  ),
                                  image: DecorationImage(
                                      image: imageProvider, fit: BoxFit.cover),
                                ),
                              ),
                              placeholder: (context, url) => Container(
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
                                              9 /
                                              100),
                                    ),
                                    color: Colors.grey),
                              ),
                            ),
                            Visibility(
                              visible:
                                  controller.creatorModel?.connected == false,
                              child: Container(
                                width: MediaQuery.of(context).size.width *
                                    widthSize /
                                    100,
                                height: MediaQuery.of(context).size.width *
                                    widthSize /
                                    100,
                                decoration: ShapeDecoration(
                                  shape: ContinuousRectangleBorder(
                                      borderRadius: BorderRadius.circular(
                                          MediaQuery.of(context).size.width *
                                              cropSide /
                                              100),
                                      side: const BorderSide(
                                          width: 1, color: Colors.grey)),
                                ),
                                child: Container(
                                  decoration: ShapeDecoration(
                                    color: Colors.grey.shade900,
                                    shape: ContinuousRectangleBorder(
                                        borderRadius: BorderRadius.circular(
                                            MediaQuery.of(context).size.width *
                                                cropSide /
                                                100),
                                        side: const BorderSide(
                                            width: 1, color: Colors.grey)),
                                  ),
                                  child: const Icon(
                                    Icons.wifi_off_rounded,
                                    color: Colors.white,
                                  ),
                                ),
                              ),
                            )
                          ],
                        ),
                        Text('${controller.creatorModel?.name}')
                      ],
                    ),
                    AnimatedOpacity(
                      opacity: controller.creatorModel!.speech &&
                              controller.creatorModel!.private == false
                          ? 1
                          : 0.3,
                      duration: const Duration(milliseconds: 250),
                      child: const Icon(
                        Icons.mic_rounded,
                        color: Colors.white,
                      ),
                    ),
                  ],
                ),
              ],
            ),
    );
  }

  Widget _players(LobbyGamePlayerModel model, BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          model.user_index.toString(),
          style: context.textTheme.bodyMedium,
        ),
        const SizedBox(
          width: 4,
        ),
        Column(
          children: [
            Stack(
              children: [
                // online player
                CachedNetworkImage(
                  imageUrl: '$appBaseUrl/${model.avatar}',
                  imageBuilder: (context, imageProvider) => Container(
                    width: MediaQuery.of(context).size.width * widthSize / 100,
                    height: MediaQuery.of(context).size.width * widthSize / 100,
                    decoration: ShapeDecoration(
                      shape: ContinuousRectangleBorder(
                        borderRadius: BorderRadius.circular(
                            MediaQuery.of(context).size.width * cropSide / 100),
                      ),
                      image: DecorationImage(
                          image: imageProvider, fit: BoxFit.cover),
                    ),
                  ),
                ),
                // dc
                _dc(model, context),
                // dead
                _dead(model, context),

                // like
                _like(model, context),
                // dislike
                _disLike(model, context),
                // hand rise
                _hand(model, context),
                // challenge
                _challenge(model, context),
                //day act
                _dayAct(model, context)
              ],
            ),
            _playerName(model.name, context),
          ],
        ),
        AnimatedOpacity(
          opacity: model.status.speech && model.status.private == false ? 1 : 0,
          duration: const Duration(milliseconds: 250),
          child: const Icon(
            Icons.mic_rounded,
            color: Colors.white,
          ),
        )
      ],
    );
  }

  _challenge(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.challenge ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Container(
        width: MediaQuery.of(context).size.width * widthSize / 100,
        height: MediaQuery.of(context).size.width * widthSize / 100,
        decoration: ShapeDecoration(
          color: Colors.grey.shade900,
          shape: ContinuousRectangleBorder(
            borderRadius: BorderRadius.circular(
                MediaQuery.of(context).size.width * cropSide / 100),
            side: const BorderSide(width: 1, color: Colors.grey),
          ),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.bolt_rounded,
              color: Colors.white,
            ),
            Text(
              'چالش',
              style: context.textTheme.bodyMedium,
            )
          ],
        ),
      ),
    );
  }

  _disLike(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.dislike ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Visibility(
        visible: model.status.dislike,
        child: Container(
          width: MediaQuery.of(context).size.width * widthSize / 100,
          height: MediaQuery.of(context).size.width * widthSize / 100,
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey),
            ),
          ),
          child: Transform.flip(
            flipY: true,
            child: LottieBuilder.asset(
              'assets/anim_like.json',
              repeat: false,
              animate: model.status.dislike,
            ),
          ),
        ),
      ),
    );
  }

  _hand(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.hand_rise ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Container(
          width: MediaQuery.of(context).size.width * widthSize / 100,
          height: MediaQuery.of(context).size.width * widthSize / 100,
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey),
            ),
          ),
          child: const Center(
            child: Icon(
              Icons.front_hand_rounded,
              color: Colors.white,
            ),
          )),
    );
  }

  _like(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.like ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Visibility(
        visible: model.status.like,
        child: Container(
          width: MediaQuery.of(context).size.width * widthSize / 100,
          height: MediaQuery.of(context).size.width * widthSize / 100,
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey),
            ),
          ),
          child: LottieBuilder.asset(
            'assets/anim_like.json',
            repeat: false,
            animate: model.status.like,
          ),
        ),
      ),
    );
  }

  _playerName(name, context) {
    return SizedBox(
      width: MediaQuery.of(context).size.width * widthSize / 100,
      child: Text(
        textDirection: TextDirection.rtl,
        textAlign: TextAlign.center,
        '$name',
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
    );
  }

  _dead(LobbyGamePlayerModel model, BuildContext context) {
    return Visibility(
      visible: !model.status.alive,
      child: Container(
        width: MediaQuery.of(context).size.width * widthSize / 100,
        height: MediaQuery.of(context).size.width * widthSize / 100,
        decoration: ShapeDecoration(
          shape: ContinuousRectangleBorder(
            borderRadius: BorderRadius.circular(
                MediaQuery.of(context).size.width * cropSide / 100),
          ),
          image: const DecorationImage(
              image: AssetImage('images/img_blood.png'), fit: BoxFit.cover),
        ),
      ),
    );
  }

  _dayAct(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.day_act ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Container(
          width: MediaQuery.of(context).size.width * widthSize / 100,
          height: MediaQuery.of(context).size.width * widthSize / 100,
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey),
            ),
          ),
          child: const Center(
            child: Icon(
              Icons.online_prediction_rounded,
              color: Colors.white,
            ),
          )),
    );
  }

  _dc(LobbyGamePlayerModel model, BuildContext context) {
    return Visibility(
      visible: !model.status.connected,
      child: Container(
        width: MediaQuery.of(context).size.width * widthSize / 100,
        height: MediaQuery.of(context).size.width * widthSize / 100,
        decoration: ShapeDecoration(
          shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey)),
        ),
        child: Container(
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
                borderRadius: BorderRadius.circular(
                    MediaQuery.of(context).size.width * cropSide / 100),
                side: const BorderSide(width: 1, color: Colors.grey)),
          ),
          child: const Icon(
            Icons.wifi_off_rounded,
            color: Colors.white,
          ),
        ),
      ),
    );
  }
}

class MiddleScreenCreator extends StatelessWidget {
  const MiddleScreenCreator({super.key});

  final int widthSize = 15;
  final int cropSide = 10;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      body: Swiper(
        loop: false,
        itemCount: 2,
        autoplay: false,
        itemBuilder: (context, index) => index == 0
            ? Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Container(
                    padding: const EdgeInsets.all(10),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(color: Colors.grey),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        GetBuilder<LobbyGameScreenController>(
                            id: 'gameEventCount',
                            builder: (controller) =>
                                Text('${controller.gameEventCount}')),
                        const SizedBox(
                          width: 4,
                        ),
                        GetBuilder<LobbyGameScreenController>(
                          id: 'game_event',
                          builder: (controller) {
                            if (controller.gameEvent == null) {
                              return Text(
                                '-',
                                style: context.textTheme.bodyMedium,
                              );
                            } else if (controller.gameEvent ==
                                LobbyGameEvent.day) {
                              return const Icon(
                                Icons.light_mode,
                                color: Colors.white,
                              );
                            } else if (controller.gameEvent ==
                                LobbyGameEvent.night) {
                              return const Icon(
                                Icons.dark_mode_rounded,
                                color: Colors.white,
                              );
                            } else if (controller.gameEvent ==
                                LobbyGameEvent.vote) {
                              return const Icon(
                                Icons.how_to_vote_rounded,
                                color: Colors.white,
                              );
                            } else {
                              return const Icon(
                                Icons.workspaces_rounded,
                                color: Colors.white,
                              );
                            }
                          },
                        ),
                      ],
                    ),
                  ),
                  const Padding(
                    padding: EdgeInsets.only(right: 16.0),
                    child: Align(
                      alignment: Alignment.centerRight,
                      child: Icon(
                        Icons.swipe_right_rounded,
                        color: Colors.white,
                      ),
                    ),
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Expanded(
                    child: GetBuilder<LobbyGameScreenController>(
                      id: 'players',
                      builder: (controller) => GridView.builder(
                        itemCount: controller.players.length,
                        gridDelegate:
                            const SliverGridDelegateWithFixedCrossAxisCount(
                                crossAxisCount: 3),
                        itemBuilder: (context, index) => Center(
                          child: GetBuilder<LobbyGameScreenController>(
                            id: controller.players[index].user_id,
                            builder: (controller) => GestureDetector(
                              onTap: () {
                                // check player profile
                                if (controller.isPlayer ||
                                    controller.isObserver) {
                                  controller.checkPlayerProfile(
                                      controller.players[index].user_id);
                                } else {
                                  // player permissions
                                  showModalBottomSheet(
                                    isScrollControlled: true,
                                    backgroundColor: Colors.transparent,
                                    context: context,
                                    builder: (context) =>
                                        ExpandedPlayerMenuForCreator(
                                      player: controller.players[index],
                                      playerAliveStatus: controller
                                          .players[index].status.alive,
                                      playerPermission: controller
                                          .playersPermissionList[index],
                                      permissionsCallback:
                                          (permissions, alive, changedSide) {
                                        Get.back();
                                        if (permissions.isNotEmpty) {
                                          // emit
                                          controller.changePlayerPermission(
                                              controller.players[index].user_id,
                                              permissions);
                                        }

                                        // alive status
                                        controller.setPlayerAliveStatus(
                                            controller.players[index].user_id,
                                            alive);

                                        // change side
                                        if (changedSide != null) {
                                          controller.changePlayerSide(
                                              controller.players[index].user_id,
                                              changedSide);
                                        }
                                      },
                                    ),
                                  );
                                }
                              },
                              child:
                                  _players(controller.players[index], context),
                            ),
                          ),
                        ),
                      ),
                    ),
                  )
                ],
              )
            : const PlayerActs(),
      ),
    );
  }

  Widget _players(LobbyGamePlayerModel model, BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          model.user_index.toString(),
          style: context.textTheme.bodyMedium,
        ),
        const SizedBox(
          width: 4,
        ),
        Column(
          children: [
            Stack(
              children: [
                // online player
                CachedNetworkImage(
                  imageUrl: '$appBaseUrl/${model.avatar}',
                  imageBuilder: (context, imageProvider) => Container(
                    width: MediaQuery.of(context).size.width * widthSize / 100,
                    height: MediaQuery.of(context).size.width * widthSize / 100,
                    decoration: ShapeDecoration(
                      shape: ContinuousRectangleBorder(
                        borderRadius: BorderRadius.circular(
                            MediaQuery.of(context).size.width * cropSide / 100),
                      ),
                      image: DecorationImage(
                          image: imageProvider, fit: BoxFit.cover),
                    ),
                  ),
                ),
                // dc
                _dc(model, context),

                // dead
                _dead(model, context),

                // like
                _like(model, context),
                // dislike
                _disLike(model, context),
                // hand rise
                _hand(model, context),
                // challenge
                _challenge(model, context),
                // day act
                _dayAct(model, context)
              ],
            ),
            _playerName(model.name, context),
          ],
        ),
        AnimatedOpacity(
          opacity: model.status.speech ? 1 : 0.3,
          duration: const Duration(milliseconds: 250),
          child: const Icon(
            Icons.mic_rounded,
            color: Colors.white,
          ),
        )
      ],
    );
  }

  _challenge(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.challenge ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Container(
        width: MediaQuery.of(context).size.width * widthSize / 100,
        height: MediaQuery.of(context).size.width * widthSize / 100,
        decoration: ShapeDecoration(
          color: Colors.grey.shade900,
          shape: ContinuousRectangleBorder(
            borderRadius: BorderRadius.circular(
                MediaQuery.of(context).size.width * cropSide / 100),
            side: const BorderSide(width: 1, color: Colors.grey),
          ),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.bolt_rounded,
              color: Colors.white,
            ),
            Text(
              'چالش',
              style: context.textTheme.bodyMedium,
            )
          ],
        ),
      ),
    );
  }

  _disLike(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.dislike ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Visibility(
        visible: model.status.dislike,
        child: Container(
          width: MediaQuery.of(context).size.width * widthSize / 100,
          height: MediaQuery.of(context).size.width * widthSize / 100,
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey),
            ),
          ),
          child: Transform.flip(
            flipY: true,
            child: LottieBuilder.asset(
              'assets/anim_like.json',
              repeat: false,
              animate: model.status.dislike,
            ),
          ),
        ),
      ),
    );
  }

  _hand(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.hand_rise ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Container(
          width: MediaQuery.of(context).size.width * widthSize / 100,
          height: MediaQuery.of(context).size.width * widthSize / 100,
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey),
            ),
          ),
          child: const Center(
            child: Icon(
              Icons.front_hand_rounded,
              color: Colors.white,
            ),
          )),
    );
  }

  _like(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.like ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Visibility(
        visible: model.status.like,
        child: Container(
          width: MediaQuery.of(context).size.width * widthSize / 100,
          height: MediaQuery.of(context).size.width * widthSize / 100,
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey),
            ),
          ),
          child: LottieBuilder.asset(
            'assets/anim_like.json',
            repeat: false,
            animate: model.status.like,
          ),
        ),
      ),
    );
  }

  _playerName(String name, context) {
    return SizedBox(
      width: MediaQuery.of(context).size.width * widthSize / 100,
      child: Text(
        textDirection: TextDirection.ltr,
        textAlign: TextAlign.center,
        name,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
    );
  }

  _dead(LobbyGamePlayerModel model, BuildContext context) {
    return Visibility(
      visible: !model.status.alive,
      child: Container(
        width: MediaQuery.of(context).size.width * widthSize / 100,
        height: MediaQuery.of(context).size.width * widthSize / 100,
        decoration: ShapeDecoration(
          shape: ContinuousRectangleBorder(
            borderRadius: BorderRadius.circular(
                MediaQuery.of(context).size.width * cropSide / 100),
          ),
          image: const DecorationImage(
              image: AssetImage('images/img_blood.png'), fit: BoxFit.cover),
        ),
      ),
    );
  }

  _dayAct(LobbyGamePlayerModel model, BuildContext context) {
    return AnimatedOpacity(
      opacity: model.status.day_act ? 1 : 0,
      duration: const Duration(milliseconds: 250),
      child: Container(
          width: MediaQuery.of(context).size.width * widthSize / 100,
          height: MediaQuery.of(context).size.width * widthSize / 100,
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey),
            ),
          ),
          child: const Center(
            child: Icon(
              Icons.online_prediction_rounded,
              color: Colors.white,
            ),
          )),
    );
  }

  _dc(LobbyGamePlayerModel model, BuildContext context) {
    return Visibility(
      visible: !model.status.connected,
      child: Container(
        width: MediaQuery.of(context).size.width * widthSize / 100,
        height: MediaQuery.of(context).size.width * widthSize / 100,
        decoration: ShapeDecoration(
          shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * cropSide / 100),
              side: const BorderSide(width: 1, color: Colors.grey)),
        ),
        child: Container(
          decoration: ShapeDecoration(
            color: Colors.grey.shade900,
            shape: ContinuousRectangleBorder(
                borderRadius: BorderRadius.circular(
                    MediaQuery.of(context).size.width * cropSide / 100),
                side: const BorderSide(width: 1, color: Colors.grey)),
          ),
          child: const Icon(
            Icons.wifi_off_rounded,
            color: Colors.white,
          ),
        ),
      ),
    );
  }
}

class BottomScreenPlayer extends StatelessWidget {
  const BottomScreenPlayer({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return GetBuilder<LobbyGameScreenController>(
      id: 'player_permissions',
      builder: (controller) {
        var buttons = [
          Obx(
            () => FloatingActionButton(
              tooltip: 'میکروفون',
              backgroundColor: controller.playerPermissions?.speech == true
                  ? controller.micToggle.value == true
                      ? Colors.green
                      : null
                  : Colors.grey,
              heroTag: 'fabMic',
              onPressed: () {
                controller.speechToggle();
              },
              mini: true,
              child: controller.micToggle.value == true
                  ? const Icon(Icons.mic_rounded)
                  : const Icon(Icons.mic_off_rounded),
            ),
          ),
          FloatingActionButton(
            tooltip: 'لایک',
            backgroundColor: controller.playerPermissions?.like_dislike == true
                ? null
                : Colors.grey,
            heroTag: 'fabLike',
            onPressed: () {
              controller.like();
            },
            mini: true,
            child: Obx(
              () => controller.likeDislikeTimeLeft.value == 5
                  ? const Icon(Icons.thumb_up_rounded)
                  : Text(
                      '${controller.likeDislikeTimeLeft.value}',
                      style: TextStyle(
                          fontFamily: 'shabnam',
                          fontSize: 14.sp,
                          color: Colors.black),
                    ),
            ),
          ),
          FloatingActionButton(
            tooltip: 'دیس لایک',
            backgroundColor: controller.playerPermissions?.like_dislike == true
                ? null
                : Colors.grey,
            heroTag: 'fabDisLike',
            onPressed: () {
              controller.disLike();
            },
            mini: true,
            child: Obx(
              () => controller.likeDislikeTimeLeft.value == 5
                  ? const Icon(Icons.thumb_down_rounded)
                  : Text(
                      '${controller.likeDislikeTimeLeft.value}',
                      style: TextStyle(
                          fontFamily: 'shabnam',
                          fontSize: 14.sp,
                          color: Colors.black),
                    ),
            ),
          ),
          FloatingActionButton(
            tooltip: 'بالابردن دست',
            backgroundColor: controller.playerPermissions?.hand_rise == true
                ? null
                : Colors.grey,
            heroTag: 'fabHandRise',
            onPressed: () {
              controller.handRise(!controller.selfDetail().status.hand_rise);
            },
            mini: true,
            child: const Icon(
              Icons.front_hand_rounded,
            ),
          ),
          FloatingActionButton(
            tooltip: 'اکت روز',
            backgroundColor: controller.playerPermissions?.day_act == true
                ? null
                : Colors.grey,
            heroTag: 'fabDayAct',
            onPressed: () {
              controller.dayAct(!controller.selfDetail().status.day_act);
            },
            mini: true,
            child: const Icon(
              Icons.online_prediction_rounded,
            ),
          ),
          FloatingActionButton(
            tooltip: 'چالش',
            backgroundColor: controller.playerPermissions?.challenge == true
                ? null
                : Colors.grey,
            heroTag: 'fabChallenge',
            onPressed: () {
              controller
                  .challengeRequest(!controller.selfDetail().status.challenge);
            },
            mini: true,
            child: const Icon(
              Icons.bolt_rounded,
            ),
          ),
          FloatingActionButton(
            tooltip: 'پیام خصوصی به گرداننده',
            backgroundColor:
                controller.playerPermissions?.chat == true ? null : Colors.grey,
            heroTag: 'fabChat',
            onPressed: () {
              if (controller.playerPermissions?.chat == true) {
                showModalBottomSheet(
                  isScrollControlled: true,
                  backgroundColor: Colors.transparent,
                  context: context,
                  builder: (context) => SendMessageToCreator(
                    callbackMsg: () {
                      Get.back();
                      // send
                      controller.sendMessageToCreator();
                    },
                  ),
                );
              } else {
                BotToast.showText(text: 'اجازه ارسال پیام داده نشده');
              }
            },
            mini: true,
            child: const Icon(
              Icons.mail_rounded,
            ),
          ),
          FloatingActionButton(
            tooltip: 'کارت حرکت آخر',
            backgroundColor:
                controller.playerPermissions?.last_move_card == true
                    ? null
                    : Colors.grey,
            heroTag: 'fabLastMove',
            onPressed: () {
              if (controller.playerPermissions?.last_move_card == true) {
                controller.pickLastMoveCard();
              }
            },
            mini: true,
            child: const Icon(
              Icons.style_rounded,
            ),
          ),
        ];
        return GetBuilder<LobbyGameScreenController>(
          id: controller.selfDetail().user_id,
          builder: (_) => controller.selfDetail().status.alive
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Expanded(
                      child: ListView.builder(
                        itemCount: buttons.length,
                        scrollDirection: Axis.horizontal,
                        itemBuilder: (context, index) => Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16),
                          child: Center(
                            child: buttons[index],
                          ),
                        ),
                      ),
                    ),
                  ],
                )
              : Center(
                  child: Text(
                    'شما زنده نیستی',
                    style: context.textTheme.bodyMedium,
                  ),
                ),
        );
      },
    );
  }
}

class BottomScreenCreator extends StatelessWidget {
  const BottomScreenCreator({super.key, required this.controller});

  final LobbyGameScreenController controller;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        GetBuilder<LobbyGameScreenController>(
          id: 'creator',
          builder: (controller) => FloatingActionButton(
            backgroundColor:
                controller.creatorModel?.private == true ? Colors.red : null,
            tooltip: 'صحبت خصوصی',
            onPressed: () {
              showModalBottomSheet(
                isScrollControlled: true,
                backgroundColor: Colors.transparent,
                context: context,
                builder: (context) => PrivateSpeechRoom(
                  players: controller.players,
                ),
              );
            },
            heroTag: 'fabPrivateSpeech',
            mini: true,
            child: const Icon(
              Icons.privacy_tip_rounded,
              color: Colors.black,
            ),
          ),
        ),
        FloatingActionButton(
          tooltip: 'اتاق فرمان',
          heroTag: 'creatorFabMenu',
          onPressed: () {
            // tell to server we need room status
            controller.roomOverAll();
          },
          mini: true,
          child: const Icon(
            Icons.menu_rounded,
            color: Colors.black,
          ),
        ),
        Obx(
          () => FloatingActionButton(
            tooltip: 'میکروفون',
            backgroundColor: controller.micToggle.value ? Colors.green : null,
            onPressed: () {
              controller.speechToggle();
            },
            mini: true,
            child: controller.micToggle.value
                ? const Icon(Icons.mic_rounded)
                : const Icon(Icons.mic_off_rounded),
          ),
        )
      ],
    );
  }
}

class ShowCharacterToUser extends StatelessWidget {
  const ShowCharacterToUser(
      {super.key, required this.character, required this.side});

  final String character;
  final String side;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SizedBox(
      height: MediaQuery.of(context).size.height * 35 / 100,
      child: ClipRRect(
        borderRadius: BorderRadius.circular(16),
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 10, sigmaY: 10),
          child: Container(
            color: const Color.fromRGBO(102, 102, 102, 0.7),
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    'نمایش نقش',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Text(
                    character,
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: 22.sp,
                        fontFamily: 'shabnam'),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Text(
                    side,
                    style: context.textTheme.bodyLarge,
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

class SendMessageToCreator extends StatelessWidget {
  const SendMessageToCreator({super.key, required this.callbackMsg});

  final VoidCallback callbackMsg;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Padding(
      padding:
          EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
      child: ClipRRect(
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(16),
          topRight: Radius.circular(16),
        ),
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: Container(
            padding: const EdgeInsets.all(8),
            color: const Color.fromRGBO(102, 102, 102, 0.6),
            child: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const SizedBox(
                    width: double.infinity,
                  ),
                  Text(
                    'ارسال پیام خصوصی به گرداننده',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  SizedBox(
                    width: MediaQuery.of(context).size.width * 80 / 100,
                    child: GetBuilder<LobbyGameScreenController>(
                      builder: (controller) => TextField(
                        controller: controller.messageToCreatorController,
                        decoration: InputDecoration(
                          border: context.theme.inputDecorationTheme.border,
                          hintText: 'متن',
                          hintTextDirection: TextDirection.rtl,
                          hintFadeDuration: const Duration(milliseconds: 250),
                          hintStyle: TextStyle(
                              fontFamily: 'shabnam',
                              color: Colors.grey,
                              fontSize: 14.sp),
                        ),
                        textDirection: TextDirection.rtl,
                        textInputAction: TextInputAction.newline,
                        maxLines: null,
                      ),
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  SizedBox(
                    width: MediaQuery.of(context).size.width * 40 / 100,
                    child: ElevatedButton(
                      onPressed: () {
                        callbackMsg();
                      },
                      child: Text(
                        'ارسال پیام',
                        style: context.textTheme.bodyMedium,
                      ),
                    ),
                  ),
                  const SizedBox(
                    height: 16,
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

class DialogExitGame extends StatelessWidget {
  const DialogExitGame(
      {super.key, required this.dcCallback, required this.leaveCallback});
  final VoidCallback dcCallback;
  final VoidCallback leaveCallback;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: SizedBox(
        width: MediaQuery.of(context).size.width * 95 / 100,
        child: ClipRRect(
          borderRadius: BorderRadius.circular(16),
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
            child: Container(
              padding: const EdgeInsets.all(8),
              color: const Color.fromRGBO(102, 102, 102, 0.5),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const SizedBox(
                    height: 16,
                  ),
                  Text(
                    'آیا قصد خروج از بازی رو داری ؟',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  GetBuilder<LobbyGameScreenController>(
                    builder: (controller) => Row(
                      mainAxisAlignment: controller.isObserver
                          ? MainAxisAlignment.center
                          : MainAxisAlignment.spaceEvenly,
                      children: [
                        SizedBox(
                          width: MediaQuery.of(context).size.width * 40 / 100,
                          child: ElevatedButton(
                            onPressed: dcCallback,
                            child: Text(
                              'خروج موقت',
                              style: context.textTheme.bodyMedium,
                            ),
                          ),
                        ),
                        Visibility(
                          visible: !controller.isObserver,
                          child: SizedBox(
                            width: MediaQuery.of(context).size.width * 40 / 100,
                            child: ElevatedButton(
                              onPressed: leaveCallback,
                              child: Text(
                                'ترک بازی',
                                style: context.textTheme.bodyMedium,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(
                    height: 16,
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

class ExpandedMainMenuForCreator extends StatefulWidget {
  const ExpandedMainMenuForCreator(
      {super.key,
      required this.gameEvent,
      required this.overAll,
      required this.callbackModifiedPermissions});

  final String gameEvent;
  final LobbyGameOverAllPermissionsModel overAll;
  final Function(List<LobbyGameOverAllPermissionCallbackModel> permissionsList,
      String? gameEvent) callbackModifiedPermissions;

  @override
  State<ExpandedMainMenuForCreator> createState() =>
      _ExpandedMainMenuForCreatorState();
}

class _ExpandedMainMenuForCreatorState
    extends State<ExpandedMainMenuForCreator> {
  final List<String> gameEventList = ['روز', 'رای گیری', 'شب', 'کی آس'];
  final List<LobbyGameOverAllPermissionCallbackModel> permissionModifiedList =
      List<LobbyGameOverAllPermissionCallbackModel>.empty(growable: true);

  late String event;
  String? selectedEvent;
  late LobbyGameOverAllPermissionsModel permissions;
  late bool challenge;
  late bool like_dislike;
  late bool speech;
  late bool hand;
  late bool chat;

  @override
  void initState() {
    event = widget.gameEvent;
    permissions = widget.overAll;
    challenge = widget.overAll.challenge;
    like_dislike = widget.overAll.like_dislike;
    speech = widget.overAll.speech;
    hand = widget.overAll.hand_rise;
    chat = widget.overAll.chat;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SizedBox(
      height: MediaQuery.of(context).size.height * 90 / 100,
      child: ClipRRect(
        borderRadius: BorderRadius.circular(16),
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: Container(
            color: const Color.fromRGBO(102, 102, 102, 0.7),
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Column(
                children: [
                  const SizedBox(
                    width: double.infinity,
                  ),
                  Text(
                    'اتاق فرمان',
                    style: context.textTheme.bodyLarge,
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Text(
                    'تو اتاق فرمان هر نوع تغییری بر روی تمامی بازیکنان اعمال میشه',
                    style: context.textTheme.bodyMedium,
                    textDirection: TextDirection.rtl,
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(event, style: context.textTheme.titleLarge),
                      const SizedBox(
                        width: 12,
                      ),
                      Text(
                        'ایونت بازی',
                        style: context.textTheme.bodyMedium,
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Wrap(
                    spacing: 16,
                    runSpacing: 16,
                    alignment: WrapAlignment.center,
                    direction: Axis.horizontal,
                    textDirection: TextDirection.rtl,
                    children: List.generate(
                      gameEventList.length,
                      (index) => GestureDetector(
                        onTap: () {
                          if (gameEventList[index] == event) {
                            return;
                          } else if (selectedEvent == gameEventList[index]) {
                            setState(() {
                              selectedEvent = null;
                            });
                          } else {
                            setState(() {
                              selectedEvent = gameEventList[index];
                            });
                          }
                        },
                        child: Container(
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(12),
                            color: gameEventList[index] == event
                                ? Colors.green
                                : gameEventList[index] == selectedEvent
                                    ? Colors.cyan
                                    : Colors.transparent,
                            border: Border.all(
                                color: gameEventList[index] == event
                                    ? Colors.green
                                    : gameEventList[index] == selectedEvent
                                        ? Colors.cyan
                                        : Colors.grey,
                                width: 1),
                          ),
                          child: Text(
                            gameEventList[index],
                            style: context.textTheme.bodyMedium,
                          ),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Text(
                    'دسترسی',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      Expanded(
                        child: Center(
                          child: Column(
                            children: [
                              Text(
                                'چالش',
                                style: context.textTheme.bodyMedium,
                              ),
                              const SizedBox(
                                height: 8,
                              ),
                              Switch(
                                activeColor: Colors.cyan,
                                value: permissions.challenge,
                                onChanged: (value) {
                                  setState(
                                    () {
                                      permissions.challenge = value;

                                      var permissionStatus =
                                          permissionModifiedList
                                              .firstWhereOrNull((element) =>
                                                  element.name == 'challenge');
                                      if (permissionStatus == null) {
                                        permissionModifiedList.add(
                                            LobbyGameOverAllPermissionCallbackModel(
                                                name: 'challenge',
                                                state: value));
                                      } else {
                                        permissionModifiedList
                                            .remove(permissionStatus);
                                      }
                                    },
                                  );
                                },
                              )
                            ],
                          ),
                        ),
                      ),
                      Expanded(
                        child: Center(
                          child: Column(
                            children: [
                              Text(
                                'لایک و دیس لایک',
                                style: context.textTheme.bodyMedium,
                              ),
                              const SizedBox(
                                height: 8,
                              ),
                              Switch(
                                activeColor: Colors.cyan,
                                value: permissions.like_dislike,
                                onChanged: (value) {
                                  setState(() {
                                    permissions.like_dislike = value;

                                    var permissionStatus =
                                        permissionModifiedList.firstWhereOrNull(
                                            (element) =>
                                                element.name == 'like_dislike');
                                    if (permissionStatus == null) {
                                      permissionModifiedList.add(
                                          LobbyGameOverAllPermissionCallbackModel(
                                              name: 'like_dislike',
                                              state: value));
                                    } else {
                                      permissionModifiedList
                                          .remove(permissionStatus);
                                    }
                                  });
                                },
                              )
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      Expanded(
                        child: Center(
                          child: Column(
                            children: [
                              Text(
                                'دست',
                                style: context.textTheme.bodyMedium,
                              ),
                              const SizedBox(
                                height: 8,
                              ),
                              Switch(
                                activeColor: Colors.cyan,
                                value: permissions.hand_rise,
                                onChanged: (value) {
                                  setState(
                                    () {
                                      permissions.hand_rise = value;

                                      var permissionStatus =
                                          permissionModifiedList
                                              .firstWhereOrNull((element) =>
                                                  element.name == 'hand_rise');
                                      if (permissionStatus == null) {
                                        permissionModifiedList.add(
                                            LobbyGameOverAllPermissionCallbackModel(
                                                name: 'hand_rise',
                                                state: value));
                                      } else {
                                        permissionModifiedList
                                            .remove(permissionStatus);
                                      }
                                    },
                                  );
                                },
                              )
                            ],
                          ),
                        ),
                      ),
                      Expanded(
                        child: Center(
                          child: Column(
                            children: [
                              Text(
                                'میکروفون',
                                style: context.textTheme.bodyMedium,
                              ),
                              const SizedBox(
                                height: 8,
                              ),
                              Switch(
                                activeColor: Colors.cyan,
                                value: permissions.speech,
                                onChanged: (value) {
                                  setState(
                                    () {
                                      permissions.speech = value;

                                      var permissionStatus =
                                          permissionModifiedList
                                              .firstWhereOrNull((element) =>
                                                  element.name == 'speech');
                                      if (permissionStatus == null) {
                                        permissionModifiedList.add(
                                            LobbyGameOverAllPermissionCallbackModel(
                                                name: 'speech', state: value));
                                      } else {
                                        permissionModifiedList
                                            .remove(permissionStatus);
                                      }
                                    },
                                  );
                                },
                              )
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                  Center(
                    child: Column(
                      children: [
                        Text(
                          'پیام خصوصی',
                          style: context.textTheme.bodyMedium,
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        Switch(
                          activeColor: Colors.cyan,
                          value: permissions.chat,
                          onChanged: (value) {
                            setState(
                              () {
                                permissions.chat = value;

                                var permissionStatus =
                                    permissionModifiedList.firstWhereOrNull(
                                        (element) => element.name == 'chat');
                                if (permissionStatus == null) {
                                  permissionModifiedList.add(
                                      LobbyGameOverAllPermissionCallbackModel(
                                          name: 'chat', state: value));
                                } else {
                                  permissionModifiedList
                                      .remove(permissionStatus);
                                }
                              },
                            );
                          },
                        )
                      ],
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  SizedBox(
                    width: MediaQuery.of(context).size.width * 30 / 100,
                    child: ElevatedButton(
                      style: ButtonStyle(
                          backgroundColor: permissionModifiedList.isEmpty
                              ? selectedEvent == null
                                  ? const MaterialStatePropertyAll(Colors.grey)
                                  : null
                              : null),
                      onPressed: () {
                        if (permissionModifiedList.isEmpty &&
                            selectedEvent == null) {
                          return;
                        } else {
                          widget.callbackModifiedPermissions(
                              permissionModifiedList, selectedEvent);
                        }
                      },
                      child: Text(
                        'تایید',
                        style: context.textTheme.bodyMedium,
                      ),
                    ),
                  ),
                  const Spacer(),
                  GetBuilder<LobbyGameScreenController>(
                    builder: (controller) => SizedBox(
                      width: MediaQuery.of(context).size.width * 40 / 100,
                      child: ElevatedButton(
                        style: const ButtonStyle(
                          backgroundColor: MaterialStatePropertyAll(Colors.red),
                        ),
                        onPressed: () {
                          Get.dialog(
                            EndGameConfirmation(
                              endCallback: () {
                                controller.endGame();
                              },
                            ),
                          );
                        },
                        child: Text(
                          'اتمام بازی',
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
    );
  }
}

class ExpandedPlayerMenuForCreator extends StatefulWidget {
  const ExpandedPlayerMenuForCreator(
      {super.key,
      required this.player,
      required this.playerPermission,
      required this.playerAliveStatus,
      required this.permissionsCallback});

  final LobbyGamePlayerPermissionsModel playerPermission;
  final LobbyGamePlayerModel player;
  final Function(List<LobbyGameModifiedPermissionModel> permissions,
      bool aliveStatus, String? changedSide) permissionsCallback;
  final bool playerAliveStatus;

  @override
  State<ExpandedPlayerMenuForCreator> createState() =>
      _ExpandedPlayerMenuForCreatorState();
}

class _ExpandedPlayerMenuForCreatorState
    extends State<ExpandedPlayerMenuForCreator> {
  late List<LobbyGameModifiedPermissionModel> previousContainer;
  List<LobbyGameModifiedPermissionModel> changedPermission =
      List<LobbyGameModifiedPermissionModel>.empty(growable: true);

  late bool playerAliveStatus;
  late bool playerNewLiveStatus;
  String? selectedSide;

  @override
  void initState() {
    playerAliveStatus = widget.playerAliveStatus;
    playerNewLiveStatus = playerAliveStatus;

    previousContainer = [
      LobbyGameModifiedPermissionModel(
          active: widget.playerPermission.speech,
          name: 'speech',
          label: 'میکروفون'),
      LobbyGameModifiedPermissionModel(
          active: widget.playerPermission.like_dislike,
          name: 'like_dislike',
          label: 'لایک و دیسلایک'),
      LobbyGameModifiedPermissionModel(
          active: widget.playerPermission.challenge,
          name: 'challenge',
          label: 'چالش'),
      LobbyGameModifiedPermissionModel(
          active: widget.playerPermission.hand_rise,
          name: 'hand_rise',
          label: 'دست'),
      LobbyGameModifiedPermissionModel(
          active: widget.playerPermission.chat,
          name: 'chat',
          label: 'پیام خصوصی'),
      LobbyGameModifiedPermissionModel(
          active: widget.playerPermission.last_move_card,
          name: 'last_move_card',
          label: 'کارت حرکت آخر'),
      LobbyGameModifiedPermissionModel(
          active: widget.playerPermission.day_act,
          name: 'day_act',
          label: 'اکت روز'),
    ];
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SizedBox(
      height: MediaQuery.of(context).size.height * 85 / 100,
      child: Scaffold(
        backgroundColor: Colors.transparent,
        body: ClipRRect(
          borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(16), topRight: Radius.circular(16)),
          child: Container(
            color: const Color.fromRGBO(102, 102, 102, 0.7),
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Column(
                children: [
                  const SizedBox(
                    width: double.infinity,
                    height: 8,
                  ),
                  Text(
                    'دسترسی بازیکن',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Row(
                    children: [
                      Expanded(
                        child: Column(
                          mainAxisSize: MainAxisSize.max,
                          children: [
                            Text(widget.player.character,
                                style: context.textTheme.bodyLarge),
                            const SizedBox(
                              height: 22,
                            ),
                            Text(widget.player.side,
                                style: context.textTheme.bodyMedium),
                          ],
                        ),
                      ),
                      Expanded(
                          child: Column(
                        children: [
                          CachedNetworkImage(
                            imageUrl: '$appBaseUrl/${widget.player.avatar}',
                            imageBuilder: (context, imageProvider) => Container(
                              width:
                                  MediaQuery.of(context).size.width * 20 / 100,
                              height:
                                  MediaQuery.of(context).size.width * 20 / 100,
                              decoration: ShapeDecoration(
                                  shape: ContinuousRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        MediaQuery.of(context).size.width *
                                            12 /
                                            100),
                                  ),
                                  image: DecorationImage(
                                      image: imageProvider, fit: BoxFit.cover)),
                            ),
                            placeholder: (context, url) => Container(
                              width:
                                  MediaQuery.of(context).size.width * 20 / 100,
                              height:
                                  MediaQuery.of(context).size.width * 20 / 100,
                              decoration: ShapeDecoration(
                                  shape: ContinuousRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        MediaQuery.of(context).size.width *
                                            12 /
                                            100),
                                  ),
                                  color: Colors.grey),
                            ),
                          ),
                          const SizedBox(
                            height: 8,
                          ),
                          SizedBox(
                            width: MediaQuery.of(context).size.width * 25 / 100,
                            child: Text(
                              widget.player.name,
                              style: context.textTheme.bodyMedium,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              textAlign: TextAlign.center,
                            ),
                          )
                        ],
                      )),
                    ],
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Wrap(
                    alignment: WrapAlignment.center,
                    textDirection: TextDirection.rtl,
                    runSpacing: 8,
                    spacing: 8,
                    children: List.generate(
                      previousContainer.length,
                      (index) => GestureDetector(
                        onTap: () {
                          if (changedPermission.firstWhereOrNull((element) =>
                                  element.name ==
                                  previousContainer[index].name) ==
                              null) {
                            var instance = previousContainer[index];
                            var modified = instance..active = !instance.active;
                            setState(() {
                              changedPermission.add(modified);
                            });
                          } else {
                            var instance = previousContainer[index];

                            setState(() {
                              changedPermission.remove(instance);
                              instance.active = !instance.active;
                            });
                          }
                        },
                        child: Container(
                          padding: const EdgeInsets.all(8),
                          decoration: BoxDecoration(
                            color: previousContainer[index].active
                                ? Colors.green
                                : Colors.grey,
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                              '${previousContainer[index].label} : ${previousContainer[index].active ? 'فعال' : 'غیرفعال'}'),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(
                    height: 20,
                  ),
                  Text(
                    'وضعیت بازیکن',
                    style: context.textTheme.bodyMedium,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        'مرده',
                        style: context.textTheme.bodyMedium,
                      ),
                      const SizedBox(
                        width: 8,
                      ),
                      Switch(
                        activeTrackColor: Colors.green.shade700,
                        inactiveTrackColor: Colors.red.shade700,
                        inactiveThumbColor: Colors.white,
                        value: playerNewLiveStatus,
                        onChanged: (value) {
                          setState(() {
                            playerNewLiveStatus = value;
                            if (value) {
                              BotToast.showText(
                                  text: 'بازیکن به دست شما زنده خواهد شد');
                            } else {
                              BotToast.showText(
                                  text: 'بازیکن به دست شما کشته خواهد شد');
                            }
                          });
                        },
                      ),
                      const SizedBox(
                        width: 8,
                      ),
                      Text(
                        'زنده',
                        style: context.textTheme.bodyMedium,
                      )
                    ],
                  ),
                  const SizedBox(
                    height: 20,
                  ),
                  Text(
                    'تغییر ساید بازیکن',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  GetBuilder<LobbyGameScreenController>(
                    builder: (controller) => Wrap(
                      runSpacing: 8,
                      spacing: 8,
                      children: List.generate(
                        controller.sideList.length,
                        (index) => GestureDetector(
                          onTap: () {
                            setState(
                              () {
                                if (controller.sideList[index] ==
                                    widget.player.side) {
                                  return;
                                } else {
                                  if (selectedSide == null) {
                                    selectedSide = controller.sideList[index];
                                  } else {
                                    selectedSide = null;
                                  }
                                }
                              },
                            );
                          },
                          child: Container(
                            padding: const EdgeInsets.all(8),
                            decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(8),
                                color: controller.sideList[index] ==
                                        widget.player.side
                                    ? Colors.green
                                    : selectedSide == controller.sideList[index]
                                        ? Colors.cyan
                                        : Colors.grey),
                            child: Text(controller.sideList[index],
                                style: context.textTheme.bodyMedium),
                          ),
                        ),
                      ),
                    ),
                  ),
                  const Spacer(),
                  ElevatedButton(
                    style: ButtonStyle(
                      backgroundColor: changedPermission.isNotEmpty ||
                              playerNewLiveStatus != playerAliveStatus ||
                              selectedSide != null
                          ? null
                          : const MaterialStatePropertyAll(Colors.grey),
                    ),
                    onPressed: () {
                      if (changedPermission.isNotEmpty ||
                          playerNewLiveStatus != playerAliveStatus ||
                          selectedSide != null) {
                        // send to server
                        widget.permissionsCallback(changedPermission,
                            playerNewLiveStatus, selectedSide);
                      }
                    },
                    child: Text(
                      'اعمال تغییرات',
                      style: context.textTheme.bodyMedium,
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

class PrivateSpeechRoom extends StatefulWidget {
  const PrivateSpeechRoom({super.key, required this.players});

  final List<LobbyGamePlayerModel> players;

  @override
  State<PrivateSpeechRoom> createState() => _PrivateSpeechRoomState();
}

class _PrivateSpeechRoomState extends State<PrivateSpeechRoom> {
  final List<LobbyGamePlayerModel> selectedPlayers =
      List<LobbyGamePlayerModel>.empty(growable: true);
  final animatedListKey = GlobalKey<AnimatedListState>();

  @override
  void initState() {
    var privateOn =
        widget.players.where((element) => element.status.private).toList();
    if (privateOn.isNotEmpty) {
      selectedPlayers.addAll(privateOn);
    }
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SizedBox(
      height: MediaQuery.of(context).size.height * 95 / 100,
      child: ClipRRect(
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(16),
          topRight: Radius.circular(16),
        ),
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: Scaffold(
            backgroundColor: const Color.fromRGBO(102, 102, 102, 0.7),
            body: ClipRRect(
              borderRadius: const BorderRadius.only(
                topLeft: Radius.circular(16),
                topRight: Radius.circular(16),
              ),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    Text(
                      'صحبت خصوصی',
                      style: context.textTheme.bodyLarge,
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    Text(
                      'شما میتونی با ایجاد صحبت خصوصی با بازیکن مدنظر به گفتوگو بپردازی و بقیه بازیکنان متوجه محتوای صحبت شما نمیشن ، ولی متوجه ورود شما به اتاق گفتوگوی خصوصی خواهند شد',
                      style: context.textTheme.bodyMedium,
                      textDirection: TextDirection.rtl,
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    SizedBox(
                      height: MediaQuery.of(context).size.width * 24 / 100,
                      child: Container(
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(18),
                          color: Colors.grey.shade600,
                        ),
                        child: Row(
                          children: [
                            Expanded(
                              child: ListView.builder(
                                itemCount: selectedPlayers.length,
                                scrollDirection: Axis.horizontal,
                                itemBuilder: (context, index) =>
                                    _selectedPlayerItem(
                                  selectedPlayers[index],
                                ),
                              ),
                            ),
                            GetBuilder<LobbyGameScreenController>(
                              id: 'creator',
                              builder: (controller) => IconButton(
                                onPressed: () {
                                  if (controller.creatorModel?.private ==
                                      true) {
                                    BotToast.showText(
                                        text:
                                            'اول باید گفتوگو را به اتمام برسونی');
                                    return;
                                  }
                                  if (selectedPlayers.isNotEmpty) {
                                    setState(() {
                                      selectedPlayers.clear();
                                    });
                                  }
                                },
                                icon: const Icon(
                                  Icons.delete_rounded,
                                  color: Colors.white,
                                ),
                              ),
                            ),
                            const SizedBox(
                              width: 8,
                            )
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Expanded(
                      child: AnimationLimiter(
                        child: GridView.builder(
                          itemCount: widget.players.length,
                          gridDelegate:
                              const SliverGridDelegateWithFixedCrossAxisCount(
                                  crossAxisCount: 3),
                          itemBuilder: (context, index) =>
                              AnimationConfiguration.staggeredGrid(
                            position: index,
                            duration: const Duration(milliseconds: 350),
                            columnCount: 3,
                            child: ScaleAnimation(
                              child: FadeInAnimation(
                                child: _playerItem(
                                  player: widget.players[index],
                                  alreadySelected: selectedPlayers
                                              .firstWhereOrNull((element) =>
                                                  element.user_id ==
                                                  widget.players[index]
                                                      .user_id) !=
                                          null
                                      ? true
                                      : false,
                                  callback: () {
                                    setState(
                                      () {
                                        var exist = selectedPlayers
                                            .firstWhereOrNull((element) =>
                                                element.user_id ==
                                                widget.players[index].user_id);

                                        // add
                                        if (exist == null) {
                                          selectedPlayers
                                              .add(widget.players[index]);
                                        }
                                        // remove
                                        else {
                                          selectedPlayers
                                              .remove(widget.players[index]);
                                        }
                                      },
                                    );
                                  },
                                ),
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        GetBuilder<LobbyGameScreenController>(
                          id: 'creator',
                          builder: (controller) => SizedBox(
                            width: MediaQuery.of(context).size.width * 40 / 100,
                            child: ElevatedButton(
                              style: ButtonStyle(
                                backgroundColor: MaterialStatePropertyAll(
                                  controller.creatorModel!.private
                                      ? Colors.red
                                      : Colors.grey,
                                ),
                              ),
                              onPressed: () {
                                if (selectedPlayers.isEmpty) {
                                  return;
                                }
                                controller.disablePrivateSpeech();
                                setState(() {
                                  selectedPlayers.clear();
                                });
                              },
                              child: Text(
                                'اتمام',
                                style: context.textTheme.bodyMedium,
                              ),
                            ),
                          ),
                        ),
                        SizedBox(
                          width: MediaQuery.of(context).size.width * 40 / 100,
                          child: GetBuilder<LobbyGameScreenController>(
                            id: 'creator',
                            builder: (controller) => ElevatedButton(
                              style: ButtonStyle(
                                backgroundColor: MaterialStatePropertyAll(
                                  controller.creatorModel!.private
                                      ? Colors.grey
                                      : null,
                                ),
                              ),
                              onPressed: () {
                                if (selectedPlayers.isEmpty) {
                                  BotToast.showText(
                                      text: 'بازیکنی انتخاب نشده');
                                  return;
                                }

                                if (controller.creatorModel?.private == true) {
                                  BotToast.showText(text: 'شما داخل گفتوگویی');
                                  return;
                                }
                                controller.createPrivateSpeechList(
                                    selectedPlayers
                                        .map((e) => e.user_id)
                                        .toList());

                                Get.back();
                              },
                              child: Text(
                                'شروع',
                                style: context.textTheme.bodyMedium,
                              ),
                            ),
                          ),
                        ),
                      ],
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

  Widget _playerItem(
      {required LobbyGamePlayerModel player,
      required bool alreadySelected,
      required VoidCallback callback}) {
    return Center(
      child: GestureDetector(
        onTap: callback,
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('${player.user_index}'),
            const SizedBox(
              width: 4,
            ),
            Column(
              children: [
                CachedNetworkImage(
                  imageUrl: '$appBaseUrl/${player.avatar}',
                  imageBuilder: (context, imageProvider) => Container(
                    width: MediaQuery.of(context).size.width * 15 / 100,
                    height: MediaQuery.of(context).size.width * 15 / 100,
                    decoration: ShapeDecoration(
                      shape: ContinuousRectangleBorder(
                        borderRadius: BorderRadius.circular(
                            MediaQuery.of(context).size.width * 10 / 100),
                        side: BorderSide(
                            color: alreadySelected
                                ? Colors.red
                                : Colors.transparent,
                            width: 1),
                      ),
                      image: DecorationImage(image: imageProvider),
                    ),
                  ),
                  placeholder: (context, url) => Container(
                    width: MediaQuery.of(context).size.width * 15 / 100,
                    height: MediaQuery.of(context).size.width * 15 / 100,
                    decoration: ShapeDecoration(
                      shape: ContinuousRectangleBorder(
                        borderRadius: BorderRadius.circular(
                            MediaQuery.of(context).size.width * 10 / 100),
                      ),
                      color: Colors.grey,
                    ),
                  ),
                ),
                SizedBox(
                  width: MediaQuery.of(context).size.width * 15 / 100,
                  child: Text(
                    player.name,
                    maxLines: 1,
                    textDirection: TextDirection.rtl,
                    textAlign: TextAlign.center,
                    overflow: TextOverflow.ellipsis,
                  ),
                )
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _selectedPlayerItem(LobbyGamePlayerModel player) {
    return Align(
      alignment: Alignment.centerLeft,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          CachedNetworkImage(
            imageUrl: '$appBaseUrl/${player.avatar}',
            imageBuilder: (context, imageProvider) => Container(
              margin: const EdgeInsets.symmetric(horizontal: 10),
              width: MediaQuery.of(context).size.width * 15 / 100,
              height: MediaQuery.of(context).size.width * 15 / 100,
              decoration: ShapeDecoration(
                  shape: ContinuousRectangleBorder(
                      borderRadius: BorderRadius.circular(
                          MediaQuery.of(context).size.width * 10 / 100)),
                  image: DecorationImage(image: imageProvider)),
            ),
          ),
          const SizedBox(
            height: 4,
          ),
          Text(player.name)
        ],
      ),
    );
  }
}

class InGameMsgDialog extends StatelessWidget {
  const InGameMsgDialog(
      {super.key, required this.msg, this.confirmable = false});

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
                    Image.asset(
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

class CreatorMessageBox extends StatelessWidget {
  const CreatorMessageBox({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SizedBox(
      height: MediaQuery.of(context).size.height * 85 / 100,
      child: ClipRRect(
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(16),
          topRight: Radius.circular(16),
        ),
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: Scaffold(
            backgroundColor: const Color.fromRGBO(102, 102, 102, 0.7),
            body: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                children: [
                  Text(
                    'صندوق پیام',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Expanded(
                    child: GetBuilder<LobbyGameScreenController>(
                      id: 'creator_message',
                      builder: (controller) => AnimatedList(
                        initialItemCount: controller.creatorMessages.length,
                        reverse: true,
                        key: controller.creatorMessageGlobalKey,
                        itemBuilder: (context, index, animation) =>
                            SlideTransition(
                          key: ValueKey(
                              controller.creatorMessages[index].player.avatar),
                          position: Tween<Offset>(
                                  begin: const Offset(0, 0.5), end: Offset.zero)
                              .animate(animation),
                          child:
                              _msg(context, controller.creatorMessages[index]),
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
    );
  }

  Widget _msg(BuildContext context, LobbyGameScreenCreatorMessageModel player) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.all(8),
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16), color: Colors.grey.shade700),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          Expanded(
            child: Text(
              player.content,
              textDirection: TextDirection.rtl,
              textAlign: TextAlign.justify,
              style: context.textTheme.bodyMedium,
            ),
          ),
          const SizedBox(
            width: 8,
          ),
          const SizedBox(
            width: 8,
          ),
          SizedBox(
            height: MediaQuery.of(context).size.width * 15 / 100,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(player.player.character),
                const SizedBox(
                  height: 8,
                ),
                Text(player.player.side),
              ],
            ),
          ),
          const SizedBox(
            width: 8,
          ),
          Column(
            children: [
              CachedNetworkImage(
                imageUrl: '$appBaseUrl/${player.player.avatar}',
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
                placeholder: (context, url) => Container(
                  width: MediaQuery.of(context).size.width * 15 / 100,
                  height: MediaQuery.of(context).size.width * 15 / 100,
                  decoration: ShapeDecoration(
                    shape: ContinuousRectangleBorder(
                      borderRadius: BorderRadius.circular(
                          MediaQuery.of(context).size.width * 10 / 100),
                    ),
                    color: Colors.grey,
                  ),
                ),
              ),
              SizedBox(
                width: MediaQuery.of(context).size.width * 15 / 100,
                child: Text(
                  player.player.name,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  textDirection: TextDirection.rtl,
                  textAlign: TextAlign.center,
                ),
              )
            ],
          ),
        ],
      ),
    );
  }
}

class PlayerPrivateRoom extends StatelessWidget {
  const PlayerPrivateRoom({super.key, required this.players});
  final List<LobbyGamePlayerModel> players;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return WillPopScope(
      onWillPop: () async => false,
      child: SizedBox(
        height: MediaQuery.of(context).size.height * 85 / 100,
        child: ClipRRect(
          borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(16), topRight: Radius.circular(16)),
          child: BackdropFilter(
            filter: ImageFilter.blur(
              sigmaX: 5,
              sigmaY: 5,
            ),
            child: Scaffold(
              backgroundColor: const Color.fromRGBO(
                102,
                102,
                102,
                0.7,
              ),
              body: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    Text(
                      'به گفتوگوی خصوصی خوش اومدی \n اینجا کسی صدات رو جز اونهایی که باید نمیشنون',
                      style: context.textTheme.bodyMedium,
                      textDirection: TextDirection.rtl,
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'میکروفون شما بطور خودکار روشن خواهد شد',
                          style: context.textTheme.bodyMedium,
                        ),
                        const Icon(
                          Icons.mic_rounded,
                          color: Colors.green,
                        )
                      ],
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Expanded(
                      child: GridView.builder(
                        itemCount: players.length,
                        gridDelegate:
                            const SliverGridDelegateWithFixedCrossAxisCount(
                                crossAxisCount: 3,
                                mainAxisSpacing: 8,
                                crossAxisSpacing: 8),
                        itemBuilder: (context, index) => Center(
                          child: _player(players[index], context),
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

  Widget _player(LobbyGamePlayerModel player, BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          player.user_index.toString(),
          style: context.textTheme.bodyMedium,
        ),
        const SizedBox(
          width: 4,
        ),
        Column(
          children: [
            Stack(
              children: [
                // online player
                CachedNetworkImage(
                  imageUrl: '$appBaseUrl/${player.avatar}',
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
                ),
              ],
            ),
            SizedBox(
              width: MediaQuery.of(context).size.width * 15 / 100,
              child: Text(
                textDirection: TextDirection.rtl,
                textAlign: TextAlign.center,
                player.name,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class EndGameResult extends StatefulWidget {
  const EndGameResult({super.key, required this.endCallback});
  final Function(dynamic rate) endCallback;
  @override
  State<EndGameResult> createState() => _EndGameResultState();
}

class _EndGameResultState extends State<EndGameResult> {
  var creatorRate = 5;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SizedBox(
      height: MediaQuery.of(context).size.height * 85 / 100,
      child: WillPopScope(
        onWillPop: () async => false,
        child: ClipRRect(
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
            child: Scaffold(
              backgroundColor: const Color.fromRGBO(102, 102, 102, 0.7),
              body: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    const SizedBox(
                      width: double.infinity,
                    ),
                    Text(
                      'اتمام بازی',
                      style: context.textTheme.bodyLarge,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Text(
                      'امتیاز دهی به گرداننده بازی',
                      style: context.textTheme.bodyMedium,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    PannableRatingBar(
                      items: List.generate(
                        5,
                        (index) => const RatingWidget(
                          child: Icon(
                            Icons.star_rounded,
                            size: 48,
                          ),
                        ),
                      ),
                      rate: creatorRate.toDouble(),
                      onChanged: (value) {
                        setState(() {
                          creatorRate = value.toInt();
                        });
                      },
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    GetBuilder<LobbyGameScreenController>(
                      builder: (controller) => Expanded(
                        child: GridView.builder(
                          itemCount: controller.players.length,
                          gridDelegate:
                              const SliverGridDelegateWithFixedCrossAxisCount(
                                  crossAxisCount: 3,
                                  mainAxisSpacing: 16,
                                  crossAxisSpacing: 16),
                          itemBuilder: (context, index) =>
                              _player(controller.players[index]),
                        ),
                      ),
                    ),
                    const Spacer(),
                    SizedBox(
                      width: MediaQuery.of(context).size.width * 40 / 100,
                      child: ElevatedButton(
                        onPressed: () {
                          widget.endCallback(creatorRate);
                        },
                        child: Text(
                          'بازگشت',
                          style: context.textTheme.bodyMedium,
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

  Widget _player(LobbyGamePlayerModel model) {
    return Center(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            model.user_index.toString(),
            style: context.textTheme.bodyMedium,
          ),
          const SizedBox(
            width: 4,
          ),
          Column(
            children: [
              Stack(
                children: [
                  // online player
                  CachedNetworkImage(
                    imageUrl: '$appBaseUrl/${model.avatar}',
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
                  ),
                ],
              ),
              SizedBox(
                width: MediaQuery.of(context).size.width * 15 / 100,
                child: Text(
                  textDirection: TextDirection.ltr,
                  textAlign: TextAlign.center,
                  model.name,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: context.textTheme.bodySmall,
                ),
              ),
              const SizedBox(
                height: 2,
              ),
              Text(
                model.character,
                style: context.textTheme.bodySmall,
              )
            ],
          ),
        ],
      ),
    );
  }
}

class EndGameConfirmation extends StatelessWidget {
  const EndGameConfirmation({super.key, required this.endCallback});
  final VoidCallback endCallback;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Center(
      child: SizedBox(
        width: MediaQuery.of(context).size.width * 85 / 100,
        child: ClipRRect(
          borderRadius: BorderRadius.circular(16),
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
            child: Container(
              color: const Color.fromRGBO(102, 102, 102, 0.7),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      'اتمام بازی رو تایید میکنی ؟',
                      style: context.textTheme.bodyLarge,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    ElevatedButton(
                      onPressed: endCallback,
                      child: Text(
                        'تایید اتمام',
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

class PlayerActs extends StatelessWidget {
  const PlayerActs({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Column(
      children: [
        const Padding(
          padding: EdgeInsets.only(left: 16.0),
          child: Align(
            alignment: Alignment.centerLeft,
            child: Icon(
              Icons.swipe_left_rounded,
              color: Colors.white,
            ),
          ),
        ),
        const SizedBox(
          height: 8,
        ),
        Text(
          'اکت بازیکنان',
          style: context.textTheme.titleMedium,
        ),
        const SizedBox(
          height: 8,
        ),
        Expanded(
          child: GetBuilder<LobbyGameScreenController>(
            id: 'players_act',
            builder: (controller) => AnimatedList(
              key: controller.playersActGlobalKey,
              initialItemCount: controller.playerActs.length,
              itemBuilder: (context, index, animation) => animatedListItem(
                  controller.playerActs[index], animation, context),
            ),
          ),
        )
      ],
    );
  }

  static animatedListItem(LobbyGamePlayerModel model,
      Animation<double> animation, BuildContext context) {
    return SizeTransition(
      sizeFactor: animation,
      key: ValueKey(model.avatar),
      child: GetBuilder<LobbyGameScreenController>(
        id: model.user_id,
        builder: (_) => Container(
          margin: const EdgeInsets.all(8),
          padding: const EdgeInsets.all(8),
          decoration: ShapeDecoration(
              shape: ContinuousRectangleBorder(
                borderRadius: BorderRadius.circular(30),
              ),
              color: Colors.grey.shade700),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              Expanded(
                child: SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  reverse: true,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    mainAxisSize: MainAxisSize.max,
                    children: [
                      Visibility(
                        visible: model.status.like,
                        child: Transform.flip(
                          flipX: true,
                          child: Container(
                            padding: const EdgeInsets.all(8),
                            margin: const EdgeInsets.symmetric(horizontal: 8),
                            decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(12),
                                color: Colors.grey.shade500),
                            child: const Icon(
                              Icons.thumb_up_rounded,
                              color: Colors.white,
                            ),
                          ),
                        ),
                      ),
                      Visibility(
                        visible: model.status.dislike,
                        child: Container(
                          padding: const EdgeInsets.all(8),
                          margin: const EdgeInsets.symmetric(horizontal: 8),
                          decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(12),
                              color: Colors.grey.shade500),
                          child: const Icon(
                            Icons.thumb_down_rounded,
                            color: Colors.white,
                          ),
                        ),
                      ),
                      Visibility(
                        visible: model.status.challenge,
                        child: Container(
                          padding: const EdgeInsets.all(8),
                          margin: const EdgeInsets.symmetric(horizontal: 8),
                          decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(12),
                              color: Colors.grey.shade500),
                          child: const Icon(
                            Icons.bolt_rounded,
                            color: Colors.white,
                          ),
                        ),
                      ),
                      Visibility(
                        visible: model.status.hand_rise,
                        child: Container(
                          padding: const EdgeInsets.all(8),
                          margin: const EdgeInsets.symmetric(horizontal: 8),
                          decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(12),
                              color: Colors.grey.shade500),
                          child: const Icon(
                            Icons.front_hand_rounded,
                            color: Colors.white,
                          ),
                        ),
                      ),
                      Visibility(
                        visible: model.status.day_act,
                        child: Container(
                          padding: const EdgeInsets.all(8),
                          margin: const EdgeInsets.symmetric(horizontal: 8),
                          decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(12),
                              color: Colors.grey.shade500),
                          child: const Icon(
                            Icons.online_prediction_rounded,
                            color: Colors.white,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(
                width: 8,
              ),
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Column(
                    children: [
                      CachedNetworkImage(
                        imageUrl: '$appBaseUrl/${model.avatar}',
                        imageBuilder: (context, imageProvider) => Container(
                          width: MediaQuery.of(context).size.width * 15 / 100,
                          height: MediaQuery.of(context).size.width * 15 / 100,
                          decoration: ShapeDecoration(
                              shape: ContinuousRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                    MediaQuery.of(context).size.width *
                                        10 /
                                        100),
                              ),
                              image: DecorationImage(
                                  image: imageProvider, fit: BoxFit.cover)),
                        ),
                        placeholder: (context, url) => Container(
                          width: MediaQuery.of(context).size.width * 15 / 100,
                          height: MediaQuery.of(context).size.width * 15 / 100,
                          decoration: ShapeDecoration(
                              shape: ContinuousRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                    MediaQuery.of(context).size.width *
                                        10 /
                                        100),
                              ),
                              color: Colors.grey),
                        ),
                      ),
                      const SizedBox(
                        height: 4,
                      ),
                      SizedBox(
                        width: MediaQuery.of(context).size.width * 15 / 100,
                        child: Text(
                          model.name,
                          textDirection: TextDirection.rtl,
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                          textAlign: TextAlign.center,
                          style: context.textTheme.bodySmall,
                        ),
                      )
                    ],
                  ),
                  const SizedBox(
                    width: 8,
                  ),
                  Text(
                    model.user_index.toString(),
                    style: context.textTheme.bodyMedium,
                  )
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}

class ObserverList extends StatelessWidget {
  const ObserverList({super.key, required this.observers});
  final List<LobbyGameObserverModel> observers;
  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: MediaQuery.of(context).size.height * 90 / 100,
      child: ClipRRect(
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(16),
          topRight: Radius.circular(16),
        ),
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: Container(
            color: const Color.fromRGBO(102, 102, 102, 0.7),
            child: Padding(
              padding: const EdgeInsets.all(12.0),
              child: GridView.builder(
                itemCount: observers.length,
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 3, mainAxisSpacing: 8, crossAxisSpacing: 8),
                itemBuilder: (context, index) =>
                    _listItem(context, observers[index]),
              ),
            ),
          ),
        ),
      ),
    );
  }

  _listItem(BuildContext context, LobbyGameObserverModel model) {
    return Center(
      child: Column(
        children: [
          CachedNetworkImage(
            imageUrl: '$appBaseUrl/${model.image}',
            imageBuilder: (context, imageProvider) => Container(
              width: MediaQuery.of(context).size.width * 20 / 100,
              height: MediaQuery.of(context).size.width * 20 / 100,
              margin: const EdgeInsets.all(8),
              decoration: ShapeDecoration(
                shape: ContinuousRectangleBorder(
                  borderRadius: BorderRadius.circular(
                      MediaQuery.of(context).size.width * 10 / 100),
                ),
                image: DecorationImage(image: imageProvider, fit: BoxFit.cover),
              ),
            ),
            placeholder: (context, url) => Container(
              width: MediaQuery.of(context).size.width * 20 / 100,
              height: MediaQuery.of(context).size.width * 20 / 100,
              margin: const EdgeInsets.all(8),
              decoration: ShapeDecoration(
                  shape: ContinuousRectangleBorder(
                    borderRadius: BorderRadius.circular(
                        MediaQuery.of(context).size.width * 10 / 100),
                  ),
                  color: Colors.grey),
            ),
          ),
          const SizedBox(
            height: 4,
          ),
          SizedBox(
            width: MediaQuery.of(context).size.width * 20 / 100,
            child: Text(
              maxLines: 1,
              model.name,
              textAlign: TextAlign.center,
              textDirection: TextDirection.rtl,
              overflow: TextOverflow.ellipsis,
            ),
          )
        ],
      ),
    );
  }
}
