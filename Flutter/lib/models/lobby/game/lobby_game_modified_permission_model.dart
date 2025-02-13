class LobbyGameModifiedPermissionModel {
  bool modified = false;
  bool active;
  String name;
  String label;

  LobbyGameModifiedPermissionModel(
      {required this.active, required this.name, required this.label});
}
