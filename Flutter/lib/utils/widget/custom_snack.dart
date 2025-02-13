import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:get/get.dart';

void showCustomSnack(
    {required BuildContext context,
    required String title,
    required String body,
    Duration duration = const Duration(seconds: 3)}) {
  Get.snackbar('', '',
      titleText: Directionality(
        textDirection: TextDirection.rtl,
        child: Text(title,
            textAlign: TextAlign.center, style: context.textTheme.bodySmall),
      ),
      messageText: Directionality(
        textDirection: TextDirection.rtl,
        child: Text(body,
            textAlign: TextAlign.center, style: context.textTheme.bodyMedium),
      ),
      isDismissible: false,
      duration: duration);
}
