import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:get/get.dart';
// ignore: library_prefixes
import 'package:dio/dio.dart' as dioRes;

class ApiRepository extends GetxService {
  Future<ApiRepository> init() async {
    return this;
  }

  final Dio _api = Get.find();

  /// login
  Future<Either<void, dioRes.Response>> loginRequest(Map body) async {
    try {
      return Right(await _api.post('/registion/log_in', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// signup
  Future<Either<void, dioRes.Response>> singUpRequest(Map body) async {
    try {
      return Right(await _api.post('/registion/sign_up', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  ///confirm phone
  Future<Either<void, dioRes.Response>> signUpConfirmPhone(Map body) async {
    try {
      return Right(
          await _api.post('/registion/sign_up_confirm_phone', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// confirm code
  Future<Either<void, dioRes.Response>> loginConfirmPhone(Map body) async {
    try {
      return Right(
          await _api.post('/registion/log_in_confirm_phone', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// find match gold
  Future<Either<void, dioRes.Response>> findMatchGold(Map body) async {
    try {
      return Right(await _api.post('/user/find_match_gold', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// user profile
  Future<Either<void, dioRes.Response>> myProfile(Map body) async {
    try {
      return Right(await _api.post('/user/profile', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// market items
  Future<Either<void, dioRes.Response>> getMarketItems(Map body) async {
    try {
      return Right(await _api.post('/items/items_list', data: body));
    } catch (e) {
      return const Left(null);
    }
  }

  /// check purchase correction
  Future<Either<void, dioRes.Response>> checkPurchase(Map body) async {
    try {
      return Right(
          await _api.post('/transaction/confirm_transaction', data: body));
    } catch (e) {
      return const Left(null);
    }
  }

  /// get leader board
  Future<Either<void, dioRes.Response>> getLeaderBoard(Map body) async {
    try {
      return Right(await _api.post('/ranking/', data: body));
    } catch (e) {
      return const Left(null);
    }
  }

  /// avatar list
  Future<Either<void, dioRes.Response>> getUserAvatarList(Map body) async {
    try {
      return Right(await _api.post('/user/items_list', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// purchase avatar
  Future<Either<void, dioRes.Response>> purchaseAvatar(Map body) async {
    try {
      return Right(await _api.post('/items/buy', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// check user name before change it
  Future<Either<void, dioRes.Response>> checkUserName(Map body) async {
    try {
      return Right(await _api.post('/user/check_new_user_name', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// change username
  Future<Either<void, dioRes.Response>> changeUsername(Map body) async {
    try {
      return Right(await _api.post('/user/change_user_name', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// change avatar image
  Future<Either<void, dioRes.Response>> changeAvatar(Map body) async {
    try {
      return Right(await _api.post('/user/edit_profile', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// report bug
  Future<Either<void, dioRes.Response>> reportBug(Map body) async {
    try {
      return Right(await _api.post('/user/support', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// payment gateway
  Future<Either<void, dioRes.Response>> paymentGateway(Map body) async {
    try {
      return Right(
          await _api.post('/transaction/create_transaction', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// report player
  Future<Either<void, dioRes.Response>> reportPlayer(Map body) async {
    try {
      return Right(await _api.post('/report/game_play', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// character list for lobby
  Future<Either<void, dioRes.Response>> getCharacterListForLobby() async {
    try {
      return Right(await _api.get('/lobby/deck'));
    } catch (_) {
      return const Left(null);
    }
  }

  /// total lobby games
  Future<Either<void, dioRes.Response>> getLobbyGames(Map body) async {
    try {
      return Right(await _api.post('/lobby/list', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// user profile
  Future<Either<void, dioRes.Response>> checkProfile(Map body) async {
    try {
      return Right(await _api.post('/user/others_profile', data: body));
    } catch (_) {
      return const Left(null);
    }
  }

  /// rate creator
  Future<Either<void, dioRes.Response>> rateCreator(Map body) async {
    try {
      return Right(await _api.post('/lobby/rate_creator', data: body));
    } catch (_) {
      return const Left(null);
    }
  }
}
