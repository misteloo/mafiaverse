import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';

class DcDialog extends StatelessWidget {
  const DcDialog({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);

    return Center(
      child: Container(
        padding: const EdgeInsets.all(16.0),
        decoration: BoxDecoration(
          color: context.theme.scaffoldBackgroundColor,
          borderRadius: BorderRadius.circular(16),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Image.asset(
              'images/img_no_internet.webp',
              width: 100,
              height: 100,
            ),
            const SizedBox(
              height: 16,
            ),
            Text(
              'عدم اتصال به اینترنت',
              style: context.textTheme.bodyMedium,
            )
          ],
        ),
      ),
    );
  }
}
