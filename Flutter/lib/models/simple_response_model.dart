class SimpleResponseModel {
  bool status;
  String msg;
  dynamic data;

  SimpleResponseModel(
      {required this.status, required this.msg, required this.data});

  factory SimpleResponseModel.fromJson(Map<String, dynamic> json) {
    return SimpleResponseModel(
        status: json['status'], msg: json['msg'], data: json['data']);
  }
}
