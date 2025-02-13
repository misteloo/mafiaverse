package ir.greendex.mafia.ui.lucky_wheel

import android.content.Context
import android.media.AudioManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.lucky_wheel.LuckyWheelStatusEntity
import ir.greendex.mafia.entity.lucky_wheel.SpinLuckyWheelEntity
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.sound.SoundManager
import ir.greendex.mafia.util.vibrate.Vibrate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LuckyVm @Inject constructor(
    private val localRepository: LocalRepository,
    private val serverRepository: ServerRepository,
    private val audioManager: AudioManager,
    private val vibrate: Vibrate,
    private val soundManager: SoundManager
) : ViewModel() {

    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }


    val getLuckyWheelStatusLiveData =
        MutableLiveData<LuckyWheelStatusEntity.LuckyWheelStatusData?>()

    fun getLuckyWheelStatus(token: String) = viewModelScope.launch(Dispatchers.IO) {
        val obj = JsonObject().apply {
            addProperty("token", token)
        }
        serverRepository.getLuckyWheelStatus(body = obj).collect {
            getLuckyWheelStatusLiveData.postValue(it?.data)
        }
    }


    fun spinLuckyWheel(
        token: String,
        callback: (SpinLuckyWheelEntity.SpinLuckyWheelData?) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val obj = JsonObject().apply {
            addProperty("token", token)
        }
        serverRepository.spinLuckyWheel(body = obj).collect {
            callback(it?.data)
        }
    }

    fun configureRotation(percent: Int): Int {
        return when (percent) {
            in (99..100) -> 240 // 100
            in 90..98 -> 60 // 50
            in 80..89 -> 120 // 40
            in 60..79 -> 300 // 30
            in 40..59 -> 180 // 20
            else -> 360 // 10
        }
    }


    fun goldCount(percent: Int):Int{
        return when (percent) {
            in (99..100) -> 100
            in 90..98 -> 50
            in 80..89 -> 40
            in 60..79 -> 30
            in 40..59 -> 20
            else -> 10
        }
    }

    suspend fun exportLeftTime(futureTime: Long): Deferred<List<Int>> =
        withContext(Dispatchers.IO) {
            val res = async {
                val time = ((futureTime - System.currentTimeMillis()) / 1000L).toFloat()
                val day = export(time / 86400f)
                val decimalDay = exportDecimal(time / 86400f)
                val hour = export(decimalDay * 24f)
                val decimalHour = exportDecimal(decimalDay * 24)
                val min = export(decimalHour * 60)
                val decimalMin = exportDecimal(decimalHour * 60)
                val sec = export(decimalMin * 60)
                listOf(day, hour, min, sec)
            }
            return@withContext res
        }

    private fun exportDecimal(num: Float): Float {
        return "0".plus(num.toString().substring(index(num), num.toString().length)).toFloat()
    }

    private fun export(num: Float): Int {
        return num.toString().substring(0, index(num)).toInt()
    }

    private fun index(num: Float): Int {
        return num.toString().indexOf('.')
    }

    fun playSound() {
        soundManager.coinSound()
    }
}