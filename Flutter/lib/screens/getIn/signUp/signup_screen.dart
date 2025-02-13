import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mafia/screens/getIn/signUp/signup_screen_controller.dart';

class SignUpScreen extends GetView<SignUpScreenController> {
  const SignUpScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        resizeToAvoidBottomInset: true,
        body: Column(
          children: [
            const SizedBox(
              width: double.infinity,
            ),
            Expanded(
              child: Padding(
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
                          Text(
                            'ساخت حساب',
                            style: context.textTheme.bodyLarge,
                          ),
                          const SizedBox(
                            height: 16,
                          ),
                          GetBuilder<SignUpScreenController>(
                            builder: (_) => Directionality(
                              textDirection: TextDirection.rtl,
                              child: TextField(
                                controller: controller.usernameField,
                                decoration: InputDecoration(
                                  border:
                                      context.theme.inputDecorationTheme.border,
                                  label: Text(
                                    'نام کاربری',
                                    style: context.textTheme.titleSmall,
                                  ),
                                  counterStyle: context.textTheme.bodySmall,
                                  helperStyle: context.textTheme.titleSmall,
                                  prefixIcon: const Icon(
                                    Icons.person,
                                    color: Colors.grey,
                                  ),
                                  errorText: controller.errorUsername,
                                ),
                                maxLines: 1,
                                keyboardType: TextInputType.text,
                                style: context.textTheme.bodyMedium,
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: 16,
                          ),
                          Text(
                            'شماره تماس اکانتت رو وارد کن',
                            style: context.textTheme.titleSmall,
                          ),
                          const SizedBox(
                            height: 8.0,
                          ),
                          GetBuilder<SignUpScreenController>(
                            builder: (_) => TextField(
                              controller: controller.phoneField,
                              decoration: InputDecoration(
                                  border:
                                      context.theme.inputDecorationTheme.border,
                                  label: const Icon(
                                    Icons.phone_android,
                                    color: Colors.grey,
                                  ),
                                  counterStyle: context.textTheme.bodySmall,
                                  prefix: Text(
                                    '09',
                                    style: context.textTheme.bodyMedium,
                                  ),
                                  errorText: controller.errorPhone),
                              inputFormatters: [
                                FilteringTextInputFormatter.allow(
                                  RegExp('[0-9]'),
                                )
                              ],
                              maxLines: 1,
                              keyboardType: TextInputType.number,
                              style: context.textTheme.bodyMedium,
                              maxLength: 9,
                            ),
                          ),
                          const SizedBox(
                            height: 16,
                          ),
                          Align(
                            alignment: Alignment.centerRight,
                            child: Text(
                              'کد معرف داری ؟',
                              style: context.textTheme.titleSmall,
                            ),
                          ),
                          const SizedBox(
                            height: 8.0,
                          ),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              GetBuilder<SignUpScreenController>(
                                builder: (_) => Row(
                                  children: [
                                    Text(
                                      controller.introducerField.text.isEmpty
                                          ? 'وارد نشده'
                                          : controller.introducerField.text,
                                      style: context.textTheme.titleSmall,
                                    ),
                                    Visibility(
                                      visible: controller
                                          .introducerField.text.isNotEmpty,
                                      child: IconButton(
                                        onPressed: () {
                                          controller.introducerField.clear();
                                          controller.update();
                                        },
                                        icon: const Icon(
                                          Icons.delete,
                                          color: Colors.grey,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              TextButton(
                                onPressed: () async {
                                  showModalBottomSheet(
                                    context: context,
                                    isScrollControlled: true,
                                    backgroundColor:
                                        context.theme.scaffoldBackgroundColor,
                                    shape: const RoundedRectangleBorder(
                                      borderRadius: BorderRadius.only(
                                        topLeft: Radius.circular(15),
                                        topRight: Radius.circular(15),
                                      ),
                                    ),
                                    builder: (context) {
                                      return SignUpIntroducer(
                                          controller: controller);
                                    },
                                  );
                                },
                                child: Container(
                                  padding: const EdgeInsets.all(4.0),
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(8.0),
                                    border: Border.all(color: Colors.grey),
                                  ),
                                  child: Text(
                                    'ورود کد معرف من',
                                    style: context.textTheme.bodyMedium,
                                  ),
                                ),
                              )
                            ],
                          ),
                          const SizedBox(
                            height: 16,
                          ),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceAround,
                            children: [
                              Row(
                                children: [
                                  Obx(
                                    () => Checkbox(
                                      value: controller.rxPrivacyPolicy.value,
                                      onChanged: (value) {
                                        controller.rxPrivacyPolicy.toggle();
                                      },
                                      activeColor: Colors.cyan,
                                    ),
                                  ),
                                  Text(
                                    'تایید',
                                    style: context.textTheme.titleMedium,
                                  )
                                ],
                              ),
                              TextButton(
                                onPressed: () {
                                  showModalBottomSheet(
                                    isScrollControlled: false,
                                    backgroundColor: Colors.grey.shade800,
                                    context: context,
                                    builder: (context) {
                                      return const PrivacyPolicy();
                                    },
                                  );
                                },
                                child: Text(
                                  'حریم خصوصی و قوانین',
                                  style: context.textTheme.bodyMedium,
                                ),
                              )
                            ],
                          ),
                          const SizedBox(
                            height: 16,
                          ),
                          GetBuilder<SignUpScreenController>(
                            id: 'loading',
                            builder: (_) => Stack(
                              alignment: Alignment.center,
                              children: [
                                Visibility(
                                  visible: !controller.loading,
                                  child: Obx(
                                    () => ElevatedButton(
                                      style: ButtonStyle(
                                          backgroundColor: controller
                                                      .rxPrivacyPolicy.value ==
                                                  false
                                              ? const WidgetStatePropertyAll(
                                                  Colors.grey)
                                              : context
                                                  .theme
                                                  .elevatedButtonTheme
                                                  .style
                                                  ?.backgroundColor),
                                      onPressed: () {
                                        if (controller.rxPrivacyPolicy.value ==
                                            false) {
                                          return;
                                        }
                                        controller.checkInputs(
                                            controller.phoneField.text,
                                            controller.usernameField.text);
                                      },
                                      child: Text(
                                        'ایجاد حساب',
                                        style: context.textTheme.bodySmall,
                                      ),
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
                                onPressed: () {
                                  Get.back();
                                },
                                child: Text(
                                  'ورود به حساب',
                                  style: context.textTheme.bodySmall,
                                ),
                              ),
                              const SizedBox(
                                width: 8.0,
                              ),
                              Text(
                                'حساب دارم',
                                style: context.textTheme.titleSmall,
                              )
                            ],
                          ),
                          const SizedBox(
                            height: 16,
                          ),
                          Image.asset(
                            'images/img_signup.webp',
                            width: 200,
                            height: 200,
                          ),
                          const SizedBox(
                            height: 16,
                          )
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class PrivacyPolicy extends StatelessWidget {
  const PrivacyPolicy({super.key});

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context);
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: SingleChildScrollView(
        child: Column(
          children: [
            const SizedBox(
              width: double.infinity,
            ),
            Text(
              'حریم خصوصی و قوانین عصر مافیا',
              style: context.textTheme.bodyLarge,
            ),
            const SizedBox(
              height: 16,
            ),
            Text('حریم کاربران', style: context.textTheme.titleMedium),
            const SizedBox(
              height: 8,
            ),
            Directionality(
                textDirection: TextDirection.rtl,
                child: Text(
                  'کاربران جهت ثبت نام در برنامه لازم است از طریق شماره تماس احراز شده خود اقدام نمایند و عصر مافیا متاهد به نگهداری و عدم انتشار این اطالاعات از بازیکنان خود میباشد ، و این شماره تماس صرفا جهت احراز حقیقی بودن فرد کاربرد داشته ، و فقط در مواردی که بازیکن موفق به کسب جوایز نقدی و یا غیر نقدی شده باشد از سوی کارشناسان برنامه با وی تماس حاصل خواهد شد و استفاده دیگری نخواهد داشت ',
                  textAlign: TextAlign.justify,
                  style: context.textTheme.bodyMedium,
                )),
            const SizedBox(
              height: 16,
            ),
            Text('دسترسی های برنامه', style: context.textTheme.titleMedium),
            const SizedBox(
              height: 8,
            ),
            Directionality(
                textDirection: TextDirection.rtl,
                child: Text(
                  '1 دسترسی اعلان: این دسترسی صرفا اطلاع رسانی بازیکن در زمان های حساس مثل پیدا شدن بازی و دیگر اعلان های مهم که کابر نیاز به اطلاع دارد مورد استفاده قرار میگیرد و جهت ورود به برنامه لازم به تایید می باشد.\n 2 دسترسی میکروفون: بازیکنان جهت ورود به بازی و ارتباط از طریق کانال صوتی موجود در بازی ،لازم به تایید می باشد. \n 3 دسترسی دوربین: در بخش بازی های حضوری جهت اینکه بازیکن بتواند به بازی ایجاد شده که توسط گرداننده یک کد QR  ساخته شده از سمت برنامه است ، ملحق شود لازم به تایید می باشد.',
                  textAlign: TextAlign.justify,
                  style: context.textTheme.bodyMedium,
                )),
            const SizedBox(
              height: 16,
            ),
            Text('قوانین بازی و نام گذاری',
                style: context.textTheme.titleMedium),
            const SizedBox(
              height: 8,
            ),
            Directionality(
                textDirection: TextDirection.rtl,
                child: Text(
                  'کاربران جهت ورود به برنامه نیاز به انتخاب نام کاربری داشته که ملزم به رعایت برخی شئونات در این نامگذاری می باشند ، چنان چه هریک از بازیکنان ، نام کاربری نامناسبی برای حساب خود انتخاب کرده باشند ،حساب بازیکن به صورت یکطرفه از سمت عصر مافیا مسدود خواهد گشت',
                  textAlign: TextAlign.justify,
                  style: context.textTheme.bodyMedium,
                )),
            const SizedBox(
              height: 8,
            ),
            Directionality(
                textDirection: TextDirection.rtl,
                child: Text(
                  'با علم به اینکه بازی مافیا یک بازی دسته جمعی بوده رعایت و عفت کلام ، ملزم به اجراست و در صورت گزارش از طرف دیگر بازیکنان ، فرد متخطی مجازات و دسترسی بازیکن به برنامه و امکانات محدود خواهد شد',
                  textAlign: TextAlign.justify,
                  style: context.textTheme.bodyMedium,
                )),
            const SizedBox(
              height: 8,
            ),
            Directionality(
                textDirection: TextDirection.rtl,
                child: Text(
                  'در بازی هایی که احراز سنی صورت گرفته باشد ، و فرد شرکت کننده زیر سن 18 سال باشد ، فرد متخطی مجازات و دسترسی بازیکن به برنامه و امکانات محدود خواهد شد',
                  textAlign: TextAlign.justify,
                  style: context.textTheme.bodyMedium,
                )),
          ],
        ),
      ),
    );
  }
}

class SignUpIntroducer extends StatelessWidget {
  final SignUpScreenController controller;
  const SignUpIntroducer({super.key, required this.controller});

  @override
  Widget build(BuildContext context) {
    TextEditingController currentEdt = TextEditingController();
    currentEdt.text = controller.introducerField.text;
    return Container(
      padding: EdgeInsets.only(
          bottom: Get.mediaQuery.viewInsets.bottom,
          top: 16,
          left: 16,
          right: 16),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            'کد معرف',
            style: context.textTheme.bodyLarge,
          ),
          const SizedBox(
            height: 8,
          ),
          Text(
            'با کد معرف میتونی هم خودت و هم دوستت باهم جایزه بگیرین',
            style: context.textTheme.titleSmall,
          ),
          const SizedBox(
            height: 16,
          ),
          TextField(
            controller: currentEdt,
            decoration: InputDecoration(
              border: context.theme.inputDecorationTheme.border,
              label: const Icon(
                Icons.abc,
                color: Colors.grey,
              ),
            ),
            maxLines: 1,
            style: context.textTheme.bodyMedium,
          ),
          const SizedBox(
            height: 16,
          ),
          ElevatedButton(
            onPressed: () {
              controller.introducerField = currentEdt;
              controller.update();
              Get.back();
            },
            style: context.theme.elevatedButtonTheme.style,
            child: Text(
              'تایید',
              style: context.textTheme.bodySmall,
            ),
          ),
          const SizedBox(
            height: 16,
          ),
        ],
      ),
    );
  }
}
