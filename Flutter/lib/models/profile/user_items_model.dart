class UserItemsModel {
  String id;
  String image;
  String serverId;
  bool active;

  UserItemsModel(
      {required this.active,
      required this.id,
      required this.image,
      required this.serverId});

  factory UserItemsModel.fromJson(dynamic json) {
    return UserItemsModel(
        active: json['active'],
        id: json['id'],
        image: json['image'],
        serverId: json['_id']);
  }
}
