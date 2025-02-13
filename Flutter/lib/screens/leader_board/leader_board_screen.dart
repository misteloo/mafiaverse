import 'package:animate_do/animate_do.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/models/leader_board/leader_board_model.dart';
import 'package:mafia/screens/leader_board/leader_board_screen_controller.dart';
import 'package:mafia/utils/constants/address.dart';
import 'package:slide_countdown/slide_countdown.dart';

class LeaderBoardScreen extends GetView<LeaderBoardScreenController> {
  const LeaderBoardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    controller.getLeaderBoard();
    ScreenUtil.init(context);
    return DefaultTabController(
      initialIndex: 2,
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: Text(
            'لیدربرد',
            style: context.textTheme.bodyLarge,
          ),
          centerTitle: true,
          bottom: TabBar(
            dividerColor: Colors.transparent,
            indicatorColor: Colors.cyan,
            tabs: [
              Tab(
                child: Text(
                  'ماهانه',
                  style: context.textTheme.bodyMedium,
                ),
              ),
              Tab(
                child: Text(
                  'هفتگی',
                  style: context.textTheme.bodyMedium,
                ),
              ),
              Tab(
                child: Text(
                  'روزانه',
                  style: context.textTheme.bodyMedium,
                ),
              ),
            ],
          ),
        ),
        body: GetBuilder<LeaderBoardScreenController>(
          id: 'available',
          builder: (_) => controller.availableLeaderBoard == false
              ? Center(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Image.asset(
                        'images/leaderboard.png',
                        width: 60,
                        height: 60,
                      ),
                      const SizedBox(
                        width: 8,
                      ),
                      Text(
                        'به زودی',
                        style: context.textTheme.bodyLarge,
                      )
                    ],
                  ),
                )
              : GetBuilder<LeaderBoardScreenController>(
                  id: 'ranks',
                  builder: (_) => controller.leaderBoardList.isNotEmpty
                      ? TabBarView(
                          dragStartBehavior: DragStartBehavior.down,
                          children: [
                            FutureBuilder(
                                future: _ranking(
                                    controller.leaderBoardList[2], context),
                                builder: (context, snapshot) {
                                  if (!snapshot.hasData) {
                                    return Center(
                                      child: LoadingAnimationWidget
                                          .prograssiveDots(
                                              color: Colors.white, size: 26),
                                    );
                                  } else {
                                    return snapshot.data!;
                                  }
                                }),
                            FutureBuilder(
                                future: _ranking(
                                    controller.leaderBoardList[1], context),
                                builder: (context, snapshot) {
                                  if (!snapshot.hasData) {
                                    return Center(
                                      child: LoadingAnimationWidget
                                          .prograssiveDots(
                                              color: Colors.white, size: 26),
                                    );
                                  } else {
                                    return snapshot.data!;
                                  }
                                }),
                            FutureBuilder(
                                future: _ranking(
                                    controller.leaderBoardList[0], context),
                                builder: (context, snapshot) {
                                  if (!snapshot.hasData) {
                                    return Center(
                                      child: LoadingAnimationWidget
                                          .prograssiveDots(
                                              color: Colors.white, size: 26),
                                    );
                                  } else {
                                    return snapshot.data!;
                                  }
                                }),
                          ],
                        )
                      : const Center(),
                ),
        ),
      ),
    );
  }

  Future<Widget> _ranking(LeaderBoardModel model, BuildContext context) async {
    var left = await controller.exportTimeLeft(
        (model.session_end - DateTime.now().millisecondsSinceEpoch) / 1000.0);
    var day = left[0];
    var hour = left[1];
    var min = left[2];
    var sec = left[3];
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        const SizedBox(
          height: 16,
        ),
        const Text(
          'زمان باقی مانده تا اتمام',
          style: TextStyle(
              fontFamily: 'shabnam', fontSize: 14, color: Colors.grey),
        ),
        const SizedBox(
          height: 8,
        ),
        SlideCountdown(
          duration:
              Duration(days: day, hours: hour, minutes: min, seconds: sec),
          separatorType: SeparatorType.title,
          separatorStyle: const TextStyle(fontFamily: 'shabnam', fontSize: 14),
          durationTitle: const DurationTitle(
              days: 'روز', hours: 'ساعت', minutes: 'دقیقه', seconds: 'ثانیه'),
          decoration: ShapeDecoration(
              shape: ContinuousRectangleBorder(
                borderRadius: BorderRadius.circular(40),
              ),
              color: Colors.cyan.shade500),
        ),
        const SizedBox(
          height: 8,
        ),
        Expanded(
          child: AnimationLimiter(
            child: ListView.builder(
              itemBuilder: (context, index) =>
                  AnimationConfiguration.staggeredList(
                position: index,
                duration: const Duration(milliseconds: 500),
                child: SlideAnimation(
                  horizontalOffset: 100,
                  child: FadeInAnimation(
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Row(
                        children: [
                          Expanded(
                            flex: 45,
                            child: Row(
                              children: [
                                const SizedBox(
                                  width: 8,
                                ),
                                Text(
                                  model.ranking_list[index].rate.toString(),
                                  style: context.textTheme.titleMedium,
                                ),
                                const SizedBox(
                                  width: 8,
                                ),
                                CachedNetworkImage(
                                  imageUrl:
                                      '$appBaseUrl/${model.ranking_list[index].avatar}',
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
                                            MediaQuery.of(context).size.width *
                                                10 /
                                                100),
                                      ),
                                      color: Colors.grey,
                                    ),
                                  ),
                                ),
                                const SizedBox(
                                  width: 8,
                                ),
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        model.ranking_list[index].name,
                                        style: context.textTheme.bodyMedium,
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                      Visibility(
                                        visible: model
                                                    .ranking_list[index].rate ==
                                                1 ||
                                            model.ranking_list[index].rate ==
                                                2 ||
                                            model.ranking_list[index].rate == 3,
                                        child: Image.asset(
                                          model.ranking_list[index].rate == 1
                                              ? 'images/rate_first_place.webp'
                                              : model.ranking_list[index]
                                                          .rate ==
                                                      2
                                                  ? 'images/rate_second_place.webp'
                                                  : model.ranking_list[index]
                                                              .rate ==
                                                          3
                                                      ? 'images/rate_place_3.png'
                                                      : '',
                                          width: 26,
                                          height: 26,
                                        ),
                                      )
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Expanded(
                            flex: 55,
                            child: Row(
                              children: [
                                Expanded(
                                  child: Center(
                                    child: Column(
                                      mainAxisSize: MainAxisSize.max,
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Text(
                                          'برد',
                                          style: context.textTheme.titleMedium,
                                        ),
                                        const SizedBox(
                                          height: 8,
                                        ),
                                        Text(
                                          '${model.ranking_list[index].win}',
                                          style: context.textTheme.bodyMedium,
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                                Expanded(
                                  child: Center(
                                    child: Column(
                                      mainAxisSize: MainAxisSize.max,
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Text(
                                          'باخت',
                                          style: context.textTheme.titleMedium,
                                        ),
                                        const SizedBox(
                                          height: 8,
                                        ),
                                        Text(
                                          '${model.ranking_list[index].lose}',
                                          style: context.textTheme.bodyMedium,
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                                Expanded(
                                  child: Center(
                                    child: Column(
                                      mainAxisSize: MainAxisSize.max,
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Image.asset(
                                          'images/gold.png',
                                          width: 24,
                                          height: 24,
                                        ),
                                        const SizedBox(
                                          height: 8,
                                        ),
                                        Text(
                                          '${model.ranking_list[index].prize}',
                                          style: context.textTheme.bodyMedium,
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          )
                        ],
                      ),
                    ),
                  ),
                ),
              ),
              itemCount: model.ranking_list.length,
            ),
          ),
        ),
        // self
        SlideInRight(
          animate: true,
          duration: const Duration(milliseconds: 900),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Container(
              padding: const EdgeInsets.all(4),
              decoration: ShapeDecoration(
                  shape: ContinuousRectangleBorder(
                    borderRadius: BorderRadius.circular(40),
                  ),
                  color: Colors.grey.shade800),
              child: Row(
                children: [
                  Expanded(
                    flex: 45,
                    child: Row(
                      children: [
                        const SizedBox(
                          width: 8,
                        ),
                        Text(
                          model.user_self.rate.toString(),
                          style: TextStyle(
                              fontFamily: 'shabnam',
                              fontSize: 12.sp,
                              color: Colors.grey),
                        ),
                        const SizedBox(
                          width: 8,
                        ),
                        CachedNetworkImage(
                          imageUrl: '$appBaseUrl/${model.user_self.avatar}',
                          imageBuilder: (context, imageProvider) => Container(
                            width: MediaQuery.of(context).size.width * 15 / 100,
                            height:
                                MediaQuery.of(context).size.width * 15 / 100,
                            decoration: ShapeDecoration(
                              shape: ContinuousRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                    MediaQuery.of(context).size.width *
                                        10 /
                                        100),
                              ),
                              image: DecorationImage(
                                  image: imageProvider, fit: BoxFit.cover),
                            ),
                          ),
                        ),
                        const SizedBox(
                          width: 8,
                        ),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                model.user_self.name,
                                style: const TextStyle(
                                    fontFamily: 'shabnam',
                                    fontSize: 14,
                                    color: Colors.white),
                                overflow: TextOverflow.ellipsis,
                              ),
                              Visibility(
                                visible: model.user_self.rate == 1 ||
                                    model.user_self.rate == 2 ||
                                    model.user_self.rate == 3,
                                child: Image.asset(
                                  model.user_self.rate == 1
                                      ? 'images/rate_first_place.webp'
                                      : model.user_self.rate == 2
                                          ? 'images/rate_second_place.webp'
                                          : model.user_self.rate == 3
                                              ? 'images/rate_place_3.png'
                                              : '',
                                  width: 26,
                                  height: 26,
                                ),
                              )
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    flex: 55,
                    child: Row(
                      children: [
                        Expanded(
                          child: Center(
                            child: Column(
                              mainAxisSize: MainAxisSize.max,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                const Text(
                                  'برد',
                                  style: TextStyle(
                                      fontFamily: 'shabnam',
                                      fontSize: 14,
                                      color: Colors.grey),
                                ),
                                const SizedBox(
                                  height: 8,
                                ),
                                Text(
                                  '${model.user_self.win}',
                                  style: const TextStyle(
                                      fontFamily: 'shabnam',
                                      fontSize: 14,
                                      color: Colors.grey),
                                ),
                              ],
                            ),
                          ),
                        ),
                        Expanded(
                          child: Center(
                            child: Column(
                              mainAxisSize: MainAxisSize.max,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  'باخت',
                                  style: context.textTheme.titleMedium,
                                ),
                                const SizedBox(
                                  height: 8,
                                ),
                                Text(
                                  '${model.user_self.lose}',
                                  style: const TextStyle(
                                      fontFamily: 'shabnam',
                                      fontSize: 14,
                                      color: Colors.grey),
                                ),
                              ],
                            ),
                          ),
                        ),
                        Expanded(
                          child: Center(
                            child: Column(
                              mainAxisSize: MainAxisSize.max,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Image.asset(
                                  'images/gold.png',
                                  width: 24,
                                  height: 24,
                                ),
                                const SizedBox(
                                  height: 8,
                                ),
                                Text(
                                  '${model.user_self.prize}',
                                  style: const TextStyle(
                                      fontFamily: 'shabnam',
                                      fontSize: 14,
                                      color: Colors.grey),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ],
                    ),
                  )
                ],
              ),
            ),
          ),
        )
      ],
    );
  }
}
