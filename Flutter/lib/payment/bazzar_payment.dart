// import 'package:flutter_poolakey/flutter_poolakey.dart';
// import 'package:mafia/utils/constants/address.dart';

class BazzarPayment {
  bool connectedToBazzar = false;
  void initPayment() {
    if (!connectedToBazzar) {
      // _connect();
    }
  }

  // void _connect() async {
  //   var connection =
  //       await FlutterPoolakey.connect(bazzar_rsa_key, onDisconnected: () {
  //     _connect();
  //     FlutterPoolakey.connect(bazzar_rsa_key);
  //   });

  //   connectedToBazzar = connection;
  // }

  // Future<PurchaseInfo> purchase(String productId) async {
  //   return await FlutterPoolakey.purchase(productId);
  // }

  // Future<bool> consume(String purchaseToken) async {
  //   return await FlutterPoolakey.consume(purchaseToken);
  // }

  // Future<PurchaseInfo?> unSuccessPurchase(String productId) async {
  //   return await FlutterPoolakey.queryPurchasedProduct(productId);
  // }
}
