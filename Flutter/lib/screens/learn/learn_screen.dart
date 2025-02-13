import 'package:flutter/material.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import 'package:get/get.dart';

class LearnScreen extends StatelessWidget {
  const LearnScreen({super.key});

  @override
  Widget build(BuildContext context) {
    var items = [
      LearnScenarioItem(
          img: 'godfather',
          name: 'پدرخوانده',
          content:
              'پدرخوانده یا رئیس مافیا ، تصمیم گیرنده تیم مافیا برای هدف قرار داده یکی از بازیکنان تیم شهر است.استعلام پدر خوانده برای کاراگاه هیشه منفی است'),
      LearnScenarioItem(
          img: 'nato',
          name: 'ناتو',
          content:
              'یکی از اعضای تیم مافیاست که در شب و با اجازه پدرخوانده قابلیت ناتویی یا حدس نقش یکی از بازیکنان نقشدار شهر را داشته باشد ، اگر ناتو نقش یکی از بازیکنان نقش دار شهر را درست حدس بزند ، در همان شب بازیکن سلاخی شده و دیگر نمیتواند از توانایی خود استفاده کند و همینطور نجات دکتر هم برایش کارساز نیست ، ناتو فقط یکبار میتواند از توانایی خود استفاده کند. در صورت نبود پدرخوانده ناتو در شب بجای پدرخوانده تیر اندازی میکند'),
      LearnScenarioItem(
          img: 'hostage_taker',
          name: 'گروگانگیر',
          content:
              'یکی از اعضای تیم مافیاست که در شب زودتراز پدرخوانده و ناتو بیدار شده و شروع به گروگانگیری اعضای شهروند میکند . آن بازیکنی که توسط گروگانگیر گرو گفته شود ، در همان شب نمیتواند از توانایی خود استفاده کند. گروگانگیر نمیتواند نگهبانی بازی را گرو بگیرد و در صورت نبودن پدرخوانده یا ناتو تیر مافیا به دست گروگانگیر می افتد.'),
      LearnScenarioItem(
          img: 'guard',
          name: 'نگهبان',
          content:
              'نگهبان از اعضای اصلی ساید شهروند است و وظیفه ی نگهبانی از بازیکان  را در شب داشته و همزمان با گروگانگیر در شب بیدار می شود ، نگهبان بازی با رای روز بیرون نمیرود و اگر در هنگام خروج رای لازم را کسب کند گرداننده یا سیستم اعلام نقش بازیکن کرده و از این پس ، این بازیکن به عنوان شهروند ساده به بازی برمیگردد'),
      LearnScenarioItem(
          img: 'rifleman',
          name: 'تنفگدار',
          content:
              'یکی از اعضای تیم شهر است ، این بازیکن در اسلحه خانه خود بی نهایت تیر مشقی و فقط یک تیر جنگی دارد و در شب به بازیکان تیر می دهد بدون اینکه بازیکن از نوع تیری که داده شده خبر داشته باشد ، در روز بعدی بازیکنی که اسلحه آن تیر دارد میتواند تیر اندازی کند و بعد از تیر اندازی نوع تیر شلیک شده مشخص خواهد شد ، در صورتی که تیر جنگی شلیک شود بازیکن هدف فورا به حالت وصیت رفته و بعد از صحبت از بازی خارج می شود و فقط ساید بازیکن به تمامی بازیکنان اعلام میشود . بعد از دادن تیر جنگی تنفگدار این بازیکن دیگر در شب بیدار نمیشود'),
      LearnScenarioItem(
          img: 'commando',
          name: 'تکاور',
          content:
              'تکاور یکی از بازیکنان تیم شهر است ، این بازیکن فقط در صورتی در شب بیدار می شود که توسط تیم مافیا مورد هدف قرار بگیرد ، این بازیکن بعد از بیدار شدن فرصت زیادی نداشته و باید یکی از بازیکنان را به عنوان مافیا شناسایی و به آن شلیک کند ، اگر بازیکنی که به شلیک شده مافیا باشد در روز بعدی آن بازیکن کشته شب خواهد بود و در غیر این صورت تکاور به عنوان کشته شب از بازی خداحافظی خواهد کرد و حتی نجات دکتر هم برایش کار ساز نیست'),
      LearnScenarioItem(
          img: 'doctor',
          name: 'دکتر',
          content:
              'دکتر یکی از بازیکنان تیم شهروند است ، و هر شب بیدار شده و یکی از بازیکنان را نجات میدهد ، این بازیکن خودش را فقط یکبار میتواند نجات دهد'),
      LearnScenarioItem(
          img: 'detective',
          name: 'کاراگاه',
          content:
              'کاراگاه یکی از بازیکنان تیم شهر است و وظیفه پیدا کردن اعضای مافیا را در شب دارد ، استعلام پدرخوانده بازی برای کاراگاه همیشه منفی خواهد بود و در غیرا این صورت اگر بازکن استعلامی جزو مافیا باشد در همان لحظه به بازیکن اعلام می شود'),
      LearnScenarioItem(
          img: 'citizen',
          name: 'شهروند ساده',
          content:
              'شهروند ساده در شب توانایی نداشته و فقط با صحبت های روز و فکت هایی که از بازیکنان بدست می آورد ، میتواند به حذف اعضای مافیا کمک کند')
    ];
    return Scaffold(
      appBar: AppBar(
        title: Text(
          'توضیح سناریو تفنگدار',
          style: context.textTheme.bodyMedium,
        ),
        centerTitle: true,
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            const SizedBox(
              width: double.infinity,
              height: 8,
            ),
            Expanded(
                child: AnimationLimiter(
              child: ListView.builder(
                itemCount: items.length,
                itemBuilder: (context, index) =>
                    AnimationConfiguration.staggeredList(
                  position: index,
                  duration: const Duration(milliseconds: 500),
                  child: SlideAnimation(
                    horizontalOffset: 50,
                    child: FadeInAnimation(
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        child: Column(
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Image.asset(
                                  'images/${items[index].img}.png',
                                  width: 40,
                                  height: 40,
                                ),
                                const SizedBox(
                                  width: 8,
                                ),
                                Text(
                                  items[index].name,
                                  style: context.textTheme.bodyMedium,
                                ),
                              ],
                            ),
                            const SizedBox(
                              height: 4,
                            ),
                            Directionality(
                              textDirection: TextDirection.rtl,
                              child: Text(
                                items[index].content,
                                textAlign: TextAlign.justify,
                                style: const TextStyle(height: 2),
                              ),
                            )
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            ))
          ],
        ),
      ),
    );
  }
}

class LearnScenarioItem {
  String img;
  String name;
  String content;

  LearnScenarioItem(
      {required this.img, required this.name, required this.content});
}
