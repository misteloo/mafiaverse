import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mafia/models/local_game/local_game_characters_model.dart';
import 'package:mafia/screens/local_game/select_deck/select_deck_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';

class SelectDeckScreen extends GetView<SelectDeckScreenController> {
  const SelectDeckScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: Text(
          'انتخاب دسته کارت',
          style: context.textTheme.bodyMedium,
        ),
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Directionality(
              textDirection: TextDirection.rtl,
              child: Text(
                'از بین کارتهای زیر ، نقشی که میخوای داخل بازی وجود داشته باشه با تعدادش رو مشخص کن',
                style: context.textTheme.titleMedium,
                textAlign: TextAlign.justify,
              ),
            ),
            const SizedBox(
              height: 8,
            ),
            Row(
              children: [
                Expanded(
                  child: Center(
                    child: Text('تعداد بازیکنان  ${controller.playerCount}'),
                  ),
                ),
                Expanded(
                    child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(
                      Icons.people_rounded,
                      color: Colors.grey,
                    ),
                    const SizedBox(
                      width: 8,
                    ),
                    GetBuilder<SelectDeckScreenController>(
                      id: 'total',
                      builder: (_) => Text(
                        '${controller.deckList.length}',
                        style: context.textTheme.bodyMedium,
                      ),
                    )
                  ],
                )),
              ],
            ),
            Expanded(
                child: ListView.builder(
              itemBuilder: (context, index) =>
                  GetBuilder<SelectDeckScreenController>(
                      id: controller.getCharacters[index].id,
                      builder: (_) =>
                          _listItem(controller.getCharacters[index], context)),
              itemCount: controller.getCharacters.length,
            )),
            ElevatedButton(
                onPressed: () {
                  controller.startGame();
                },
                child: Text(
                  'شروع بازی',
                  style: context.textTheme.bodyMedium,
                ))
          ],
        ),
      ),
    );
  }

  Widget _listItem(LocalGameCharactersModel item, BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8),
      child: GestureDetector(
        onTap: () {
          controller.toggleItemAddRemove(item.id);
        },
        child: Row(
          children: [
            IconButton(
              onPressed: () {
                Get.bottomSheet(
                  AboutCharacter(model: item),
                );
              },
              icon: const Icon(
                Icons.more_horiz_rounded,
                color: Colors.grey,
              ),
            ),
            Visibility(
              visible: item.multi && controller.isInDeckList(item.id),
              child: Row(
                children: [
                  FloatingActionButton(
                    heroTag: 'removeItem${item.id}',
                    onPressed: () {
                      controller.removeItem(item.id);
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
                  Text(
                    '${controller.itemCount(item.id)}',
                    style: context.textTheme.bodyMedium,
                  ),
                  const SizedBox(
                    width: 8,
                  ),
                  FloatingActionButton(
                    heroTag: 'addItem${item.id}',
                    onPressed: () {
                      controller.addItem(item.id);
                    },
                    mini: true,
                    child: const Icon(
                      Icons.add,
                      color: Colors.black,
                    ),
                  ),
                ],
              ),
            ),
            const Spacer(),
            Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(
                  item.name,
                  style: context.textTheme.bodyMedium,
                ),
                const SizedBox(
                  height: 8,
                ),
                Text(
                  item.side == 'mafia'
                      ? 'ساید : مافیا'
                      : item.side == 'citizen'
                          ? 'ساید : شهروند'
                          : 'ساید : مستفل',
                  style: context.textTheme.titleMedium,
                )
              ],
            ),
            const SizedBox(
              width: 8,
            ),
            CachedNetworkImage(
              imageUrl: '$appBaseUrl/${item.icon}',
              imageBuilder: (context, imageProvider) => Container(
                width: MediaQuery.of(context).size.width * 15 / 100,
                height: MediaQuery.of(context).size.width * 15 / 100,
                decoration: ShapeDecoration(
                  shape: ContinuousRectangleBorder(
                    borderRadius: BorderRadius.circular(
                        MediaQuery.of(context).size.width * 10 / 100),
                    side: BorderSide(
                        color: controller.isInDeckList(item.id)
                            ? Colors.cyan
                            : Colors.grey,
                        width: 2),
                  ),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(
                    8,
                  ),
                  child: Image(image: imageProvider),
                ),
              ),
              placeholder: (context, url) => Container(
                width: MediaQuery.of(context).size.width * 15 / 100,
                height: MediaQuery.of(context).size.width * 15 / 100,
                decoration: ShapeDecoration(
                    shape: ContinuousRectangleBorder(
                      borderRadius: BorderRadius.circular(
                          MediaQuery.of(context).size.width * 10 / 100),
                      side: BorderSide(
                          color: controller.isInDeckList(item.id)
                              ? Colors.cyan
                              : Colors.grey,
                          width: 2),
                    ),
                    color: Colors.grey),
              ),
            )
          ],
        ),
      ),
    );
  }
}

class AboutCharacter extends StatelessWidget {
  const AboutCharacter({super.key, required this.model});
  final LocalGameCharactersModel model;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return ClipRRect(
      borderRadius: const BorderRadius.only(
        topLeft: Radius.circular(16),
        topRight: Radius.circular(16),
      ),
      child: BackdropFilter(
        filter: ImageFilter.blur(
          sigmaX: 5,
          sigmaY: 5,
        ),
        child: Container(
          color: const Color.fromRGBO(102, 102, 102, 0.5),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: SingleChildScrollView(
              child: Column(
                children: [
                  const SizedBox(
                    width: double.infinity,
                  ),
                  Text(
                    'درباره نقش ${model.name}',
                    style: context.textTheme.bodyLarge,
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Directionality(
                    textDirection: TextDirection.rtl,
                    child: Text(
                      model.description,
                      style: context.textTheme.bodyMedium,
                      textAlign: TextAlign.justify,
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
