// ignore_for_file: deprecated_member_use

import 'dart:math';
import 'dart:ui';

import 'package:audioplayers/audioplayers.dart';
import 'package:bot_toast/bot_toast.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/models/lobby/last_card_model.dart';
import 'package:mafia/screens/lobby/create/create_lobby_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';

import '../../../models/lobby/lobby_character_model.dart';

class CreateLobbyScreen extends GetView<CreateLobbyScreenController> {
  const CreateLobbyScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);

    return Scaffold(
      appBar: AppBar(
        title: Text(
          'ساخت لابی',
          style: context.textTheme.bodyLarge,
        ),
        centerTitle: true,
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: SingleChildScrollView(
          child: Column(
            children: [
              const SizedBox(
                height: 16,
              ),
              Row(
                children: [
                  Expanded(
                    child: Directionality(
                      textDirection: TextDirection.rtl,
                      child: TextField(
                        controller: controller.lobbyScenarioController,
                        decoration: InputDecoration(
                          border: context.theme.inputDecorationTheme.border,
                          label: Text(
                            'اسم سناریو',
                            style: context.textTheme.titleMedium,
                          ),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(
                    width: 8,
                  ),
                  Expanded(
                    child: Directionality(
                      textDirection: TextDirection.rtl,
                      child: TextField(
                        controller: controller.lobbyNameController,
                        decoration: InputDecoration(
                          border: context.theme.inputDecorationTheme.border,
                          label: Text(
                            'اسم لابی',
                            style: context.textTheme.titleMedium,
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
              // password
              Visibility(
                visible: controller.privateLobby(),
                child: Column(
                  children: [
                    const SizedBox(
                      height: 8,
                    ),
                    Directionality(
                      textDirection: TextDirection.rtl,
                      child: TextField(
                        controller: controller.lobbyPassWordController,
                        decoration: InputDecoration(
                            border: context.theme.inputDecorationTheme.border,
                            label: Text(
                              'رمز لابی',
                              style: context.textTheme.titleMedium,
                            ),
                            counterStyle: context.textTheme.titleMedium),
                        maxLength: 8,
                      ),
                    )
                  ],
                ),
              ),
              // player count
              const SizedBox(
                height: 8,
              ),
              Text(
                'تعداد بازیکنان لابی رو مشخص کن',
                style: context.textTheme.titleMedium,
              ),
              const SizedBox(
                height: 8,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  Container(
                    padding: const EdgeInsets.all(8),
                    width: MediaQuery.of(context).size.width * 20 / 100,
                    height: MediaQuery.of(context).size.width * 20 / 100,
                    decoration: ShapeDecoration(
                      shape: ContinuousRectangleBorder(
                        borderRadius: BorderRadius.circular(
                            MediaQuery.of(context).size.width * 10 / 100),
                        side: const BorderSide(color: Colors.grey),
                      ),
                    ),
                    child: Center(
                      child: Image.asset('images/citizen_hat.png'),
                    ),
                  ),
                  Obx(
                    () => Text(
                      '${controller.playerCount}',
                      style: context.textTheme.bodyLarge,
                    ),
                  ),
                  FloatingActionButton(
                    heroTag: 'fabMinusPlayer',
                    onPressed: () {
                      controller.decPlayer();
                    },
                    mini: true,
                    child: const Icon(Icons.remove, color: Colors.black),
                  ),
                  FloatingActionButton(
                    heroTag: 'fabAddPlayer',
                    onPressed: () {
                      controller.incPlayer();
                    },
                    mini: true,
                    child: const Icon(Icons.add, color: Colors.black),
                  )
                ],
              ),
              // side
              DefineGameSide(
                controller: controller,
                openBs: () {
                  showModalBottomSheet(
                      isScrollControlled: true,
                      context: context,
                      builder: (context) => _enterGameSideBs(
                            context: context,
                            callback: (name) {
                              if (controller.sideNameList.contains(name) ==
                                  false) {
                                Get.back();

                                controller.sideNameList.add(name);
                                // update list
                                controller.gameSideKey.currentState!.insertItem(
                                  0,
                                  duration: const Duration(milliseconds: 250),
                                );
                              } else {
                                BotToast.showText(
                                    text: 'این ساید قبلا تعریف شده');
                              }
                            },
                          ),
                      backgroundColor: Colors.transparent);
                },
              ),
              // deck
              _createDeck(context),
              // last movement card
              _createLastMove(context),

              const SizedBox(
                height: 16,
              ),
              SizedBox(
                width: 120,
                child: ElevatedButton(
                  onPressed: () {
                    controller.createGame();
                  },
                  child: Text(
                    'ایجاد لابی',
                    style: context.textTheme.bodyMedium,
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  Widget _enterGameSideBs(
      {required BuildContext context,
      required Function(String name) callback}) {
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
            color: const Color.fromRGBO(102, 102, 102, 0.5),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const SizedBox(
                    width: double.infinity,
                  ),
                  Text(
                    'ایجاد ساید های بازی',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Directionality(
                    textDirection: TextDirection.rtl,
                    child: TextField(
                      controller: controller.createGameSideEditingController,
                      decoration: InputDecoration(
                        border: context.theme.inputDecorationTheme.border,
                        label: Text(
                          'ساید های بازی را تعریف کنید',
                          style: context.textTheme.titleMedium,
                        ),
                      ),
                      maxLines: 1,
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  SizedBox(
                    width: 120,
                    child: ElevatedButton(
                      onPressed: () {
                        if (controller
                            .createGameSideEditingController.text.isNotEmpty) {
                          callback(
                              controller.createGameSideEditingController.text);
                        }
                      },
                      child: Text(
                        'تایید',
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
    );
  }

  Widget _createLastMove(BuildContext context) {
    return Column(
      children: [
        const SizedBox(
          height: 8,
        ),
        Text(
          'ایجاد کارت حرکت آخر',
          style: context.textTheme.titleMedium,
        ),
        const SizedBox(
          height: 8,
        ),
        SizedBox(
          height: 100,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.end,
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisSize: MainAxisSize.max,
            children: [
              Expanded(
                child: Obx(
                  () => AnimatedList(
                    scrollDirection: Axis.horizontal,
                    key: controller.lastCardKey,
                    initialItemCount: controller.lastMoveCardList.length,
                    itemBuilder: (context, index, animation) => SlideTransition(
                      key: ValueKey(controller.lastMoveCardList[index].name),
                      position: Tween<Offset>(
                              begin: const Offset(0.5, 0), end: Offset.zero)
                          .animate(animation),
                      child: _animatedItemList(
                          controller.lastMoveCardList[index], context, () {
                        var currentlyRemoving =
                            controller.lastMoveCardList[index];
                        controller.lastMoveCardList.removeAt(index);
                        controller.lastCardKey.currentState!.removeItem(
                          duration: const Duration(milliseconds: 250),
                          index,
                          (context, animation) => SlideTransition(
                            key: ValueKey(currentlyRemoving.name),
                            position: Tween<Offset>(
                                    begin: const Offset(1, 0), end: Offset.zero)
                                .animate(animation),
                            child: _animatedItemList(
                              currentlyRemoving,
                              context,
                              () {},
                            ),
                          ),
                        );
                      }),
                    ),
                  ),
                ),
              ),
              const SizedBox(
                width: 8,
              ),
              FloatingActionButton(
                onPressed: () {
                  // bs
                  showModalBottomSheet(
                      isScrollControlled: true,
                      context: context,
                      builder: (context) => CreateLastMoveCard(
                            controller: controller,
                          ),
                      backgroundColor: Colors.transparent);
                },
                heroTag: 'fabAddLastCard',
                child: const Icon(
                  Icons.style_rounded,
                  color: Colors.black,
                ),
              )
            ],
          ),
        ),
      ],
    );
  }

  Widget _createDeck(BuildContext context) {
    return Column(
      children: [
        const SizedBox(
          height: 8,
        ),
        Text(
          'انتخاب یا ایجاد دسته کارت بازی',
          style: context.textTheme.titleMedium,
        ),
        const SizedBox(
          height: 8,
        ),
        SizedBox(
          height: 130,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.end,
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisSize: MainAxisSize.max,
            children: [
              Expanded(
                child: Obx(
                  () => ListView.builder(
                    scrollDirection: Axis.horizontal,
                    itemBuilder: (context, index) => Center(
                      child: Container(
                        width: 100,
                        height: 100,
                        margin: const EdgeInsets.all(8),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.spaceAround,
                          children: [
                            Text(
                              controller.characterList[index].name,
                              style: context.textTheme.titleSmall,
                            ),
                            controller.characterList[index].custom
                                ? const Icon(Icons.question_mark_rounded,
                                    color: Colors.grey, size: 40)
                                : CachedNetworkImage(
                                    imageUrl:
                                        '$appBaseUrl/${controller.characterList[index].icon}',
                                    imageBuilder: (context, imageProvider) =>
                                        Image(
                                      image: imageProvider,
                                      width: 40,
                                      height: 40,
                                    ),
                                  ),
                            Text(
                              controller.characterList[index].count.toString(),
                              style: context.textTheme.bodyMedium,
                            )
                          ],
                        ),
                      ),
                    ),
                    itemCount: controller.characterList
                        .where((p0) => p0.count > 0)
                        .toList()
                        .length,
                  ),
                ),
              ),
              const SizedBox(
                width: 8,
              ),
              GetBuilder<CreateLobbyScreenController>(
                id: 'deckLoading',
                builder: (_) => FloatingActionButton(
                  onPressed: () async {
                    if (controller.playerCount.value == 0) {
                      BotToast.showText(text: 'تعداد بازیکنان رو مشخص کن');
                      return;
                    } else if (controller.sideNameList.isEmpty) {
                      BotToast.showText(text: 'ساید های بازی رو تعریف کن');
                      return;
                    }
                    await controller.getCharacters();
                    if (!context.mounted) return;

                    showModalBottomSheet(
                      useSafeArea: true,
                      isScrollControlled: true,
                      backgroundColor: Colors.grey.shade900,
                      context: context,
                      isDismissible: false,
                      builder: (context) {
                        return CreateLobbyDeck(
                          controller: controller,
                          characters: controller.characterList,
                          playerCount: controller.playerCount.value,
                          deckList: (List<LobbyCharacterModel> decks) {
                            Get.back();
                            controller.createDeck(decks);
                          },
                        );
                      },
                    );
                  },
                  heroTag: 'fabAddDeck',
                  child: !controller.deckLoading
                      ? const Icon(
                          Icons.style_rounded,
                          color: Colors.black,
                        )
                      : LoadingAnimationWidget.prograssiveDots(
                          color: Colors.black, size: 26),
                ),
              )
            ],
          ),
        ),
      ],
    );
  }

  Widget _animatedItemList(LastMoveCardModel lastMoveCard, BuildContext context,
      VoidCallback? deleteCallback) {
    return Container(
      width: 80,
      height: 80,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: Colors.grey, width: 2),
      ),
      margin: const EdgeInsets.all(8),
      padding: const EdgeInsets.all(8),
      child: InkWell(
        onTap: deleteCallback,
        child: Center(
          child: Text(
            lastMoveCard.name,
            textAlign: TextAlign.center,
            style: context.textTheme.bodyMedium,
            overflow: TextOverflow.ellipsis,
            maxLines: 3,
          ),
        ),
      ),
    );
  }
}

class DefineGameSide extends StatefulWidget {
  const DefineGameSide(
      {super.key, required this.controller, required this.openBs});
  final CreateLobbyScreenController controller;
  final VoidCallback openBs;

  @override
  State<DefineGameSide> createState() => _DefineGameSideState();
}

class _DefineGameSideState extends State<DefineGameSide> {
  final TextEditingController editingController = TextEditingController();
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Column(
      children: [
        const SizedBox(
          height: 8,
        ),
        Text(
          'ساید های بازی رو مشخص کن',
          style: context.textTheme.titleMedium,
        ),
        SizedBox(
          height: 120,
          child: Row(
            children: [
              Expanded(
                child: AnimatedList(
                  scrollDirection: Axis.horizontal,
                  initialItemCount: widget.controller.sideNameList.length,
                  key: widget.controller.gameSideKey,
                  itemBuilder: (context, index, animation) => SlideTransition(
                    key: ValueKey(widget.controller.sideNameList[index]),
                    position: Tween<Offset>(
                            begin: const Offset(0.5, 0), end: Offset.zero)
                        .animate(animation),
                    child: _animatedItemList(
                      name: widget.controller.sideNameList[index],
                      context: context,
                      deleteCallback: () {
                        if (widget.controller.sideNameList[index] == 'مافیا' ||
                            widget.controller.sideNameList[index] == 'شهروند') {
                          return;
                        }
                        var currentItem = widget.controller.sideNameList[index];
                        widget.controller.sideNameList.removeAt(index);
                        widget.controller.gameSideKey.currentState!.removeItem(
                          index,
                          (context, animation) => SlideTransition(
                            key: ValueKey(currentItem),
                            position: Tween<Offset>(
                                    begin: const Offset(1, 0), end: Offset.zero)
                                .animate(animation),
                            child: _animatedItemList(
                              name: currentItem,
                              context: context,
                              deleteCallback: () {},
                            ),
                          ),
                          duration: const Duration(milliseconds: 150),
                        );
                      },
                    ),
                  ),
                ),
              ),
              const SizedBox(
                width: 8,
              ),
              FloatingActionButton(
                heroTag: 'fabCreateGameSide',
                onPressed: () {
                  widget.openBs();
                },
                child: Padding(
                  padding: const EdgeInsets.all(4.0),
                  child: Image.asset('images/citizen_hat.png'),
                ),
              ),
            ],
          ),
        )
      ],
    );
  }

  Widget _animatedItemList(
      {required String name,
      required BuildContext context,
      required VoidCallback? deleteCallback}) {
    return Container(
      width: 80,
      height: 80,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: Colors.grey, width: 2),
      ),
      margin: const EdgeInsets.all(8),
      padding: const EdgeInsets.all(8),
      child: InkWell(
        onTap: deleteCallback,
        child: Center(
          child: Text(
            name,
            textAlign: TextAlign.center,
            style: context.textTheme.bodyMedium,
            overflow: TextOverflow.ellipsis,
            maxLines: 3,
          ),
        ),
      ),
    );
  }
}

class CreateLobbyDeck extends StatefulWidget {
  const CreateLobbyDeck({
    super.key,
    required this.controller,
    required this.characters,
    required this.playerCount,
    required this.deckList,
  });
  final List<LobbyCharacterModel> characters;
  final int playerCount;
  final Function(List<LobbyCharacterModel>) deckList;
  final CreateLobbyScreenController controller;
  @override
  State<CreateLobbyDeck> createState() => _CreateLobbyDeckState();
}

class _CreateLobbyDeckState extends State<CreateLobbyDeck> {
  late List<LobbyCharacterModel> modifierList;

  int characterCount = 0;

  @override
  void initState() {
    modifierList =
        widget.characters.where((element) => element.custom == false).toList();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return WillPopScope(
      onWillPop: () async => false,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            const SizedBox(
              height: 26,
            ),
            Row(
              children: [
                Expanded(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        '$characterCount',
                        style: context.textTheme.bodyLarge,
                      ),
                      const SizedBox(
                        width: 4,
                      ),
                      Text(
                        'نقش انتخاب شده',
                        style: context.textTheme.titleMedium,
                      )
                    ],
                  ),
                ),
                Expanded(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        '${widget.playerCount}',
                        style: context.textTheme.bodyLarge,
                      ),
                      const SizedBox(
                        width: 4,
                      ),
                      Text(
                        'تعداد بازیکن',
                        style: context.textTheme.titleMedium,
                      )
                    ],
                  ),
                )
              ],
            ),
            const SizedBox(
              height: 16,
            ),
            Expanded(
                flex: 4,
                child: GridView.builder(
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2, mainAxisSpacing: 16),
                  itemBuilder: (context, index) => Column(
                    children: [
                      Text(widget.characters[index].name),
                      const SizedBox(
                        height: 4,
                      ),
                      InkWell(
                        splashColor: Colors.transparent,
                        onTap: () {
                          setState(() {
                            var item = modifierList.firstWhere((element) =>
                                element.id == widget.characters[index].id);
                            if (!item.selected) {
                              AudioPlayer().play(AssetSource('bubble.mp3'));
                              item.count++;
                              characterCount++;
                            } else {
                              characterCount = characterCount - item.count;
                              item.count = 0;
                            }
                            item.selected = !item.selected;
                          });
                        },
                        child: CachedNetworkImage(
                          imageUrl: '$appBaseUrl/${modifierList[index].icon}',
                          imageBuilder: (context, imageProvider) => Container(
                            width: MediaQuery.of(context).size.width * 20 / 100,
                            height:
                                MediaQuery.of(context).size.width * 20 / 100,
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(
                                  MediaQuery.of(context).size.width * 10 / 100),
                              border: Border.all(
                                  color: modifierList[index].selected
                                      ? Colors.cyan
                                      : Colors.grey,
                                  width: 2),
                            ),
                            child: Padding(
                              padding: const EdgeInsets.all(4.0),
                              child: Image(
                                image: imageProvider,
                              ),
                            ),
                          ),
                          placeholder: (context, url) => Container(
                            width: MediaQuery.of(context).size.width * 20 / 100,
                            height:
                                MediaQuery.of(context).size.width * 20 / 100,
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(
                                  MediaQuery.of(context).size.width * 10 / 100),
                              color: Colors.grey,
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(
                        height: 8,
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          FloatingActionButton(
                            mini: true,
                            onPressed: () {
                              if (!modifierList[index].selected) {
                                BotToast.showText(text: 'اول نقش رو انتخاب کن');
                                return;
                              }
                              setState(() {
                                var count = modifierList[index].count;
                                if (count > 1) {
                                  count--;
                                  modifierList[index].count = count;
                                  characterCount--;
                                }
                              });
                            },
                            heroTag: 'fabDecCount',
                            child: const Icon(Icons.remove),
                          ),
                          const SizedBox(
                            width: 4,
                          ),
                          Text(
                            '${modifierList[index].count}',
                            style: context.textTheme.bodyMedium,
                          ),
                          const SizedBox(
                            width: 4,
                          ),
                          FloatingActionButton(
                            mini: true,
                            onPressed: () {
                              if (!modifierList[index].selected) {
                                BotToast.showText(text: 'اول نقش رو انتخاب کن');
                                return;
                              }
                              setState(() {
                                var count = modifierList[index].count;
                                count++;
                                modifierList[index].count = count;
                                characterCount++;
                              });
                            },
                            heroTag: 'fabIncCount',
                            child: const Icon(Icons.add),
                          ),
                        ],
                      ),
                    ],
                  ),
                  itemCount: widget.characters.length,
                )),
            const SizedBox(
              height: 8,
            ),
            Text(
              'از منوی پایین میتونی نقش جدید درست کنی',
              style: context.textTheme.titleMedium,
            ),
            const SizedBox(
              height: 8,
            ),
            Expanded(
                flex: 1,
                child: Row(
                  children: [
                    Expanded(
                      child: AnimatedList(
                        reverse: true,
                        scrollDirection: Axis.horizontal,
                        key: widget.controller.createdDeckKey,
                        initialItemCount: modifierList
                            .where((element) => element.custom == true)
                            .length,
                        itemBuilder: (context, index, animation) =>
                            SlideTransition(
                          key: ValueKey(modifierList
                              .where((element) => element.custom == true)
                              .toList()[index]
                              .id),
                          position: Tween<Offset>(
                                  begin: const Offset(0.5, 0), end: Offset.zero)
                              .animate(animation),
                          child: _createCharacterAnimatedListItem(
                            character: modifierList
                                .where((element) => element.custom == true)
                                .toList()[index],
                            deleteCallback: () {
                              setState(() {
                                characterCount--;
                              });
                              var currentlyRemoving = modifierList
                                  .where((element) => element.custom == true)
                                  .toList()[index];
                              modifierList.removeWhere(
                                  (element) => element == currentlyRemoving);
                              widget.controller.createdDeckKey.currentState
                                  ?.removeItem(
                                duration: const Duration(milliseconds: 250),
                                index,
                                (context, animation) => SlideTransition(
                                  key: ValueKey(currentlyRemoving.name),
                                  position: Tween<Offset>(
                                          begin: const Offset(1, 0),
                                          end: Offset.zero)
                                      .animate(animation),
                                  child: _createCharacterAnimatedListItem(
                                    character: currentlyRemoving,
                                    deleteCallback: () {},
                                  ),
                                ),
                              );
                            },
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(
                      width: 8,
                    ),
                    FloatingActionButton(
                      onPressed: () {
                        Get.dialog(
                          CreateLobbyCharacterDialog(
                            sides: widget.controller.sideNameList,
                            callback: (name, side) {
                              if (name.isNotEmpty) {
                                Get.back();
                                Future.delayed(
                                  const Duration(milliseconds: 150),
                                  () {
                                    setState(
                                      () {
                                        characterCount++;
                                        AudioPlayer()
                                            .play(AssetSource('bubble.mp3'));

                                        modifierList.add(
                                          LobbyCharacterModel(
                                              name: name,
                                              id: Random().nextInt(9999) + 1000,
                                              icon: '',
                                              custom: true,
                                              count: 1,
                                              customSide: side),
                                        );

                                        widget.controller.createdDeckKey
                                            .currentState
                                            ?.insertItem(
                                          0,
                                          duration:
                                              const Duration(milliseconds: 250),
                                        );
                                      },
                                    );
                                  },
                                );
                              }
                            },
                          ),
                        );
                      },
                      heroTag: 'addNewCharacter',
                      child: const Icon(
                        Icons.create,
                        color: Colors.black,
                      ),
                    )
                  ],
                )),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                ElevatedButton(
                  onPressed: () {
                    Get.back();
                  },
                  child: Text(
                    'خروج',
                    style: context.textTheme.bodyMedium,
                  ),
                ),
                ElevatedButton(
                  onPressed: () {
                    if (characterCount != widget.playerCount) {
                      BotToast.showText(
                          text: 'تعداد بازیکن ها با نقش ها برابر نیست');
                      return;
                    }
                    // callback
                    widget.deckList(modifierList);
                  },
                  child: Text(
                    'اتمام',
                    style: context.textTheme.bodyMedium,
                  ),
                )
              ],
            )
          ],
        ),
      ),
    );
  }

  Widget _createCharacterAnimatedListItem(
      {required LobbyCharacterModel character,
      required VoidCallback? deleteCallback}) {
    return GestureDetector(
      onTap: deleteCallback,
      child: Container(
        padding: const EdgeInsets.all(8),
        margin: const EdgeInsets.all(4),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: Colors.grey),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            Text(
              character.name,
              style: context.textTheme.bodyMedium,
            ),
            const SizedBox(
              height: 4,
            ),
            Text(
              '${character.customSide}',
              style: context.textTheme.bodyMedium,
            )
          ],
        ),
      ),
    );
  }
}

class CreateLastMoveCard extends StatelessWidget {
  const CreateLastMoveCard({super.key, required this.controller});

  final CreateLobbyScreenController controller;

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
            color: const Color.fromRGBO(102, 102, 102, 0.5),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const SizedBox(
                    width: double.infinity,
                  ),
                  Text(
                    'ایجاد کارت حرکت آخر',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Directionality(
                    textDirection: TextDirection.rtl,
                    child: TextField(
                      controller: controller.lastCardController,
                      decoration: InputDecoration(
                        border: context.theme.inputDecorationTheme.border,
                        label: Text(
                          'حرکت آخر',
                          style: context.textTheme.titleMedium,
                        ),
                      ),
                      maxLines: 1,
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  SizedBox(
                    width: 120,
                    child: ElevatedButton(
                      onPressed: () {
                        if (controller.lastCardController.text.isNotEmpty) {
                          controller.createLastMoveCard(
                              controller.lastCardController.text);

                          Future.delayed(const Duration(milliseconds: 50), () {
                            Get.back();
                          });
                        }
                      },
                      child: Text(
                        'تایید',
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
    );
  }
}

class CreateLobbyCharacterDialog extends StatefulWidget {
  const CreateLobbyCharacterDialog({
    super.key,
    required this.sides,
    required this.callback,
  });

  final List<String> sides;
  final Function(String name, String side) callback;
  @override
  State<CreateLobbyCharacterDialog> createState() =>
      _CreateLobbyCharacterDialogState();
}

class _CreateLobbyCharacterDialogState
    extends State<CreateLobbyCharacterDialog> {
  TextEditingController edtCharacterController = TextEditingController();
  late List<dynamic> checkBoxList;

  @override
  void initState() {
    checkBoxList = widget.sides
        .map<dynamic>((side) => {'side': side, 'active': false})
        .toList();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Align(
      alignment: Alignment.topCenter,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(16),
          child: BackdropFilter(
            filter: ImageFilter.blur(
              sigmaX: 5,
              sigmaY: 5,
            ),
            child: Container(
              width: MediaQuery.of(context).size.width * 70 / 100,
              padding: EdgeInsets.only(
                  bottom: MediaQuery.of(context).viewInsets.bottom),
              color: const Color.fromRGBO(102, 102, 102, 0.5),
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const SizedBox(
                      height: 16,
                    ),
                    Text(
                      'ساخت نقش',
                      style: context.textTheme.bodyLarge,
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Card(
                      color: Colors.transparent,
                      child: Directionality(
                        textDirection: TextDirection.rtl,
                        child: TextField(
                          controller: edtCharacterController,
                          decoration: InputDecoration(
                              border: context.theme.inputDecorationTheme.border,
                              label: Text(
                                'اسم نقش',
                                style: context.textTheme.titleMedium,
                              )),
                          maxLines: 1,
                        ),
                      ),
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    Text(
                      'انتخاب ساید نقش',
                      style: context.textTheme.bodyMedium,
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    Card(
                      color: Colors.grey.shade800,
                      child: Column(children: [
                        SizedBox(
                          height: 150,
                          child: ListView.builder(
                            itemBuilder: (context, index) => Row(
                              mainAxisAlignment: MainAxisAlignment.end,
                              children: [
                                Text(
                                  checkBoxList[index]['side'],
                                  style: context.textTheme.bodyMedium,
                                ),
                                const SizedBox(
                                  width: 8,
                                ),
                                Checkbox(
                                    side: const BorderSide(color: Colors.grey),
                                    activeColor: Colors.cyan,
                                    shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(6),
                                    ),
                                    value: checkBoxList[index]['active'],
                                    onChanged: (value) {
                                      setState(() {
                                        checkBoxList[index]['active'] = value;
                                      });
                                    }),
                              ],
                            ),
                            itemCount: widget.sides.length,
                          ),
                        )
                      ]),
                    ),
                    const SizedBox(
                      height: 16,
                    ),
                    ElevatedButton(
                      onPressed: () {
                        // callback name
                        if (edtCharacterController.text.isEmpty) {
                          BotToast.showText(text: 'اسم نقش خالیه');
                          return;
                        } else if (checkBoxList
                                .where((element) => element['active'])
                                .toList()
                                .length >
                            1) {
                          BotToast.showText(
                              text: 'هر نقش فقط میتونه یک ساید داشته باشه');
                          return;
                        } else if (checkBoxList
                            .where((element) => element['active'])
                            .toList()
                            .isEmpty) {
                          BotToast.showText(text: 'برای نقش سایدی انتخاب نشده');
                          return;
                        } else {
                          widget.callback(
                              edtCharacterController.text,
                              checkBoxList.singleWhere(
                                  (element) => element['active'])['side']);
                        }
                      },
                      child: Text(
                        'تایید',
                        style: context.textTheme.bodyMedium,
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
