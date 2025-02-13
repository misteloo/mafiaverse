import 'dart:ui';

import 'package:bot_toast/bot_toast.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:easy_refresh/easy_refresh.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter_expandable_fab/flutter_expandable_fab.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/models/lobby/lobby_list_model.dart';
import 'package:mafia/screens/lobby/lobby_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';
import 'package:mafia/utils/widget/check_player_profile.dart';

import '../../models/profile/check_other_profile_model.dart';
import '../../utils/widget/find_text_direction.dart';

class LobbyScreen extends GetView<LobbyScreenController> {
  const LobbyScreen({super.key});
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);

    return DefaultTabController(
      length: 3,
      initialIndex: 2,
      child: Scaffold(
        appBar: AppBar(
          title: Text(
            'لابی',
            style: context.textTheme.bodyLarge,
          ),
          centerTitle: true,
          bottom: TabBar(
            dividerColor: Colors.transparent,
            indicatorColor: Colors.cyan,
            tabs: [
              Tab(
                child: Text(
                  'من',
                  style: context.textTheme.bodyMedium,
                ),
              ),
              Tab(
                child: Text(
                  'در حال بازی',
                  style: context.textTheme.bodyMedium,
                ),
              ),
              Tab(
                child: Text(
                  'عضو گیری',
                  style: context.textTheme.bodyMedium,
                ),
              ),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            LobbySelfGames(controller: controller),
            LobbyStartedGames(controller: controller),
            LobbyUnStartedGames(controller: controller),
          ],
        ),
      ),
    );
  }
}

class LobbySelfGames extends StatelessWidget {
  const LobbySelfGames({super.key, required this.controller});
  final LobbyScreenController controller;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      body: EasyRefresh(
        callRefreshOverOffset: 100,
        refreshOnStart: true,
        header: const MaterialHeader(
          backgroundColor: Colors.cyan,
        ),
        controller: controller.scrollController,
        onRefresh: () {
          controller.getLobbyList(type: 'self');
        },
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: NotificationListener<UserScrollNotification>(
            onNotification: (notification) {
              final ScrollDirection direction = notification.direction;
              if (direction == ScrollDirection.reverse) {
                controller.showFab.value = false;
                controller.showFab.refresh();
              } else if (direction == ScrollDirection.forward) {
                controller.showFab.value = true;
                controller.showFab.refresh();
              }
              return true;
            },
            child: LobbyListItem(
              controller: controller,
              type: 'self',
            ),
          ),
        ),
      ),
    );
  }
}

class LobbyStartedGames extends StatelessWidget {
  const LobbyStartedGames({super.key, required this.controller});
  final LobbyScreenController controller;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      body: EasyRefresh(
        callRefreshOverOffset: 100,
        refreshOnStart: true,
        header: const MaterialHeader(
          backgroundColor: Colors.cyan,
        ),
        controller: controller.scrollController,
        onRefresh: () {
          controller.getLobbyList(type: 'started');
        },
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: NotificationListener<UserScrollNotification>(
            onNotification: (notification) {
              final ScrollDirection direction = notification.direction;
              if (direction == ScrollDirection.reverse) {
                controller.showFab.value = false;
                controller.showFab.refresh();
              } else if (direction == ScrollDirection.forward) {
                controller.showFab.value = true;
                controller.showFab.refresh();
              }
              return true;
            },
            child: LobbyListItem(
              controller: controller,
              type: 'started',
            ),
          ),
        ),
      ),
    );
  }
}

