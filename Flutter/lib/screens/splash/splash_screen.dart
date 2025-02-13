import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mafia/screens/splash/splash_controller.dart';

class SplashScreen extends GetView<SplashScreenController> {
  const SplashScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    controller.getDetail();
    return SafeArea(
      child: Scaffold(body: SplashBody(completed: () async {})),
    );
  }
}

class SplashBody extends StatefulWidget {
  const SplashBody({super.key, required this.completed});
  final VoidCallback completed;
  @override
  State<SplashBody> createState() => _SplashBodyState();
}

class _SplashBodyState extends State<SplashBody> {
  // late CachedVideoPlayerController videoPlayerController;
  // late CustomVideoPlayerController _customVideoPlayerController;

  @override
  void dispose() {
    // _customVideoPlayerController.dispose();
    // _customVideoPlayerController.videoPlayerController.removeListener(() {});
    // _customVideoPlayerController.playedOnceNotifier.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();

    // videoPlayerController =
    //     CachedVideoPlayerController.asset('assets/splash_video.mp4')
    //       ..initialize().then((value) => setState(() {}))
    //       ..play();

    // _customVideoPlayerController = CustomVideoPlayerController(
    //   context: context,
    //   videoPlayerController: videoPlayerController..videoPlayerOptions,
    //   customVideoPlayerSettings: const CustomVideoPlayerSettings(
    //       playOnlyOnce: true,
    //       showPlayButton: false,
    //       showFullscreenButton: false,
    //       showDurationPlayed: false,
    //       showDurationRemaining: false,
    //       showMuteButton: false,
    //       showSeekButtons: false,
    //       alwaysShowThumbnailOnVideoPaused: false,
    //       customVideoPlayerProgressBarSettings:
    //           CustomVideoPlayerProgressBarSettings(
    //         showProgressBar: false,
    //       ),
    //       settingsButtonAvailable: false),
    // );
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold();
    // _customVideoPlayerController.playedOnceNotifier.addListener(
    //   () {
    //     widget.completed();
    //   },
    // );
    // return SizedBox(
    //   width: double.infinity,
    //   height: double.infinity,
    //   child: CustomVideoPlayer(
    //     customVideoPlayerController: _customVideoPlayerController,
    //   ),
    // );
  }
}
