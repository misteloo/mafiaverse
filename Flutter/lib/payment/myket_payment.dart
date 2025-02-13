import 'package:mafia/utils/constants/address.dart';
import 'package:myket_iap/myket_iap.dart';
import 'package:myket_iap/util/iab_result.dart';
import 'package:myket_iap/util/purchase.dart';

class MyketPayment {
  IabResult? iab;
  void initPayment() async {
    iab = await MyketIAP.init(rsaKey: myket_rsa_key);
  }

  Future<Map<dynamic, dynamic>> purchase(String productId) async {
    return await MyketIAP.launchPurchaseFlow(
        sku: productId, payload: productId);
  }

  Future<Map<dynamic, dynamic>> consume(Purchase purchase) async {
    return await MyketIAP.consume(purchase: purchase);
  }
}
