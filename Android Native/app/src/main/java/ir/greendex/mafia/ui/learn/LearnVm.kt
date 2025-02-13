package ir.greendex.mafia.ui.learn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.R
import ir.greendex.mafia.entity.learn.LearnEntity
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.ROUTER
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearnVm @Inject constructor(private val localRepository: LocalRepository) : ViewModel() {

    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }

    val getCharacters = MutableLiveData<List<LearnEntity>>()
    fun addCharacters() {
        val characters = mutableListOf<LearnEntity>()
        characters.add(
            LearnEntity(
                name = "پدرخوانده",
                isMafia = true,
                img = R.drawable.godfather,
                content = "پدرخوانده رئیس مافیاست ، تصمیم گیری برای شلیک کردن در شب به تصمیم پدرخوانده بازی بستگی داره ، پدرخوانده هر شب توسط هم تیمی خود یعنی ناتو بیدار میشن و برای شهر تصمیم گیری میکنن. \n استعلام پدر خوانده برای کاراگاه همیشه نفی خواهد بود ."
            )
        )
        characters.add(
            LearnEntity(
                name = "ناتو",
                isMafia = true,
                img = R.drawable.nato,
                content = "ناتو یکی از همدستان پدرخوانده بوده که در شب باهم بیدار شده و برای شهر تصمیم گیری میکنن ، پدرخوانده تصمیم میگیرد که این بازیکن از توانایی خود استفاده کند یا خیر ، ناتو توانایی حدس نقش داشته و اگر از بازیکنان شهر نقشش را درست حدس بزند به اصطلاح سلاخی شده و حتی دکتر بازی هم نمیتواند شخص را نجات دهد.\nناتو فقط یکبار میتواند از توانایی خود استفاده کند.\nدرصورتی که پدرخوانده در بازی نباشد اسلحه مافیا دست ناتو افتاده و در شب تصمیم میگیرد که به چه کسی شلیک کند ."
            )
        )
        characters.add(
            LearnEntity(
                name = "گروگانگیر",
                isMafia = true,
                img = R.drawable.hostage_taker,
                content = "گروگانگیر جزوی از تیم مافیا بوده ولی در شب همراه با پدرخوانده و ناتو چشم باز نمیکند ، گروگانگیر و بقیه اعضای مافیا یکدیگر را میشناسند ولی باهم چشم باز نمیکنند ، گروگانگیر زودتر از همه در شب بیدار شده و شروع به گرو گرفتن بازیکنان شهر میکند درصورتی که فرد گروگان گرفته شده دارای توانایی باشد در آن شب نمیتواند از توانایی خود استفاده کند.\nدرصورتی که پدرخوانده و ناتو از بازی خارج شده باشند اسلحه دست گروگانگیر میوفتد و در زمان تیر اندازی بجای پدرخوانده شلیک خواهد کرد."
            )
        )
        characters.add(
            LearnEntity(
                name = "نگهبان",
                isMafia = false,
                img = R.drawable.guard,
                content = "نگهبان نقش مخالف گروگانگیر را در شب داشته و شروع به نگهبانی از هم شهری های خودش را میدهد و در شب زودتر از تمامی بازیکنان بیدار می شود . \nنگهبان با رای روز بیروز نمیرود ، در صورتی که بازیکن دارای نقش نگهبان به رای گیری رفته و رای خروج را کسب کند ، به عنوان نگهبان شهر به بازیکنان اعلام شده و از آن به بعد به عنوان شهروند ساده بر سر جای خود مینشیند."
            )
        )
        characters.add(
            LearnEntity(
                name = "کاراگاه",
                isMafia = false,
                img = R.drawable.detective,
                content = "کاراگاه وظیفه استعلام تیم مافیا را داشته و هر شب استعلام یک نفر را میگیرد ، استعلام پدرخوانده همیشه برای کاراگاه منفی خواهد بود."
            )
        )
        characters.add(
            LearnEntity(
                name = "دکتر",
                isMafia = false,
                img = R.drawable.doctor,
                content = "دکتر شهر هر شب از یک نفر را پرستاری میکند ، اگر فردی که دکتر از آن پرستاری میکند توسط تیر مافیا مورد هدف قرار گرفته باشد دیگر کشته نخواهد شد."
            )
        )
        characters.add(
            LearnEntity(
                name = "تفنگدار",
                isMafia = false,
                img = R.drawable.rifileman,
                content = "تفنگدار بازی فردی ایست که در دستان خود تیرهای مشقی بینهایت داشته و فقط یک تیر جنگی دارد و هر شب به بازیکنان بر اساس انتخاب خودش تیر مشقی یا جنگی میدهد. \nفردی که تیر جنگی یا مشقی دارد در روز همان شب میتواند از آن تیر استفاده کرده و شلیک کند ، اگر تیر مشقی باشد هیچ اتفاقی نمی افتد ، درصورتی که تیر جنگی باشد فرد چه در ساید مافیا و چه در ساید شهروند بیرون رفته و فقط ساید فرد خوانده می شود.\n در صورتی که تفنگدار از تیر جنگی استفاده کرده باشد دیگر نمیتواند در شب های بعدی چه از تیر مشقی و چه از تیر جنگی استفاده کند و در زمان شب کلا بیدار نخواهد شد \n تفنگدار نمیتواند به خودش تیر جنگی بدهد"
            )
        )
        characters.add(
            LearnEntity(
                name = "تکاور",
                isMafia = false,
                img = R.drawable.commando,
                content = "تکاور فقط در صورتی در شب بیدار می شود که از سمت مافیا مورد هدف قرار گرفته باشد ، در شب بیدار شده و میتواند یک نفر را هذف قرار دهد.\nاگر تکاور مافیا را مورد هذف قرار دهد در روز بعد زنده مانده و مافیا به عنوان کشته شب بیرون میرود در غیر این صورت حتی با سیو دکتر هم روز بعد به عنوان کشته بیرون خواهد رفت"
            )
        )
        characters.add(
            LearnEntity(
                name = "شهروند ساده",
                isMafia = false,
                img = R.drawable.citizen,
                content = "شهروند ساده دارای نقش نمی باشد."
            )
        )

        getCharacters.postValue(characters)
    }
}