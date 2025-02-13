import 'dart:async';

import 'package:bot_toast/bot_toast.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_volume_controller/flutter_volume_controller.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_configuration.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/main_controller.dart';
import 'package:mafia/screens/game/find_match/find_match_screen_mw.dart';
import 'package:mafia/screens/lobby/create/create_lobby_screen.dart';
import 'package:mafia/screens/lobby/create/create_lobby_screen_binding.dart';
import 'package:mafia/screens/lobby/game/lobby_game_screen.dart';
import 'package:mafia/screens/lobby/game/lobby_game_screen_binding.dart';
import 'package:mafia/screens/lobby/lobby_screen.dart';
import 'package:mafia/screens/lobby/lobby_screen_binding.dart';
import 'package:mafia/screens/game/end_game/end_game_screen.dart';
import 'package:mafia/screens/game/end_game/end_game_screen_binding.dart';
import 'package:mafia/screens/game/find_match/find_match_binding.dart';
import 'package:mafia/screens/game/find_match/find_match_screen.dart';
import 'package:mafia/screens/game/nato_game/nato_screen.dart';
import 'package:mafia/screens/game/nato_game/nato_screen_binding.dart';
import 'package:mafia/screens/game/reconnect/reconnect_screen.dart';
import 'package:mafia/screens/game/reconnect/reconnect_screen_binding.dart';
import 'package:mafia/screens/game/select_character/select_character_screen.dart';
import 'package:mafia/screens/game/select_character/select_character_screen_binding.dart';
import 'package:mafia/screens/getIn/confirmCode/confirm_code_binding.dart';
import 'package:mafia/screens/getIn/confirmCode/confirm_code_screen.dart';
import 'package:mafia/screens/getIn/login/login_screen.dart';
import 'package:mafia/screens/getIn/login/login_screen_binding.dart';
import 'package:mafia/screens/getIn/signUp/signup_screen.dart';
import 'package:mafia/screens/getIn/signUp/signup_screen_binding.dart';
import 'package:mafia/screens/home/home_screen.dart';
import 'package:mafia/screens/home/home_screen_binding.dart';
import 'package:mafia/screens/leader_board/leader_board_screen.dart';
import 'package:mafia/screens/leader_board/leader_board_screen_binding.dart';
import 'package:mafia/screens/learn/learn_screen.dart';
import 'package:mafia/screens/lobby/waiting/lobby_waiting_room_screen.dart';
import 'package:mafia/screens/lobby/waiting/lobby_waiting_room_screen_binding.dart';
import 'package:mafia/screens/local_game/local_game_screen.dart';
import 'package:mafia/screens/local_game/local_game_screen_binding.dart';
import 'package:mafia/screens/local_game/select_deck/select_deck_screen.dart';
import 'package:mafia/screens/local_game/select_deck/select_deck_screen_binding.dart';
import 'package:mafia/screens/main_btm_nav_holder/main_bottom_nav_holder.dart';
import 'package:mafia/screens/main_btm_nav_holder/main_bottom_nav_holder_binding.dart';
import 'package:mafia/screens/profile/edit_profile/edit_profile_screen.dart';
import 'package:mafia/screens/profile/edit_profile/edit_profile_screen_binding.dart';
import 'package:mafia/screens/profile/profile_screen.dart';
import 'package:mafia/screens/profile/profile_screen_binding.dart';
import 'package:mafia/screens/profile/report/report_bug_screen.dart';
import 'package:mafia/screens/profile/report/report_bug_screen_binding.dart';
import 'package:mafia/screens/shop/shop_screen.dart';
import 'package:mafia/screens/shop/shop_screen_binding.dart';
import 'package:mafia/screens/splash/splash_screen.dart';
import 'package:mafia/screens/splash/splash_screen_binding.dart';
import 'package:mafia/screens/unread_message/unread_message_screen.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/socket/services/lobby/socket_lobby_create_service.dart';
import 'package:mafia/utils/socket/services/find_match/socket_find_match_service.dart';
import 'package:mafia/utils/socket/services/lobby/socket_lobby_game_service.dart';
import 'package:mafia/utils/socket/services/lobby/socket_lobby_service.dart';
import 'package:mafia/utils/socket/services/local/socket_local_game_service.dart';
import 'package:mafia/utils/socket/socket_manager.dart';
import 'package:mafia/utils/socket/services/nato_game/socket_nato_game_service.dart';
import 'package:mafia/utils/socket/services/reconnect/socket_reconnect_service.dart';
import 'package:mafia/utils/socket/services/nato_game/socket_select_character_service.dart';
import 'package:mafia/utils/socket/socket_service.dart';
import 'package:mafia/utils/socket/services/lobby/socket_lobby_waiting_service.dart';
import 'package:mafia/utils/widget/app_theme.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:wakelock_plus/wakelock_plus.dart';
import 'firebase_options.dart';
import 'package:firebase_crashlytics/firebase_crashlytics.dart';

