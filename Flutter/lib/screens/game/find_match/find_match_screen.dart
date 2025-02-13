import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:lottie/lottie.dart';
import 'package:mafia/models/game/find_match/find_match_user_model.dart';
import 'package:mafia/screens/game/find_match/find_match_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';

class FindMatchScreen extends GetView<FindMatchScreenController> {
  const FindMatchScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // font
    ScreenUtil.init(context);

    return FindMatchScreenHolder(
      controller: controller,
    );
  }
}

class FindMatchScreenHolder extends StatefulWidget {
  const FindMatchScreenHolder({super.key, required this.controller});
  final FindMatchScreenController controller;
  @override
  State<FindMatchScreenHolder> createState() => _FindMatchScreenHolderState();
}

class _FindMatchScreenHolderState extends State<FindMatchScreenHolder>
    with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);

    if (state == AppLifecycleState.resumed) {
      if (widget.controller.appBackground) {
        widget.controller.navigateToPrevious();
      }
    } else if (state == AppLifecycleState.paused) {
      widget.controller.cancelFindMatch();
      widget.controller.fromBackground();
    }
  }

  @override
  Widget build(BuildContext context) {
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
              padding: const EdgeInsets.all(16.0),
              child: Column(
                children: [
                  const SizedBox(
                    width: double.infinity,
                  ),
                  Expanded(
                    flex: 20,
                    child: Lottie.asset(
                      'assets/anim_loading.json',
                      width: 150,
                      height: 0.2.sh,
                      animate: true,
                      repeat: true,
                    ),
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Text(
                    'جستجوی بازیکن ، از صفحه خارج نشوید',
                    style: context.textTheme.titleMedium,
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  GetBuilder<FindMatchScreenController>(
                    id: 'findMatchCount',
                    builder: (_) => Directionality(
                      textDirection: TextDirection.rtl,
                      child: Text(
                        '${widget.controller.findMatchUsers.length} نفر از 10 نفر',
                        style: context.textTheme.bodyLarge,
                      ),
                    ),
                  ),
                  const SizedBox(
                    height: 8,
                  ),
                  Expanded(
                    flex: 50,
                    child: Center(
                      child: GetBuilder<FindMatchScreenController>(
                        id: 'findMatchUsers',
                        builder: (_) => GridView.builder(
                          gridDelegate:
                              const SliverGridDelegateWithFixedCrossAxisCount(
                                  crossAxisCount: 3,
                                  mainAxisSpacing: 16,
                                  crossAxisSpacing: 16),
                          itemBuilder: (context, index) => FindMatchUserPlace(
                            controller: widget.controller,
                            item: widget.controller.findMatchUsers[index],
                            context: context,
                          ),
                          itemCount: widget.controller.findMatchUsers.length,
                        ),
                      ),
                    ),
                  ),
                  const Spacer(),
                  GetBuilder<FindMatchScreenController>(
                    id: 'gameFound',
                    builder: (_) => !widget.controller.gameFound.value
                        ? ElevatedButton(
                            onPressed: () async {
                              await widget.controller.cancelFindMatch();

                              Get.back();
                            },
                            child: Text(
                              'خروج از جستجو',
                              style: context.textTheme.bodySmall,
                            ),
                          )
                        : GetBuilder<FindMatchScreenController>(
                            id: 'counter',
                            builder: (_) => Directionality(
                              textDirection: TextDirection.rtl,
                              child: Text(
                                '${widget.controller.counter.value} تا شروع بازی',
                                style: context.textTheme.bodyLarge,
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

class FindMatchUserPlace extends StatelessWidget {
  const FindMatchUserPlace({
    super.key,
    required this.controller,
    required this.item,
    required this.context,
  });
  final FindMatchScreenController controller;
  final FindMatchUserModel item;
  final BuildContext context;
  @override
  Widget build(BuildContext context) {
    return CachedNetworkImage(
      imageUrl: '$appBaseUrl/${item.user_image}',
      imageBuilder: (context, imageProvider) => Center(
        child: Container(
          width: MediaQuery.of(context).size.width * 20 / 100,
          height: MediaQuery.of(context).size.width * 20 / 100,
          padding: const EdgeInsets.all(16),
          decoration: ShapeDecoration(
            shape: ContinuousRectangleBorder(
              borderRadius: BorderRadius.circular(
                  MediaQuery.of(context).size.width * 13 / 100),
              side: const BorderSide(width: 2, color: Colors.grey),
            ),
            image: DecorationImage(image: imageProvider),
          ),
        ),
      ),
      placeholder: (context, url) => Center(
        child: Container(
          width: MediaQuery.of(context).size.width * 20 / 100,
          height: MediaQuery.of(context).size.width * 20 / 100,
          padding: const EdgeInsets.all(16),
          decoration: ShapeDecoration(
              shape: ContinuousRectangleBorder(
                borderRadius: BorderRadius.circular(
                    MediaQuery.of(context).size.width * 13 / 100),
                side: const BorderSide(width: 2, color: Colors.grey),
              ),
              color: Colors.grey),
        ),
      ),
    );
  }
}