class LobbyUnStartedGames extends StatelessWidget {
  const LobbyUnStartedGames({super.key, required this.controller});
  final LobbyScreenController controller;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      floatingActionButtonLocation: ExpandableFab.location,
      floatingActionButton: Obx(
        () => AnimatedSlide(
          duration: const Duration(milliseconds: 150),
          offset: controller.showFab.value ? Offset.zero : const Offset(0, 0.5),
          child: AnimatedOpacity(
            duration: const Duration(milliseconds: 150),
            opacity: controller.showFab.value ? 1 : 0,
            child: ExpandableFab(
              openButtonBuilder: RotateFloatingActionButtonBuilder(
                child: const Icon(
                  Icons.add,
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
                  heroTag: 'fabPrivateLobby',
                  onPressed: () {
                    controller.navigateToCreateLobby(true);
                  },
                  icon: const Icon(
                    Icons.private_connectivity,
                    color: Colors.black,
                  ),
                  label: const Text(
                    'خصوصی',
                    style: TextStyle(
                        fontFamily: 'shabnam',
                        fontSize: 14,
                        color: Colors.black),
                  ),
                ),
                FloatingActionButton.extended(
                  heroTag: 'fabPublicLobby',
                  onPressed: () {
                    controller.navigateToCreateLobby(false);
                  },
                  icon: const Icon(
                    Icons.public_rounded,
                    color: Colors.black,
                  ),
                  label: const Text(
                    'عمومی',
                    style: TextStyle(
                        fontFamily: 'shabnam',
                        fontSize: 14,
                        color: Colors.black),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
      body: EasyRefresh(
        callRefreshOverOffset: 100,
        refreshOnStart: true,
        header: const MaterialHeader(
          backgroundColor: Colors.cyan,
        ),
        controller: controller.scrollController,
        onRefresh: () {
          controller.getLobbyList(type: 'un_started');
        },
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: NotificationListener<UserScrollNotification>(
            onNotification: (notification) {
              final ScrollDirection direction = notification.direction;
              if (direction == ScrollDirection.reverse) {
                controller.showFab.value = false;
                controller.showFab.refresh();
              } else if (direction == ScrollDirection.forward) {
                controller.showFab.value = true;
                controller.showFab.refresh();
              }
              return true;
            },
            child: LobbyListItem(
              controller: controller,
              type: 'un_started',
            ),
          ),
        ),
      ),
    );
  }
}

class LobbyListItem extends StatelessWidget {
  const LobbyListItem(
      {super.key, required this.controller, required this.type});
  final LobbyScreenController controller;
  final String type;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return AnimationLimiter(
      child: GetBuilder<LobbyScreenController>(
        id: type == 'un_started'
            ? 'unStartedList'
            : type == 'started'
                ? 'startedList'
                : 'selfList',
        builder: (_) => ListView.builder(
          itemCount: type == 'un_started'
              ? controller.unStartedGamesList.length
              : type == 'started'
                  ? controller.startedGamesList.length
                  : controller.selfGamesList.length,
          itemBuilder: (context, index) {
            late List<LobbyListModel> list;
            switch (type) {
              case 'un_started':
                list = controller.unStartedGamesList;
                break;
              case 'started':
                list = controller.startedGamesList;
                break;
              case 'self':
                list = controller.selfGamesList;
            }
            return AnimationConfiguration.staggeredList(
              position: index,
              child: SlideAnimation(
                verticalOffset: 100,
                duration: const Duration(milliseconds: 450),
                child: FadeInAnimation(
                  child: _listItem(list[index], context),
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  Widget _listItem(LobbyListModel model, BuildContext context) {
    return GetBuilder<LobbyScreenController>(
      id: model.lobby_id,
      builder: (_) => Container(
        margin: const EdgeInsets.all(8),
        width: double.infinity,
        decoration: ShapeDecoration(
          shape: ContinuousRectangleBorder(
            borderRadius: BorderRadius.circular(60),
            side: const BorderSide(width: 2, color: Colors.grey),
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            children: [
              Row(
                children: [
                  Expanded(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          '${model.players.length}',
                          style: TextStyle(
                              fontFamily: 'shabnam',
                              fontSize: 20.sp,
                              color: Colors.grey),
                        ),
                        const Text('  /  '),
                        Text(
                          '${model.player_cnt}',
                          style: TextStyle(
                              fontFamily: 'shabnam',
                              fontSize: 30.sp,
                              color: Colors.white),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        Text(
                          model.name,
                          style: context.textTheme.bodyLarge,
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        Text(
                          'سناریو : ${model.scenario}',
                          style: context.textTheme.titleMedium,
                          textAlign: TextAlign.center,
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        Icon(
                          model.private
                              ? Icons.privacy_tip_rounded
                              : Icons.public_rounded,
                          color: Colors.grey,
                        )
                      ],
                    ),
                  ),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        InkWell(
                          borderRadius: BorderRadius.circular(
                              MediaQuery.of(context).size.width * 10 / 100),
                          onTap: () {
                            // check creator profile
                            controller.checkProfile(
                              lobby: model,
                              profileCallback:
                                  (CheckOtherProfileModel profile) {
                                // check profile
                                showModalBottomSheet(
                                  backgroundColor: Colors.transparent,
                                  context: context,
                                  builder: (context) =>
                                      // bs
                                      CheckPlayerProfile(profile: profile),
                                );
                              },
                            );
                          },
                          child: model.creator.loadingProfile
                              // profile
                              ? SizedBox(
                                  width: MediaQuery.of(context).size.width *
                                      15 /
                                      100,
                                  height: MediaQuery.of(context).size.width *
                                      15 /
                                      100,
                                  child: Center(
                                    child:
                                        LoadingAnimationWidget.prograssiveDots(
                                            color: Colors.white, size: 26),
                                  ),
                                )
                              : CachedNetworkImage(
                                  imageUrl:
                                      '$appBaseUrl/${model.creator.image}',
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
                                          image: imageProvider,
                                          fit: BoxFit.cover),
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
                                              MediaQuery.of(context)
                                                      .size
                                                      .width *
                                                  10 /
                                                  100),
                                        ),
                                        color: Colors.grey),
                                  ),
                                ),
                        ),
                        const SizedBox(
                          height: 8,
                        ),
                        Text(
                          model.creator.name,
                          style: context.textTheme.bodyMedium,
                          textAlign: TextAlign.center,
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
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Visibility(
                    visible: type == 'started',
                    child: ElevatedButton(
                      onPressed: () {
                        controller.joinToGameAsObserver(model.lobby_id);
                      },
                      child: model.loading
                          ? LoadingAnimationWidget.prograssiveDots(
                              color: Colors.black, size: 30)
                          : Text(
                              'مشاهده',
                              style: context.textTheme.bodyMedium,
                            ),
                    ),
                  ),
                  Visibility(
                    visible:
                        type == 'un_started' || type == 'self' ? true : false,
                    child: ElevatedButton(
                      onPressed: () {
                        if (model.private && type == 'un_started') {
                          showModalBottomSheet(
                            backgroundColor: Colors.transparent,
                            isScrollControlled: true,
                            context: context,
                            builder: (context) => _password(
                              editingController: controller.passwordTextEditing,
                              context: context,
                              callback: (password) {
                                Get.back();
                                if (type == 'self') {
                                  controller.reconnectToGame(model.lobby_id);
                                } else {
                                  controller.joinToGame(model, password);
                                }
                              },
                            ),
                          );
                        } else {
                          if (type == 'self') {
                            controller.reconnectToGame(model.lobby_id);
                          } else {
                            controller.joinToGame(model, null);
                          }
                        }
                      },
                      child: model.loading
                          ? LoadingAnimationWidget.prograssiveDots(
                              color: Colors.black, size: 30)
                          : Text(
                              'پیوستن',
                              style: context.textTheme.bodyMedium,
                            ),
                    ),
                  ),
                ],
              ),
              const SizedBox(
                height: 8,
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _password(
      {required BuildContext context,
      required TextEditingController editingController,
      required Function(String password) callback}) {
    final ValueNotifier<TextDirection> textDir =
        ValueNotifier(TextDirection.ltr);
    return Padding(
      padding:
          EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(16),
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
          child: Container(
            width: double.infinity,
            color: const Color.fromRGBO(102, 102, 102, 0.5),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const SizedBox(
                  height: 16,
                ),
                Text(
                  'لابی دارای رمز است',
                  style: context.textTheme.bodyMedium,
                ),
                const SizedBox(
                  height: 16,
                ),
                SizedBox(
                  width: MediaQuery.of(context).size.width * 70 / 100,
                  child: ValueListenableBuilder<TextDirection>(
                    valueListenable: textDir,
                    builder: (context, value, child) => Directionality(
                      textDirection: TextDirection.rtl,
                      child: TextField(
                        controller: editingController,
                        decoration: InputDecoration(
                          labelText: 'رمز ورود به لابی',
                          labelStyle: TextStyle(
                            fontFamily: 'shabnam',
                            fontSize: 14.sp,
                            color: Colors.white,
                          ),
                        ),
                        textDirection: value,
                        obscureText: true,
                        onChanged: (input) {
                          if (input.trim().length < 2) {
                            final dir = getDirection(input);
                            if (dir != value) textDir.value = dir;
                          }
                        },
                      ),
                    ),
                  ),
                ),
                const SizedBox(
                  height: 16,
                ),
                ElevatedButton(
                  onPressed: () {
                    if (editingController.text.isNotEmpty) {
                      callback(editingController.text);
                    } else {
                      BotToast.showText(text: 'رمز وارد نشده');
                    }
                  },
                  child: Text(
                    'تایید رمز',
                    style: context.textTheme.bodyMedium,
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
    );
  }
}
