import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/screens/getIn/login/login_screen_controller.dart';

class LoginScreen extends GetView<LoginScreenController> {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return SafeArea(
      child: Scaffold(
        resizeToAvoidBottomInset: false,
        body: Padding(
          padding: const EdgeInsets.all(50.0),
          child: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Text(
                  'ورود به حساب',
                  style: context.textTheme.bodyLarge,
                ),
                const SizedBox(
                  height: 16,
                ),
                Obx(
                  () => TextField(
                    controller: controller.textController,
                    decoration: InputDecoration(
                      border: context.theme.inputDecorationTheme.border,
                      prefix: Text(
                        '09',
                        style: context.textTheme.bodyMedium,
                        textAlign: TextAlign.center,
                      ),
                      label: const Icon(
                        Icons.phone_android,
                        color: Colors.grey,
                      ),
                      counterStyle: context.textTheme.bodySmall,
                      errorText: controller.rxErrorField.value,
                    ),
                    inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                    keyboardType: TextInputType.number,
                    style: context.textTheme.bodyMedium,
                    maxLength: 9,
                  ),
                ),
                const SizedBox(
                  height: 16,
                ),
                GetBuilder<LoginScreenController>(
                  id: 'loading',
                  builder: (_) => Stack(
                    children: [
                      Visibility(
                        visible: !controller.loading,
                        child: ElevatedButton(
                          style: context.theme.elevatedButtonTheme.style,
                          onPressed: () {
                            //popup a loading toast

                            controller.checkPhoneInput(
                                controller.textController.text);
                          },
                          child: Text(
                            'ورود به حساب',
                            style: context.textTheme.bodySmall,
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
                ),
                const SizedBox(
                  height: 16,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    TextButton(
                      onPressed: () async {
                        await Get.toNamed(controller.routing('signup'));
                      },
                      child: Text(
                        'ایجاد حساب',
                        style: context.textTheme.bodySmall,
                      ),
                    ),
                    const SizedBox(
                      width: 8.0,
                    ),
                    Text(
                      'حساب نداری؟',
                      style: context.textTheme.titleSmall,
                    ),
                  ],
                ),
                const SizedBox(
                  height: 16,
                ),
                Image.asset(
                  'images/img_login.webp',
                  width: 200,
                  height: 200,
                ),
                const SizedBox(
                  height: 20,
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
