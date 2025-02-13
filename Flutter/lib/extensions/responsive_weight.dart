import 'package:flutter/material.dart';

extension ResponsiveWeight on int {
  int percentHight(BuildContext context) {
    double height = MediaQuery.of(context).size.height;
    return this * height ~/ 100;
  }

  int percentWidth(BuildContext context) {
    double width = MediaQuery.of(context).size.width;
    return this * width ~/ 100;
  }
}
