import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/screens/profile/edit_profile/edit_profile_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';

class EditProfileScreen extends GetView<EditProfileScreenController> {
  const EditProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    controller.getAvatars();

    return Scaffold(
      appBar: AppBar(
        title: Text(
          'ویرایش پروفایل',
          style: context.textTheme.bodyMedium,
        ),
        centerTitle: true,
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: GestureDetector(
        onVerticalDragUpdate: (details) {
          if (details.primaryDelta! > 20) {
            return;
          }
        },
        onHorizontalDragUpdate: (details) {
          if (details.primaryDelta! > 10) {
            return;
          }
        },
        child: GetBuilder<EditProfileScreenController>(
          id: 'avatars',
          builder: (_) => controller.avatarList.isNotEmpty
              ? SingleChildScrollView(
                  child: AnimationLimiter(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: AnimationConfiguration.toStaggeredList(
                        childAnimationBuilder: (widget) => SlideAnimation(
                          horizontalOffset: 100,
                          child: FadeInAnimation(child: widget),
                        ),
                        duration: const Duration(milliseconds: 500),
                        children: [
                          _editUserName(context),
                          _editAvatar(context),
                          const SizedBox(
                            height: 20,
                          ),
                          ElevatedButton(
                            onPressed: () {
                              controller.updateProfile();
                            },
                            child: Text(
                              'ذخیره تغییرات',
                              style: context.textTheme.bodyMedium,
                            ),
                          )
                        ],
                      ),
                    ),
                  ),
                )
              : Center(
                  child: LoadingAnimationWidget.prograssiveDots(
                      color: Colors.white, size: 26),
                ),
        ),
      ),
    );
  }

  Widget _editAvatar(BuildContext context) {
    return Column(
      children: [
        const SizedBox(
          width: double.infinity,
        ),
        Text(
          'تغییر آواتار',
          style: context.textTheme.titleLarge,
        ),
        const SizedBox(
          height: 16,
        ),
        GetBuilder<EditProfileScreenController>(
          id: 'editAvatar',
          builder: (_) => CachedNetworkImage(
            imageUrl:
                '$appBaseUrl/${controller.avatarList.singleWhere((element) => element.active).image}',
            imageBuilder: (context, imageProvider) => Container(
              width: MediaQuery.of(context).size.width * 15 / 100,
              height: MediaQuery.of(context).size.width * 15 / 100,
              decoration: ShapeDecoration(
                shape: ContinuousRectangleBorder(
                  borderRadius: BorderRadius.circular(
                    MediaQuery.of(context).size.width * 10 / 100,
                  ),
                ),
                image: DecorationImage(image: imageProvider),
              ),
            ),
          ),
        ),
        const SizedBox(
          height: 16,
        ),
        Text(
          'از بین آواتار ها یکی رو انتخاب کن',
          style: context.textTheme.titleMedium,
        ),
        const SizedBox(
          height: 16,
        ),
        GetBuilder<EditProfileScreenController>(
          id: 'avatars',
          builder: (_) => SizedBox(
            height: MediaQuery.of(context).size.height * 12 / 100,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              itemBuilder: (context, index) => CachedNetworkImage(
                imageUrl: '$appBaseUrl/${controller.avatarList[index].image}',
                imageBuilder: (context, imageProvider) => Center(
                  child: InkWell(
                    onTap: () {
                      controller.changeAvatar(controller.avatarList[index].id);
                    },
                    borderRadius: BorderRadius.circular(
                        MediaQuery.of(context).size.width * 5 / 100),
                    child: Container(
                      margin: const EdgeInsets.symmetric(horizontal: 8),
                      width: MediaQuery.of(context).size.width * 20 / 100,
                      height: MediaQuery.of(context).size.width * 20 / 100,
                      decoration: ShapeDecoration(
                        shape: ContinuousRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                MediaQuery.of(context).size.width * 13 / 100),
                            side: BorderSide(
                                color: controller.avatarList[index].active
                                    ? Colors.cyan
                                    : Colors.grey,
                                width: 2)),
                        image: DecorationImage(image: imageProvider),
                      ),
                    ),
                  ),
                ),
              ),
              itemCount: controller.avatarList.length,
            ),
          ),
        )
      ],
    );
  }

  Widget _editUserName(BuildContext context) {
    return Column(
      children: [
        const SizedBox(
          height: 16,
        ),
        Text(
          'تغییر نام کاربری',
          style: context.textTheme.titleMedium,
        ),
        const SizedBox(
          height: 16,
        ),
        SizedBox(
          width: MediaQuery.of(context).size.width * 70 / 100,
          child: GetBuilder<EditProfileScreenController>(
            id: 'username',
            builder: (_) => TextField(
              controller: controller.textEditingController,
              decoration: InputDecoration(
                border: context.theme.inputDecorationTheme.border,
                errorBorder: context.theme.inputDecorationTheme.errorBorder,
                label: const Icon(
                  Icons.abc_rounded,
                  color: Colors.grey,
                ),
                helperStyle: context.textTheme.titleSmall,
                suffix: SizedBox(
                  height: 22,
                  child: GetBuilder<EditProfileScreenController>(
                    id: 'loadingUsername',
                    builder: (_) => controller.loadingCheckUsername == false
                        ? Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Text(
                                controller.suffixText ?? '',
                                style: TextStyle(
                                    color: controller.suffixError
                                        ? Colors.red
                                        : Colors.green,
                                    fontFamily: 'shabnam',
                                    fontSize: 12),
                              ),
                              const SizedBox(
                                width: 4,
                              ),
                              controller.suffixText != null
                                  ? controller.suffixError
                                      ? const Icon(
                                          Icons.error,
                                          color: Colors.red,
                                          size: 22,
                                        )
                                      : const Icon(
                                          Icons.check,
                                          color: Colors.green,
                                          size: 22,
                                        )
                                  : const Center()
                            ],
                          )
                        : Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              const Text(
                                '',
                                style: TextStyle(
                                    color: Colors.grey,
                                    fontFamily: 'shabnam',
                                    fontSize: 12),
                              ),
                              const SizedBox(
                                width: 4,
                              ),
                              LoadingAnimationWidget.prograssiveDots(
                                  color: Colors.grey, size: 18)
                            ],
                          ),
                  ),
                ),
              ),
              maxLines: 1,
              keyboardType: TextInputType.text,
              style: context.textTheme.bodyMedium,
              onChanged: (value) => controller.onCheckUsername(value),
            ),
          ),
        ),
        const SizedBox(height: 16),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Image.asset(
              'images/golds.webp',
              width: 30,
              height: 30,
            ),
            const SizedBox(
              width: 8,
            ),
            Directionality(
              textDirection: TextDirection.rtl,
              child: Text(
                '500 سکه',
                style: context.textTheme.bodyLarge,
              ),
            ),
            const SizedBox(
              width: 8,
            ),
            Text(
              'هزینه تغییر نام کاربری ',
              style: context.textTheme.titleMedium,
            )
          ],
        ),
        const SizedBox(
          height: 26,
        )
      ],
    );
  }
}
