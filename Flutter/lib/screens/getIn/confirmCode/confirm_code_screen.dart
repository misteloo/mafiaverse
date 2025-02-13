import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/screens/getIn/confirmCode/confirm_code_controller.dart';

class ConfirmCodeScreen extends GetView<ConfirmCodeScreenController> {
  const ConfirmCodeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // run timer
    controller.startCountDownTimer();
    return SafeArea(
      child: PopScope(
        canPop: false,
        onPopInvoked: (_) {},
        child: Scaffold(
          body: Padding(
            padding: const EdgeInsets.all(50.0),
            child: SingleChildScrollView(
              child: AnimationLimiter(
                child: Column(
                  children: AnimationConfiguration.toStaggeredList(
                    duration: const Duration(milliseconds: 500),
                    childAnimationBuilder: (widget) => SlideAnimation(
                      horizontalOffset: 50.0,
                      child: FadeInAnimation(child: widget),
                    ),
                    children: [
                      const SizedBox(
                        width: double.infinity,
                      ),
                      Text(
                        'تایید شماره تماس',
                        style: context.textTheme.bodyLarge,
                      ),
                      const SizedBox(
                        height: 16.0,
                      ),
                      Align(
                        alignment: Alignment.centerRight,
                        child: Text(
                          'کد تایید ارسال شده رو وارد کن',
                          style: context.textTheme.titleMedium,
                        ),
                      ),
                      const SizedBox(
                        height: 8.0,
                      ),
                      GetBuilder<ConfirmCodeScreenController>(
                        id: 'edtConfirmCode',
                        builder: (_) => TextField(
                          controller: controller.confirmCodeEditing,
                          decoration: InputDecoration(
                            border: context.theme.inputDecorationTheme.border,
                            label: const Icon(
                              Icons.phone_android,
                              color: Colors.grey,
                            ),
                            hintText: '__ __ __ __',
                            hintStyle: context.textTheme.titleMedium,
                            counterStyle: context.textTheme.bodyMedium,
                            errorText: controller.errorEdtConfirm,
                            errorStyle:
                                context.theme.inputDecorationTheme.errorStyle,
                          ),
                          maxLines: 1,
                          inputFormatters: [
                            FilteringTextInputFormatter.digitsOnly
                          ],
                          keyboardType: TextInputType.number,
                          style: context.textTheme.bodyMedium,
                          textAlign: TextAlign.center,
                          maxLength: 4,
                        ),
                      ),
                      const SizedBox(
                        height: 16,
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          Obx(
                            () => ElevatedButton(
                              onPressed: () {
                                if (controller.rxTimer.value == 0) {
                                  Get.back();
                                }
                              },
                              style: controller.rxTimer.value > 0
                                  ? const ButtonStyle(
                                      backgroundColor:
                                          MaterialStatePropertyAll(Colors.grey),
                                    )
                                  : context.theme.elevatedButtonTheme.style,
                              child: Text(
                                '${controller.rxTimer} بازگشت',
                                style: context.textTheme.bodyMedium,
                              ),
                            ),
                          ),
                          GetBuilder<ConfirmCodeScreenController>(
                            id: 'loading',
                            builder: (_) => Stack(
                              alignment: Alignment.center,
                              children: [
                                Visibility(
                                  visible: !controller.loading,
                                  child: ElevatedButton(
                                    onPressed: () {
                                      controller.checkInput(
                                          controller.confirmCodeEditing.text);
                                    },
                                    style:
                                        context.theme.elevatedButtonTheme.style,
                                    child: Text(
                                      'تایید شماره',
                                      style: context.textTheme.bodyMedium,
                                    ),
                                  ),
                                ),
                                Visibility(
                                  visible: controller.loading,
                                  child: LoadingAnimationWidget.prograssiveDots(
                                      color: Colors.white, size: 26),
                                )
                              ],
                            ),
                          )
                        ],
                      ),
                      const SizedBox(
                        height: 20,
                      ),
                      Image.asset(
                        'images/img_verification.webp',
                        width: 200,
                        height: 200,
                        fit: BoxFit.contain,
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
