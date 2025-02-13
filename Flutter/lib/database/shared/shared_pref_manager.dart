import 'package:get/get.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SharedPrefManager extends GetxService {
  SharedPreferences? _instance;

  Future<SharedPrefManager> init() async {
    if (_instance == null) {
      _instance = await SharedPreferences.getInstance();
      return this;
    }
    return this;
  }

  Future<bool> writeData<T>(String key, T value) async {
    switch (value.runtimeType) {
      case String:
        return await _instance!.setString(key, value as String);
      case int:
        return await _instance!.setInt(key, value as int);
      case bool:
        return await _instance!.setBool(key, value as bool);
      case double:
        return await _instance!.setDouble(key, value as double);
      default:
        return false;
    }
  }

  String? readString(String key) => _instance?.getString(key);
  int? readInt(String key) => _instance?.getInt(key);
  bool? readBool(String key) => _instance?.getBool(key);
  double? readDouble(String key) => _instance?.getDouble(key);

  Future<bool> containData(String key) async {
    return _instance!.containsKey(key);
  }

  Future<bool> removeData(String key) async {
    return await _instance!.remove(key);
  }
}
