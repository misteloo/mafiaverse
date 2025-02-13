import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/screens/profile/report/report_bug_screen_controller.dart';

class ReportBugScreen extends GetView<ReportBugScreenController> {
  const ReportBugScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Scaffold(
      appBar: AppBar(
        title: Text(
          'گزارش خطا',
          style: context.textTheme.bodyLarge,
        ),
        centerTitle: true,
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: SingleChildScrollView(
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Directionality(
                  textDirection: TextDirection.rtl,
                  child: Text(
                    'اگر در حین کار با برنامه و یا در هنگام بازی ، به مشکلی برخوردید ، برای تیم فنی ارسال تا بررسی شود',
                    style: context.textTheme.titleMedium,
                    textAlign: TextAlign.justify,
                  ),
                ),
              ),
              const SizedBox(
                height: 16,
              ),
              Directionality(
                textDirection: TextDirection.rtl,
                child: TextField(
                  controller: controller.titleController,
                  decoration: InputDecoration(
                    border: const OutlineInputBorder(),
                    labelText: 'عوان',
                    labelStyle: TextStyle(
                        fontFamily: 'shabnam',
                        fontSize: 14.sp,
                        color: Colors.grey),
                  ),
                  maxLines: 1,
                ),
              ),
              const SizedBox(
                height: 16,
              ),
              Directionality(
                textDirection: TextDirection.rtl,
                child: TextField(
                  controller: controller.contentController,
                  decoration: InputDecoration(
                    border: const OutlineInputBorder(),
                    labelText: 'متن پیام',
                    labelStyle: TextStyle(
                        fontFamily: 'shabnam',
                        fontSize: 14.sp,
                        color: Colors.grey),
                  ),
                  keyboardType: TextInputType.multiline,
                  maxLines: 10,
                ),
              ),
              const SizedBox(
                height: 16,
              ),
              ElevatedButton(
                  onPressed: () {
                    if (controller.loading) {
                      return;
                    }

                    if (controller.contentController.text.isEmpty ||
                        controller.titleController.text.isEmpty) {
                      BotToast.showText(text: 'عنوان و متن نمیتواند خالی باشد');
                      return;
                    }
                    // report
                    controller.sendReport();
                  },
                  child: GetBuilder<ReportBugScreenController>(
                    id: 'loading',
                    builder: (_) => controller.loading == false
                        ? Text(
                            'ارسال پیام',
                            style: context.textTheme.bodyMedium,
                          )
                        : LoadingAnimationWidget.prograssiveDots(
                            color: Colors.white, size: 30),
                  ))
            ],
          ),
        ),
      ),
    );
  }
}
