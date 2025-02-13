// ignore_for_file: non_constant_identifier_names

class ShopItemModel {
  String marketId;
  String id;
  String? image;
  bool? active;
  bool? active_for_user;
  dynamic price;
  dynamic off;
  dynamic gold;
  dynamic price_after_off;
  bool loading = false;

  ShopItemModel(
      {required this.marketId,
      required this.id,
      required this.image,
      required this.active,
      required this.active_for_user,
      required this.price,
      required this.off,
      required this.gold,
      required this.price_after_off});

  factory ShopItemModel.fromJson(dynamic json) {
    return ShopItemModel(
        marketId: json['_id'] ?? 'undefined',
        id: json['id'],
        image: json['image'],
        active: json['active'],
        active_for_user: json['active_for_user'],
        price: json['price'],
        off: json['off'],
        gold: json['gold'],
        price_after_off: json['price_after_off']);
  }
}