void main() async {
  runZonedGuarded(() async {
    WidgetsFlutterBinding.ensureInitialized();
    // disable device rotate
    await SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
    ]);
    // firebase configuration
    await Firebase.initializeApp(
      options: DefaultFirebaseOptions.currentPlatform,
    );

    await FlutterVolumeController.updateShowSystemUI(true);

    // Pass all uncaught errors from the framework to Crashlytics.
    FlutterError.onError = FirebaseCrashlytics.instance.recordFlutterError;

    // init services
    await initServices();
    // keep screen on
    WakelockPlus.enable();
    // app
    runApp(const App());

    FirebaseMessaging messaging = FirebaseMessaging.instance;
    if (GetPlatform.isWeb) {
      NotificationSettings _ = await messaging.requestPermission(
        alert: true,
        announcement: true,
        badge: true,
        carPlay: false,
        criticalAlert: true,
        provisional: false,
        sound: true,
      );
    } else {
      if (await Permission.notification.isDenied) {
        Permission.notification.request();
      }

      /* var fToken = await messaging.getToken();
    print(fToken);
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      print('NOTIFICATION ${message.notification?.body}');
      if (message.notification != null) {}
    }); */
    }
  }, (error, stack) => FirebaseCrashlytics.instance.recordError(error, stack));
}

Future<void> initServices() async {
  await Get.putAsync(() => HashRouting().init());
  await Get.putAsync(() => SharedPrefManager().init());
  await Get.putAsync(() => ApiConfiguration().init());
  await Get.putAsync(() => ApiRepository().init());
  await Get.putAsync(() => SocketService().init());
  await Get.putAsync(() => SocketFindMatchService().init());
  await Get.putAsync(() => SocketSelectCharacterService().init());
  await Get.putAsync(() => SocketReconnectService().init());
  await Get.putAsync(() => SocketNatoGameService().init());
  await Get.putAsync(() => SocketLocalGameService().init());
  await Get.putAsync(() => SocketCreateLobbyService().init());
  await Get.putAsync(() => SocketWaitingLobbyService().init());
  await Get.putAsync(() => SocketLobbyService().init());
  await Get.putAsync(() => SocketLobbyGameService().init());
  await Get.putAsync(() => SocketManager().init());
}

class App extends GetView<MainController> {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    Get.lazyPut(() => MainController(), fenix: true);
    // font size
    ScreenUtil.init(context);

    return GetMaterialApp(
      defaultTransition: Transition.cupertino,
      theme: AppTheme.light(),
      darkTheme: AppTheme.dark(),
      locale: const Locale('en'),
      themeMode: ThemeMode.light,
      initialRoute: controller.initialRout(),
      unknownRoute: GetPage(
        name: controller.initialRout(),
        page: () => const SplashScreen(),
        binding: SplashScreenBinding(),
      ),
      builder: BotToastInit(),
      routingCallback: (Routing? value) {
        controller.modifyBackgroundMusic(value);
      },
      getPages: [
        GetPage(
          name: controller.initialRout(),
          page: () => const SplashScreen(),
          binding: SplashScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('login'),
          page: () => const LoginScreen(),
          binding: LoginScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('signup'),
          page: () => const SignUpScreen(),
          binding: SignUpScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('confirm_code'),
          page: () => const ConfirmCodeScreen(),
          binding: ConfirmCodeScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('main_btm_nav_holder'),
          page: () => const MainBottomNavHolderScreen(),
          binding: MainBottomNavHolderBinding(),
        ),
        GetPage(
          name: controller.getPage('home'),
          page: () => const HomeScreen(),
          binding: HomeScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('profile'),
          page: () => const ProfileScreen(),
          binding: ProfileScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('shop'),
          page: () => const ShopScreen(),
          binding: ShopScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('leader_board'),
          page: () => const LeaderBoardScreen(),
          binding: LeaderBoardScreenBinding(),
        ),
        GetPage(
            name: controller.getPage('find_match'),
            page: () => const FindMatchScreen(),
            binding: FindMatchScreenBinding(),
            middlewares: [FindMatchScreenMw()]),
        GetPage(
          name: controller.getPage('select_character'),
          page: () => const SelectCharacterScreen(),
          binding: SelectCharacterScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('nato_game'),
          page: () => const NatoScreen(),
          binding: NatoScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('reconnect'),
          page: () => const ReconnectScreen(),
          binding: ReconnectScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('end_game_result'),
          page: () => const EndGameScreen(),
          binding: EndGameScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('edit_profile'),
          page: () => const EditProfileScreen(),
          binding: EditProfileScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('local_game'),
          page: () => const LocalGameScreen(),
          binding: LocalGameScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('select_deck'),
          page: () => const SelectDeckScreen(),
          binding: SelectDeckScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('unread_message'),
          page: () => const UnreadMessageScreen(),
        ),
        GetPage(
          name: controller.getPage('learn'),
          page: () => const LearnScreen(),
        ),
        GetPage(
          name: controller.getPage('report_bug'),
          page: () => const ReportBugScreen(),
          binding: ReportBugScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('lobby'),
          page: () => const LobbyScreen(),
          binding: LobbyScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('create_lobby'),
          page: () => const CreateLobbyScreen(),
          binding: CreateLobbyScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('lobby_waiting_room'),
          page: () => LobbyWaitingRoomScreen(),
          binding: LobbyWaitingRoomScreenBinding(),
        ),
        GetPage(
          name: controller.getPage('lobby_game'),
          page: () => const LobbyGameScreen(),
          binding: LobbyGameScreenBinding(),
        )
      ],
    );
  }
}
