import 'package:dio/dio.dart';
import 'package:get/get.dart';
import 'package:mafia/utils/constants/address.dart';

class ApiConfiguration extends GetxService {
  Future<Dio> init() async {
    return Dio(
      BaseOptions(
        baseUrl: appBaseUrl,
        connectTimeout: const Duration(seconds: 10),
        receiveTimeout: const Duration(seconds: 10),
        sendTimeout: const Duration(seconds: 10),
      ),
    ); /* ..interceptors.add(PrettyDioLogger()); */
  }
}
