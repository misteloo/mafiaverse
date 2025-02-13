import 'package:flutter/material.dart';
import 'package:get/get.dart';

class UnreadMessageScreen extends StatelessWidget {
  const UnreadMessageScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          'پیام های شما',
          style: context.textTheme.bodyMedium,
        ),
        centerTitle: true,
        iconTheme: const IconThemeData(color: Colors.white),
      ),
    );
  }
}
