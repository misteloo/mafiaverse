import 'package:audioplayers/audioplayers.dart';
import 'package:bot_toast/bot_toast.dart';
// import 'package:flutter_poolakey/purchase_info.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/shop/shop_item_model.dart';
import 'package:mafia/payment/bazzar_payment.dart';
import 'package:mafia/payment/myket_payment.dart';
import 'package:mafia/utils/constants/payment_enum.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:myket_iap/myket_iap.dart';
import 'package:myket_iap/util/iab_result.dart';
import 'package:myket_iap/util/purchase.dart';
import 'package:vibration/vibration.dart';

class ShopScreenController extends GetxController {
  ShopScreenController() {
    // _bazzarPayment.initPayment();
    _myketPayment.initPayment();
  }

  // injections
  final BazzarPayment _bazzarPayment = Get.find();
  final MyketPayment _myketPayment = Get.find();
  final ApiRepository _api = Get.find();
  final SharedPrefManager _shared = Get.find();

  // variables
  final List<ShopItemModel> goldList =
      List<ShopItemModel>.empty(growable: true);
  final List<ShopItemModel> avatarList =
      List<ShopItemModel>.empty(growable: true);
  int userGolds = 0;

  final paymentType = PaymentType.Myket;

  bool purchaseItemLoading = false;

  String token() {
    return _shared.readString('token')!;
  }

  void _startVibrate() async {
    if (await Vibration.hasVibrator() == true) {
      Vibration.vibrate();
    }
  }

  void getMarketItems() async {
    Map body = {'token': token()};
    var response = await _api.getMarketItems(body);

    response.fold(
      (left) {
        BotToast.showText(text: 'خطا در ارتباط');
      },
      (right) {
        var json = right.data['data'];
        var userGold = json['user_gold']; // int
        userGolds = userGold;
        update(['userGold']);
        var items = json['items'];

        items.map((it) {
          var type = it['type'];
          var typeItem = it['items'];
          var itemList = typeItem
              .map<ShopItemModel>((e) => ShopItemModel.fromJson(e))
              .toList() as List<ShopItemModel>;
          if (type == 'gold') {
            goldList.clear();
            goldList.addAll(itemList);

            update(['goldItems']);
          }
          if (type == 'avatar') {
            avatarList.clear();
            avatarList.addAll(itemList);
            update(['avatarItems']);
          }
        }).toList();
      },
    );
  }

  void purchaseAvatar(String it, Function(bool) callback) async {
    purchaseItemLoading = true;
    update(['purchaseLoading']);
    Map body = {'token': token(), 'item_id': it};

    var response = await _api.purchaseAvatar(body);

    response.fold((left) {
      _startVibrate();
      BotToast.showText(text: 'خطا در خرید');
      purchaseItemLoading = false;
      update(['purchaseLoading']);
    }, (right) {
      _startVibrate();
      purchaseItemLoading = false;
      update(['purchaseLoading']);
      if (right.data['status'] == true) {
        BotToast.showText(text: 'محصول شما خریداری شد');
        callback(true);
      } else {
        BotToast.showText(text: right.data['msg']);
        callback(false);
      }
    });
  }

  void purchaseGold(String id) async {
    if (paymentType == PaymentType.Gateway) {
      BotToast.showLoading();
      Map body = {'plan': id, 'token': _shared.readString('token')};
      var response = await _api.paymentGateway(body);
      response.fold((left) {
        BotToast.showText(text: 'خطا در ارتباط');
        BotToast.closeAllLoading();
      }, (right) async {
        BotToast.closeAllLoading();
        if (right.data['status']) {
          var uri = Uri.parse(right.data['data']['url']);
          await launchUrl(uri);
        } else {
          BotToast.showText(text: right.data['msg']);
        }
      });
    } else if (paymentType == PaymentType.Myket) {
      Map response = await _myketPayment.purchase(id);

      Purchase? purchase = response[MyketIAP.PURCHASE];
      IabResult? purchaseResult = response[MyketIAP.RESULT];
      if (purchaseResult?.isSuccess() == true) {
        // check purchase info
        _checkPurchase(id, purchase!.mToken, purchase);

        return;
      }

      if (purchaseResult?.isFailure() == true) {
        BotToast.showText(text: 'خطا در خرید');
      }
    } else {
      if (_bazzarPayment.connectedToBazzar) {
        //   var unSuccessPurchase = await _bazzarPayment.unSuccessPurchase(id);
        //   if (unSuccessPurchase != null) {
        //     // check purchase info
        //     _checkPurchase(id, unSuccessPurchase.purchaseToken, null);
        //   } else {
        //     try {
        //       PurchaseInfo info = await _bazzarPayment.purchase(
        //         id,
        //       );

        //       // check purchase info
        //       _checkPurchase(id, info.purchaseToken, null);
        //     } catch (e) {
        //       BotToast.showText(text: 'خرید لغو شد');
        //     }
        //   }
        // } else {
        //   BotToast.showText(text: 'عدم اتصال به کافه بازار');
      }
    }
  }

  void _checkPurchase(
      String sku, String purchaseToken, dynamic myketPurchase) async {
    Map body = {
      'token': token(),
      'plan': sku,
      'tr_token': purchaseToken,
      'platform': 'myket'
    };
    var response = await _api.checkPurchase(body);

    response.fold((left) {
      BotToast.showText(text: 'خطا در فرایند خرید');
    }, (right) async {
      // payment type
      if (paymentType == PaymentType.Myket) {
        if (right.data['status']) {
          // consume
          var consumeResponse = await _myketPayment.consume(myketPurchase);
          IabResult consumeResult = consumeResponse[MyketIAP.RESULT];
          if (consumeResult.isSuccess()) {
            getMarketItems();
            BotToast.showText(text: 'خرید با موفقیت انجام شد');
            AudioPlayer().play(AssetSource('coin_sound.mp3'));
            _startVibrate();
          } else {
            BotToast.showText(text: 'خرید شما لغو شد');
          }
        } else {
          BotToast.showText(text: right.data['msg']);
        }
      } else if (paymentType == PaymentType.Bazzar) {
        // if (right.data['status']) {
        //   // consume product
        //   bool consumeResult = await _bazzarPayment.consume(purchaseToken);
        //   if (consumeResult) {
        //     getMarketItems();
        //     BotToast.showText(text: 'خرید با موفقیت انجام شد');
        //     AudioPlayer().play(AssetSource('coin_sound.mp3'));
        //     _startVibrate();
        //   } else {
        //     BotToast.showText(text: 'خرید شما لغو شد');
        //   }
        // } else {
        //   BotToast.showText(text: right.data['msg']);
        // }
      }
    });
  }
}
