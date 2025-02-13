import 'dart:async';

import 'package:bot_toast/bot_toast.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:circular_countdown_timer/circular_countdown_timer.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/extensions/responsive_weight.dart';
import 'package:mafia/models/game/select_character/select_character_character_model.dart';
import 'package:mafia/models/game/select_character/select_character_user_model.dart';
import 'package:mafia/screens/game/select_character/select_character_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';
import 'package:mafia/utils/constants/values.dart';

import '../../../utils/widget/flip_card.dart';

class SelectCharacterScreen extends GetView<SelectCharacterScreenController> {
  const SelectCharacterScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // font
    ScreenUtil.init(context);
    return SafeArea(
      child: PopScope(
        canPop: false,
        child: GestureDetector(
          onHorizontalDragUpdate: (update) {
            if (update.primaryDelta! > 10) {
              return;
            }
          },
          child: Scaffold(
            body: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  const SizedBox(
                    width: double.infinity,
                  ),
                  Expanded(
                    flex: 5.percentHight(context),
                    child: Text(
                      'انتخاب نقش',
                      style: context.textTheme.bodyLarge,
                    ),
                  ),
                  Expanded(
                    flex: 5.percentHight(context),
                    child: Align(
                      alignment: Alignment.topRight,
                      child: Text(
                        'ترتیب نوبت بازیکنان',
                        style: context.textTheme.titleMedium,
                      ),
                    ),
                  ),
                  Expanded(
                    flex: 20.percentHight(context),
                    child: AnimatedList(
                      scrollDirection: Axis.horizontal,
                      key: controller.animatedListKey,
                      itemBuilder: (context, index, animation) =>
                          GetBuilder<SelectCharacterScreenController>(
                        id: 'userList',
                        builder: (_) => UsersTurn(
                          user: controller.userList[index],
                          isTurn: index == 0,
                          animation: animation,
                        ),
                      ),
                    ),
                  ),
                  Expanded(
                    flex: 20.percentHight(context),
                    child: GetBuilder<SelectCharacterScreenController>(
                      id: 'characterList',
                      builder: (_) => controller.characterList.isNotEmpty
                          ? Center(
                              child:
                                  GetBuilder<SelectCharacterScreenController>(
                                id: 'counter',
                                builder: (_) => CircularCountDownTimer(
                                  controller: controller.countDownController,
                                  width: 12.percentHight(context).toDouble(),
                                  height: 12.percentHight(context).toDouble(),
                                  duration: selectCharacterTimer,
                                  fillColor: Colors.cyan,
                                  ringColor: Colors.transparent,
                                  autoStart: false,
                                  isReverseAnimation: true,
                                  isReverse: true,
                                  textStyle: context.textTheme.bodyMedium,
                                  strokeCap: StrokeCap.round,
                                ),
                              ),
                            )
                          : Center(
                              child: LoadingAnimationWidget.prograssiveDots(
                                  color: Colors.white, size: 30),
                            ),
                    ),
                  ),
                  Expanded(
                    flex: 40.percentHight(context),
                    child: Center(
                      child: GetBuilder<SelectCharacterScreenController>(
                        id: 'characterList',
                        builder: (_) => CharacterHolder(
                          controller: controller,
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
}

class UsersTurn extends StatelessWidget {
  const UsersTurn(
      {super.key,
      required this.user,
      required this.isTurn,
      required this.animation});
  final SelectCharacterUserModel user;
  final bool isTurn;
  final Animation<double> animation;

  @override
  Widget build(BuildContext context) {
    return FadeTransition(
      opacity: animation,
      child: Center(
        child: SizedBox(
          width: 15.percentHight(context).toDouble(),
          height: 18.percentHight(context).toDouble(),
          child: Column(
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const SizedBox(
                width: double.maxFinite,
              ),
              CachedNetworkImage(
                imageUrl: '$appBaseUrl/${user.user_image}',
                imageBuilder: (context, imageProvider) => Container(
                  width: 10.percentHight(context).toDouble(),
                  height: 10.percentHight(context).toDouble(),
                  decoration: ShapeDecoration(
                    shape: ContinuousRectangleBorder(
                      borderRadius: BorderRadius.circular(30),
                      side: BorderSide(
                          width: 2, color: isTurn ? Colors.cyan : Colors.red),
                    ),
                    image: DecorationImage(image: imageProvider),
                  ),
                ),
                placeholder: (context, url) => Container(
                  width: 2.percentHight(context).toDouble(),
                  height: 2.percentHight(context).toDouble(),
                  decoration: ShapeDecoration(
                    shape: ContinuousRectangleBorder(
                      borderRadius: BorderRadius.circular(40),
                    ),
                  ),
                ),
              ),
              Container(
                padding: const EdgeInsets.all(6),
                decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(10),
                    color: Colors.grey),
                child: Text(
                  user.user_name!,
                  style: context.textTheme.bodySmall,
                  textAlign: TextAlign.center,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}

class CharacterHolder extends StatelessWidget {
  const CharacterHolder({super.key, required this.controller});
  final SelectCharacterScreenController controller;

  @override
  Widget build(BuildContext context) {
    return Wrap(
      runSpacing: 2.percentHight(context).toDouble(),
      spacing: 2.percentHight(context).toDouble(),
      alignment: WrapAlignment.center,
      children: List.generate(
        controller.characterList.length,
        (index) => _item(controller.characterList[index], context, index),
      ),
    );
  }

  Widget _item(SelectCharacterCharacterModel character, BuildContext context,
      int index) {
    return GestureDetector(
      onTap: () {
        if (!controller.myTurnToPick) {
          BotToast.showText(text: 'نوبت شما نیست');
          return;
        }
        if (character.selected! == true) {
          BotToast.showText(text: 'این کارت انتخاب شده');
          return;
        }
        if (controller.timeLeft > 1) {
          controller.chooseCard(index, character.name!);
        }
      },
      child: SizedBox(
        width: 20.percentWidth(context).toDouble(),
        height: 20.percentWidth(context).toDouble(),
        child: FlipCard(
          toggler: character.selected!,
          backCard: const UnSelectedCardWidget(),
          frontCard: character.selected == true
              ? SelectedCardWidget(userImage: character.selected_by!)
              : const Center(),
        ),
      ),
    );
  }
}

class SelectedCardWidget extends StatelessWidget {
  const SelectedCardWidget({super.key, required this.userImage});
  final String userImage;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: ShapeDecoration(
        shape: ContinuousRectangleBorder(
          borderRadius: BorderRadius.circular(40),
        ),
        image: DecorationImage(
          image: NetworkImage('$appBaseUrl/$userImage'),
        ),
      ),
    );
  }
}

class UnSelectedCardWidget extends StatelessWidget {
  const UnSelectedCardWidget({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: ShapeDecoration(
        shape: ContinuousRectangleBorder(
          borderRadius: BorderRadius.circular(40),
          side: const BorderSide(color: Colors.grey, width: 2),
        ),
      ),
      child: const Center(
        child: Icon(
          Icons.question_mark_rounded,
          color: Colors.grey,
        ),
      ),
    );
  }
}

class SelectedCardReveal extends StatefulWidget {
  const SelectedCardReveal(
      {super.key,
      required this.image,
      required this.name,
      required this.dismissCallback});
  final String name;
  final String image;
  final VoidCallback dismissCallback;

  @override
  State<SelectedCardReveal> createState() => _SelectedCardRevealState();
}

class _SelectedCardRevealState extends State<SelectedCardReveal> {
  int timeLeft = 5;

  @override
  void initState() {
    Timer.periodic(const Duration(seconds: 1), (timer) {
      if (timeLeft == 0) {
        timer.cancel();
        widget.dismissCallback();
      } else {
        setState(() {
          timeLeft--;
        });
      }
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      height: Get.height / 2,
      decoration: BoxDecoration(
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(16),
          topRight: Radius.circular(16),
        ),
        color: context.theme.scaffoldBackgroundColor,
      ),
      child: Column(
        children: [
          const SizedBox(
            width: double.infinity,
          ),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  Text(
                    'نمایش نقش',
                    style: context.textTheme.bodyMedium,
                  ),
                  Column(
                    children: [
                      Container(
                        margin: const EdgeInsets.all(16),
                        width: 25.percentWidth(context).toDouble(),
                        height: 25.percentWidth(context).toDouble(),
                        decoration: ShapeDecoration(
                          shape: ContinuousRectangleBorder(
                            borderRadius: BorderRadius.circular(40),
                          ),
                          image: DecorationImage(
                            image: AssetImage(widget.image),
                          ),
                        ),
                      ),
                      Text(
                        widget.name,
                        style: context.textTheme.bodyLarge,
                      ),
                    ],
                  ),
                  SizedBox(
                    width: 50.percentWidth(context).toDouble() + 50,
                    child: Card(
                      color: context.theme.scaffoldBackgroundColor,
                      child: Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Center(
                          child: Directionality(
                            textDirection: TextDirection.rtl,
                            child: Text(
                              '$timeLeft تا شروع بازی',
                              style: context.textTheme.bodyMedium,
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          )
        ],
      ),
    );
  }
}
