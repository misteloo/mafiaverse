import 'package:flutter/material.dart';
import 'package:get/get.dart';

class BrickButton extends StatelessWidget {
  const BrickButton(
      {super.key, required this.customText, this.child, required this.onClick});
  final String customText;
  final Widget? child;
  final VoidCallback onClick;
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onClick,
      child: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8),
          image: const DecorationImage(
              image: AssetImage('images/brick.jpeg'), fit: BoxFit.cover),
        ),
        child: child ??
            Text(
              customText,
              style: context.textTheme.bodyMedium,
            ),
      ),
    );
  }
}
