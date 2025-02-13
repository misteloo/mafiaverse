// ignore_for_file: deprecated_member_use

import 'dart:ui';

import 'package:bot_toast/bot_toast.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:get/get_state_manager/get_state_manager.dart';
import 'package:mafia/models/lobby/lobby_detail_model.dart';
import 'package:mafia/models/lobby/waiting_lobby_model.dart';
import 'package:mafia/models/profile/check_other_profile_model.dart';
import 'package:mafia/screens/lobby/waiting/lobby_waiting_room_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';
import 'package:mafia/utils/widget/check_player_profile.dart';

import '../../../utils/widget/find_text_direction.dart';

class LobbyWaitingRoomScreen extends GetView<LobbyWaitingRoomScreenController> {
  LobbyWaitingRoomScreen({super.key});
  final ValueNotifier<TextDirection> _textDir =
      ValueNotifier(TextDirection.ltr);

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SafeArea(
      child: Container(
        width: double.infinity,
        height: double.infinity,
        decoration: const BoxDecoration(
          image: DecorationImage(
              image: AssetImage(
                'images/chat_background.jpg',
              ),
              fit: BoxFit.cover),
        ),
        child: WillPopScope(
          onWillPop: () async {
            return false;
          },
          child: Scaffold(
            appBar: AppBar(
              backgroundColor: Colors.transparent,
              title: Text(
                'اتاق گفتو گو',
                style: context.textTheme.bodyMedium,
              ),
              centerTitle: true,
              leading: IconButton(
                onPressed: () {
                  controller.chatSound.toggle();
                },
                icon: Obx(
                  () => Icon(
                    controller.chatSound.value
                        ? Icons.volume_up_rounded
                        : Icons.volume_off,
                    color: Colors.white,
                  ),
                ),
              ),
              actions: [
                IconButton(
                  onPressed: () {
                    Get.dialog(
                      DialogExitGame(
                        deleteGame: false,
                        callback: () async {
                          Get.back();
                          await controller.leaveLobby();
                        },
                      ),
                    );
                  },
                  icon: const Icon(
                    Icons.exit_to_app_rounded,
                    color: Colors.white,
                  ),
                ),
                IconButton(
                  onPressed: () {
                    Get.dialog(
                      const ReportLobby(),
                    );
                  },
                  icon: const Icon(
                    Icons.more_vert_rounded,
                    color: Colors.white,
                  ),
                )
              ],
            ),
            backgroundColor: Colors.transparent,
            body: SizedBox(
              width: double.infinity,
              height: double.infinity,
              child: Column(
                children: [
                  _headerOfChat(context, controller),
                  _chatWidget(),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Container _headerOfChat(
      BuildContext context, LobbyWaitingRoomScreenController controller) {
    return Container(
      padding: const EdgeInsets.all(16),
      width: double.infinity,
      height: 100,
      child: GestureDetector(
        onTap: () {
          showModalBottomSheet(
            isScrollControlled: true,
            context: context,
            builder: (context) => LobbyPlayersDetailData(
              myUserId: controller.myUserId,
              creator: controller.creatorData!,
              controller: controller,
            ),
          );
        },
        child: GetBuilder<LobbyWaitingRoomScreenController>(
          id: 'players',
          builder: (c) => Stack(
            alignment: Alignment.centerLeft,
            children: [
              ...List.generate(
                c.playersData.length > 3 ? 3 : c.playersData.length,
                (index) => Positioned(
                  left: index * 26,
                  child: CachedNetworkImage(
                    imageUrl: '$appBaseUrl/${c.playersData[index].image}',
                    imageBuilder: (context, imageProvider) => CircleAvatar(
                      backgroundImage: imageProvider,
                      radius: 26,
                    ),
                    placeholder: (context, url) => const CircleAvatar(
                      radius: 26,
                      backgroundColor: Colors.grey,
                    ),
                  ),
                ),
              ),
              Obx(
                () => Visibility(
                  visible: controller.isCreator.value,
                  child: Align(
                    alignment: Alignment.centerRight,
                    child: ElevatedButton(
                        onPressed: () {
                          controller.startGame();
                        },
                        child: Text(
                          'شروع بازی',
                          style: context.textTheme.bodyMedium,
                        )),
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  Expanded _chatWidget() {
    return Expanded(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          Expanded(
            child: GetBuilder<LobbyWaitingRoomScreenController>(
              id: 'chat',
              builder: (_) => ChatView(
                controller: controller,
              ),
            ),
          ),
          Row(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Obx(
                  () {
                    var status = controller.micToggleStatus.value;

                    return FloatingActionButton(
                      heroTag: 'fabMicToggle',
                      backgroundColor: status ? Colors.green : null,
                      onPressed: () {
                        controller.micToggle();
                      },
                      mini: true,
                      child: const Icon(
                        Icons.mic_rounded,
                        color: Colors.black,
                      ),
                    );
                  },
                ),
              ),
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: ClipRRect(
                    child: BackdropFilter(
                      filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        decoration: BoxDecoration(
                          color: const Color.fromRGBO(102, 102, 102, 0.5),
                          borderRadius: BorderRadius.circular(16),
                        ),
                        child: ValueListenableBuilder<TextDirection>(
                          valueListenable: _textDir,
                          builder: (context, value, child) => TextField(
                            controller: controller.textEditingController,
                            decoration: const InputDecoration(
                              border: InputBorder.none,
                              enabledBorder: InputBorder.none,
                              focusedBorder: InputBorder.none,
                            ),
                            textInputAction: TextInputAction.newline,
                            keyboardType: TextInputType.multiline,
                            textAlign: TextAlign.justify,
                            maxLines: null,
                            textDirection: value,
                            onChanged: (input) {
                              if (input.trim().length < 2) {
                                final dir = getDirection(input);
                                if (dir != value) _textDir.value = dir;
                              }
                            },
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: FloatingActionButton(
                  heroTag: 'fabSendMsg',
                  onPressed: () {
                    controller.chat();
                  },
                  mini: true,
                  child: const Icon(
                    Icons.send_rounded,
                    color: Colors.black,
                  ),
                ),
              )
            ],
          ),
        ],
      ),
    );
  }
}

class LobbyPlayersDetailData extends StatelessWidget {
  const LobbyPlayersDetailData(
      {super.key,
      required this.myUserId,
      required this.creator,
      required this.controller});
  final LobbyDetailPlayerModel creator;

  final LobbyWaitingRoomScreenController controller;
  final String? myUserId;
  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: MediaQuery.of(context).size.height * 90 / 100,
      child: Scaffold(
        body: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            children: [
              const SizedBox(
                width: double.infinity,
              ),
              Text(
                'سازنده لابی',
                style: context.textTheme.titleMedium,
              ),
              const SizedBox(
                height: 8,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  GetBuilder<LobbyWaitingRoomScreenController>(
                    id: creator.user_id,
                    builder: (_) =>
                        controller.specificPlayerSpeechStatus(creator.user_id)
                            ? const Icon(
                                Icons.mic_rounded,
                                color: Colors.white,
                              )
                            : Icon(
                                Icons.mic_off_rounded,
                                color: Colors.grey.shade700,
                              ),
                  ),
                  const SizedBox(
                    width: 8,
                  ),
                  CachedNetworkImage(
                    imageUrl: '$appBaseUrl/${creator.image}',
                    imageBuilder: (context, imageProvider) => Container(
                      width: MediaQuery.of(context).size.width * 20 / 100,
                      height: MediaQuery.of(context).size.width * 20 / 100,
                      decoration: ShapeDecoration(
                        shape: ContinuousRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              MediaQuery.of(context).size.width * 12 / 100),
                        ),
                        image: DecorationImage(
                            image: imageProvider, fit: BoxFit.cover),
                      ),
                    ),
                    placeholder: (context, url) => Container(
                      width: MediaQuery.of(context).size.width * 20 / 100,
                      height: MediaQuery.of(context).size.width * 20 / 100,
                      decoration: ShapeDecoration(
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                MediaQuery.of(context).size.width * 10 / 100),
                          ),
                          color: Colors.grey),
                    ),
                  ),
                ],
              ),
              const SizedBox(
                height: 8,
              ),
              Text(
                creator.name,
                style: context.textTheme.bodyMedium,
              ),
              const SizedBox(
                height: 16,
              ),
              Text(
                'بازیکنان حاضر درلابی',
                style: context.textTheme.titleMedium,
              ),
              const SizedBox(
                height: 16,
              ),
              Expanded(
                child: AnimatedList(
                  initialItemCount: controller.playersData.length,
                  key: controller.playersGlobalKey,
                  itemBuilder: (context, index, animation) => _listItem(
                    animation,
                    controller.playersData[index],
                    context,
                    controller,
                    () {
                      // kick player
                      controller
                          .kickPlayer(controller.playersData[index].user_id);
                      // update list
                      var current = controller.playersData[index];
                      controller.playersGlobalKey.currentState!.removeItem(
                        index,
                        (context, animation) => _listItem(
                          animation,
                          current,
                          context,
                          controller,
                          () {},
                        ),
                        duration: const Duration(milliseconds: 250),
                      );
                    },
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  Widget _listItem(
      Animation<double> animation,
      LobbyDetailPlayerModel model,
      BuildContext context,
      LobbyWaitingRoomScreenController controller,
      VoidCallback kick) {
    return SizeTransition(
      sizeFactor: animation,
      child: Container(
        margin: const EdgeInsets.all(8),
        width: double.infinity,
        decoration: ShapeDecoration(
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(50),
            ),
            color: Colors.grey.shade800),
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Row(
            children: [
              Visibility(
                visible: myUserId == creator.user_id,
                child: IconButton(
                  onPressed: kick,
                  icon: const Icon(
                    Icons.delete_rounded,
                    color: Colors.white,
                  ),
                ),
              ),
              Expanded(
                  child: Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      SizedBox(
                        width: MediaQuery.of(context).size.width * 15 / 100,
                        child: Text(
                          model.name,
                          style: context.textTheme.bodyMedium,
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                          textAlign: TextAlign.end,
                        ),
                      ),
                      const SizedBox(
                        height: 8,
                      ),
                      GetBuilder<LobbyWaitingRoomScreenController>(
                        id: model.user_id,
                        builder: (controller) =>
                            controller.specificPlayerSpeechStatus(model.user_id)
                                ? const Icon(
                                    Icons.mic_rounded,
                                    color: Colors.white,
                                  )
                                : Icon(
                                    Icons.mic_off_rounded,
                                    color: Colors.grey.shade700,
                                  ),
                      )
                    ],
                  ),
                  const SizedBox(
                    width: 12,
                  ),
                  CachedNetworkImage(
                    imageUrl: '$appBaseUrl/${model.image}',
                    imageBuilder: (context, imageProvider) => Container(
                      width: MediaQuery.of(context).size.width * 15 / 100,
                      height: MediaQuery.of(context).size.width * 15 / 100,
                      decoration: ShapeDecoration(
                        shape: ContinuousRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              MediaQuery.of(context).size.width * 10 / 100),
                        ),
                        image: DecorationImage(image: imageProvider),
                      ),
                    ),
                  ),
                  const SizedBox(
                    width: 4,
                  ),
                  IconButton(
                    onPressed: () {
                      controller.checkProfile(
                        userId: model.user_id,
                        profileCallback:
                            (CheckOtherProfileModel profileCallback) {
                          showModalBottomSheet(
                            backgroundColor: Colors.transparent,
                            context: context,
                            builder: (context) =>
                                CheckPlayerProfile(profile: profileCallback),
                          );
                        },
                      );
                    },
                    icon: const Icon(
                      Icons.remove_red_eye,
                      color: Colors.grey,
                    ),
                  )
                ],
              ))
            ],
          ),
        ),
      ),
    );
  }
}

class DialogExitGame extends StatelessWidget {
  const DialogExitGame(
      {super.key, required this.deleteGame, required this.callback});
  final VoidCallback callback;
  final bool deleteGame;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: MediaQuery.of(context).size.width * 85 / 100,
      child: Center(
        child: ClipRRect(
          borderRadius: BorderRadius.circular(16),
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
            child: Container(
              color: const Color.fromRGBO(102, 102, 102, 0.5),
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      deleteGame ? 'حذف لابی' : 'خروج',
                      style: context.textTheme.bodyLarge,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Text(
                      deleteGame
                          ? 'مطمئنی قصد حذف لابی رو داری ؟'
                          : 'از لابی خارح میشی ؟',
                      style: context.textTheme.bodyMedium,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    SizedBox(
                      width: MediaQuery.of(context).size.width * 40 / 100,
                      child: ElevatedButton(
                        onPressed: () {
                          callback();
                        },
                        child: Text(
                          'تایید',
                          style: context.textTheme.bodyMedium,
                        ),
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

class ChatView extends StatefulWidget {
  const ChatView({super.key, required this.controller});
  final LobbyWaitingRoomScreenController controller;

  @override
  State<ChatView> createState() => _ChatViewState();
}

class _ChatViewState extends State<ChatView> {
  ScrollController? _scrollController;
  @override
  void initState() {
    _scrollController?.animateTo(_scrollController!.position.maxScrollExtent,
        duration: const Duration(milliseconds: 500),
        curve: Curves.fastOutSlowIn);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<LobbyWaitingRoomScreenController>(
      id: 'chat',
      builder: (_) => AnimatedList(
        reverse: true,
        controller: _scrollController,
        key: widget.controller.chatListKey,
        itemBuilder: (context, index, animation) => SlideTransition(
          key: ValueKey(widget.controller.chatList[index].avatar),
          position: Tween<Offset>(begin: const Offset(0, 0.5), end: Offset.zero)
              .animate(animation),
          child: _animatedItemList(widget.controller.chatList[index], context),
        ),
      ),
    );
  }

  Widget _animatedItemList(LobbyWaitingMessageModel msg, BuildContext context) {
    return Align(
      alignment: msg.self
          ? Alignment.centerRight
          : msg.is_system
              ? Alignment.center
              : Alignment.centerLeft,
      child: msg.is_system
          ?
          // system
          Container(
              padding: const EdgeInsets.all(8),
              margin: const EdgeInsets.all(8),
              constraints: BoxConstraints(
                maxWidth: MediaQuery.of(context).size.width * 70 / 100,
              ),
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(16), color: Colors.grey),
              child: Column(
                children: [
                  Visibility(
                    visible: msg.avatar.isNotEmpty,
                    child: CachedNetworkImage(
                      imageUrl: '$appBaseUrl/${msg.avatar}',
                      imageBuilder: (context, imageProvider) => CircleAvatar(
                        radius: 25,
                        backgroundImage: imageProvider,
                      ),
                      placeholder: (context, url) => const CircleAvatar(
                        radius: 25,
                        backgroundColor: Colors.grey,
                      ),
                    ),
                  ),
                  Text(
                    msg.msg,
                    style: TextStyle(
                        fontFamily: 'shabnam',
                        fontSize: 14.sp,
                        color: Colors.black),
                  )
                ],
              ),
            )
          :
          //people
          Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment:
                  msg.self ? MainAxisAlignment.end : MainAxisAlignment.start,
              children: [
                Visibility(
                  visible: msg.self ? false : true,
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: CachedNetworkImage(
                      imageUrl: '$appBaseUrl/${msg.avatar}',
                      imageBuilder: (context, imageProvider) => CircleAvatar(
                        radius: 25,
                        backgroundImage: imageProvider,
                      ),
                      placeholder: (context, url) => const CircleAvatar(
                        radius: 25,
                        backgroundColor: Colors.grey,
                      ),
                    ),
                  ),
                ),
                Container(
                  constraints: BoxConstraints(
                    maxWidth: MediaQuery.of(context).size.width * 65 / 100,
                  ),
                  margin: const EdgeInsets.all(8),
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(14),
                    color: msg.self ? Colors.black54 : Colors.white54,
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(4.0),
                    child: Column(
                      crossAxisAlignment: msg.self
                          ? CrossAxisAlignment.end
                          : CrossAxisAlignment.start,
                      children: [
                        Text(
                          msg.name,
                          style: msg.self
                              ? context.textTheme.titleMedium
                              : TextStyle(
                                  fontFamily: 'shabnam',
                                  fontSize: 14.sp,
                                  color: Colors.black),
                        ),
                        Text(
                          msg.msg,
                          textAlign: TextAlign.justify,
                          style: TextStyle(
                              fontFamily: 'shabnam',
                              fontSize: 14.sp,
                              color: msg.self ? Colors.white : Colors.white),
                          textDirection: TextDirection.rtl,
                        ),
                      ],
                    ),
                  ),
                ),
                Visibility(
                  visible: msg.self ? true : false,
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: CachedNetworkImage(
                      imageUrl: '$appBaseUrl/${msg.avatar}',
                      imageBuilder: (context, imageProvider) => CircleAvatar(
                        radius: 25,
                        backgroundImage: imageProvider,
                      ),
                      placeholder: (context, url) => const CircleAvatar(
                        radius: 25,
                        backgroundColor: Colors.grey,
                      ),
                    ),
                  ),
                )
              ],
            ),
    );
  }
}

class ReportLobby extends StatelessWidget {
  const ReportLobby({super.key});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: MediaQuery.of(context).size.width * 85 / 100,
      child: Center(
        child: ClipRRect(
          borderRadius: BorderRadius.circular(16),
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
            child: Container(
              color: const Color.fromRGBO(102, 102, 102, 0.5),
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      'گزارش',
                      style: context.textTheme.bodyLarge,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Text(
                      'گزارش محتوای گفتوگو',
                      style: context.textTheme.bodyMedium,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    SizedBox(
                      width: MediaQuery.of(context).size.width * 40 / 100,
                      child: ElevatedButton(
                        onPressed: () {
                          BotToast.showText(text: 'گزارش شما ارسال شد');
                          Get.back();
                        },
                        child: Text(
                          'ارسال',
                          style: context.textTheme.bodyMedium,
                        ),
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
