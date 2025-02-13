import 'dart:ui';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:lottie/lottie.dart';
import 'package:mafia/models/shop/shop_item_model.dart';
import 'package:mafia/screens/shop/shop_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';
import 'package:mafia/utils/extension/extensions.dart';

class ShopScreen extends GetView<ShopScreenController> {
  const ShopScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    controller.getMarketItems();
    return Scaffold(
      appBar: AppBar(
        title: Text(
          'فروشگاه',
          style: context.textTheme.bodyLarge,
        ),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.only(left: 16.0, right: 16, bottom: 16),
        child: SingleChildScrollView(
          child: AnimationLimiter(
            child: Column(
              children: AnimationConfiguration.toStaggeredList(
                duration: const Duration(milliseconds: 500),
                childAnimationBuilder: (widget) => SlideAnimation(
                  horizontalOffset: 100,
                  child: FadeInAnimation(child: widget),
                ),
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      LottieBuilder.asset(
                        'assets/coins.json',
                        width: 50,
                        height: 50,
                      ),
                      const SizedBox(
                        width: 8,
                      ),
                      GetBuilder<ShopScreenController>(
                        id: 'userGold',
                        builder: (_) => Text(
                          controller.userGolds.toString().numberSeparator(),
                          style: context.textTheme.bodyLarge,
                        ),
                      )
                    ],
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Align(
                    alignment: Alignment.centerRight,
                    child: Text(
                      'بسته های طلایی',
                      style: context.textTheme.titleMedium,
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  GetBuilder<ShopScreenController>(
                    id: 'goldItems',
                    builder: (_) => SizedBox(
                      height: MediaQuery.of(context).size.height * 35 / 100,
                      width: double.infinity,
                      child: ListView.builder(
                        itemBuilder: (context, index) =>
                            _goldItem(context, controller.goldList[index]),
                        itemCount: controller.goldList.length,
                        scrollDirection: Axis.horizontal,
                        reverse: true,
                      ),
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  Align(
                    alignment: Alignment.centerRight,
                    child: Text(
                      'بسته های آواتار',
                      style: context.textTheme.titleMedium,
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  GetBuilder<ShopScreenController>(
                    id: 'avatarItems',
                    builder: (_) => SizedBox(
                      width: double.infinity,
                      height: MediaQuery.of(context).size.height * 35 / 100,
                      child: ListView.builder(
                        itemBuilder: (context, index) => _avatarItem(
                          context,
                          controller.avatarList[index],
                        ),
                        scrollDirection: Axis.horizontal,
                        reverse: true,
                        itemCount: controller.avatarList.length,
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

  Widget _avatarItem(BuildContext context, ShopItemModel item) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          CachedNetworkImage(
            imageUrl: '$appBaseUrl/${item.image}',
            imageBuilder: (context, imageProvider) => Container(
              width: MediaQuery.of(context).size.width * 20 / 100,
              height: MediaQuery.of(context).size.width * 20 / 100,
              decoration: ShapeDecoration(
                  shape: ContinuousRectangleBorder(
                    borderRadius: BorderRadius.circular(
                        MediaQuery.of(context).size.width * 14 / 100),
                  ),
                  image: DecorationImage(image: imageProvider)),
            ),
            placeholder: (context, url) => Container(
              width: MediaQuery.of(context).size.width * 20 / 100,
              height: MediaQuery.of(context).size.width * 20 / 100,
              decoration: ShapeDecoration(
                  shape: ContinuousRectangleBorder(
                    borderRadius: BorderRadius.circular(
                        MediaQuery.of(context).size.width * 14 / 100),
                  ),
                  color: Colors.grey),
            ),
          ),
          const SizedBox(
            height: 8,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Image.asset(
                'images/gold.png',
                width: 30,
                height: 30,
              ),
              const SizedBox(
                width: 8,
              ),
              Text(
                '${item.price}',
                style: context.textTheme.bodyMedium,
              ),
            ],
          ),
          const SizedBox(
            height: 8,
          ),
          ElevatedButton(
            onPressed: () {
              if (item.active_for_user == false) {
                return;
              }

              Get.dialog(
                ConfirmPurchase(
                  img: item.image!,
                  controller: controller,
                  itemId: item.marketId,
                  purchaseSuccessCallback: () {
                    Get.back();
                    // reload market items
                    controller.getMarketItems();
                  },
                ),
                barrierDismissible: false,
              );
            },
            style: ButtonStyle(
              backgroundColor: MaterialStatePropertyAll(
                  item.active_for_user == true ? Colors.cyan : Colors.grey),
            ),
            child: Text(item.active_for_user == true ? 'خرید' : 'خریداری شده'),
          )
        ],
      ),
    );
  }

  Widget _goldItem(BuildContext context, ShopItemModel item) {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        children: [
          Image.asset(
            'images/${item.off > 0 ? 'discount' : 'gold_pack'}.png',
            width: MediaQuery.of(context).size.width * 15 / 100,
            height: MediaQuery.of(context).size.width * 15 / 100,
          ),
          Text(
            'بسته ${item.gold} تایی',
            style: context.textTheme.bodyLarge,
          ),
          const SizedBox(
            height: 8,
          ),
          Directionality(
            textDirection: TextDirection.rtl,
            child: Opacity(
              opacity: item.off > 0 ? 1 : 0,
              child: Text(
                item.off > 0
                    ? '${item.price.toString().numberSeparator()} تومان'
                    : '',
                style: TextStyle(
                    fontFamily: 'shabnam',
                    fontSize: 16.sp,
                    color: Colors.grey,
                    decoration: TextDecoration.lineThrough,
                    decorationThickness: 2,
                    decorationColor: Colors.grey.shade400),
              ),
            ),
          ),
          const SizedBox(
            height: 8,
          ),
          FittedBox(
            child: Row(
              children: [
                Text('تومان', style: context.textTheme.titleMedium),
                const SizedBox(
                  width: 4,
                ),
                Text(
                  item.off > 0
                      ? item.price_after_off.toString().numberSeparator()
                      : item.price.toString().numberSeparator(),
                  style: TextStyle(
                      fontFamily: 'shabnam',
                      color:
                          item.off > 0 ? Colors.yellow.shade700 : Colors.white,
                      fontSize: 16.sp),
                ),
              ],
            ),
          ),
          const SizedBox(
            height: 8,
          ),
          ElevatedButton(
            onPressed: () {
              controller.purchaseGold(item.id);
            },
            child: GetBuilder<ShopScreenController>(
              id: item.id,
              builder: (_) => item.loading == false
                  ? Text(
                      'خرید',
                      style: context.textTheme.bodyMedium,
                    )
                  : LoadingAnimationWidget.prograssiveDots(
                      color: Colors.black, size: 26),
            ),
          )
        ],
      ),
    );
  }
}

class ConfirmPurchase extends StatelessWidget {
  const ConfirmPurchase(
      {super.key,
      required this.img,
      required this.controller,
      required this.itemId,
      required this.purchaseSuccessCallback});

  final String img;
  final ShopScreenController controller;
  final String itemId;
  final Function() purchaseSuccessCallback;
  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return PopScope(
      canPop: false,
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Image.asset(
              'images/basket_image.webp',
              width: 200,
              height: 100,
            ),
            SizedBox(
              width: MediaQuery.of(context).size.width * 70 / 100,
              child: ClipRRect(
                borderRadius: BorderRadius.circular(16),
                child: BackdropFilter(
                  filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
                  child: Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(16),
                      color: const Color.fromRGBO(105, 105, 105, 0.3),
                    ),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        CachedNetworkImage(
                          imageUrl: '$appBaseUrl/$img',
                          imageBuilder: (context, imageProvider) => Container(
                            width: MediaQuery.of(context).size.width * 20 / 100,
                            height:
                                MediaQuery.of(context).size.width * 20 / 100,
                            decoration: ShapeDecoration(
                              shape: ContinuousRectangleBorder(
                                  borderRadius: BorderRadius.circular(
                                      MediaQuery.of(context).size.width *
                                          13 /
                                          100)),
                              image: DecorationImage(image: imageProvider),
                            ),
                          ),
                        ),
                        const SizedBox(
                          height: 16,
                        ),
                        Text(
                          'تایید خرید محصول ؟',
                          style: context.textTheme.bodyMedium,
                        ),
                        const SizedBox(
                          height: 16,
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            ElevatedButton(
                              onPressed: () {
                                Get.back();
                              },
                              child: Text(
                                'لغو',
                                style: context.textTheme.bodyMedium,
                              ),
                            ),
                            GetBuilder<ShopScreenController>(
                              id: 'purchaseLoading',
                              builder: (_) => Stack(
                                alignment: Alignment.center,
                                children: [
                                  AnimatedOpacity(
                                    opacity:
                                        controller.purchaseItemLoading == false
                                            ? 1
                                            : 0,
                                    duration: const Duration(milliseconds: 500),
                                    child: ElevatedButton(
                                      onPressed: () {
                                        if (controller.purchaseItemLoading) {
                                          return;
                                        }
                                        controller.purchaseAvatar(itemId, (p0) {
                                          if (p0) {
                                            purchaseSuccessCallback();
                                          }
                                        });
                                      },
                                      child: Text(
                                        'تایید',
                                        style: context.textTheme.bodyMedium,
                                      ),
                                    ),
                                  ),
                                  AnimatedOpacity(
                                    opacity:
                                        controller.purchaseItemLoading ? 1 : 0,
                                    duration: const Duration(milliseconds: 500),
                                    child:
                                        LoadingAnimationWidget.prograssiveDots(
                                            color: Colors.white, size: 26),
                                  ),
                                ],
                              ),
                            )
                          ],
                        )
                      ],
                    ),
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
