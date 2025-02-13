package ir.greendex.mafia.game.vm.general

import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.game.general.FindMatchUserEntity
import ir.greendex.mafia.entity.game.general.GameFoundEntity
import ir.greendex.mafia.util.base.BaseVm
import ir.greendex.mafia.util.socket.FindMatchSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class FindMatchBsVm @Inject constructor(
    private val gson: Gson,
    private val notification: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat,
) : BaseVm() {
    private lateinit var findMatchSocketManager: FindMatchSocketManager

    @SuppressLint("MissingPermission")
    fun createNotification(title: String, msg: String) {
        val builder = notification.setContentTitle(title).setContentText(msg)
        notificationManager.notify(1, builder.build())
    }

    fun setSocket(findMatchSocketManager: FindMatchSocketManager) {
        this.findMatchSocketManager = findMatchSocketManager
    }

    fun findMatch(gameType: String,auth:Boolean) {
        val obj = JSONObject().apply {
            put("game_type", gameType)
            put("auth",auth)
        }
        findMatchSocketManager.findMatch(obj)
    }


    fun normalRobotUserJoinedFindMatch(callback: (List<FindMatchUserEntity.Data>) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            findMatchSocketManager.normalRobotUserJoinedFindMatch {
                val response = gson.fromJson(it, FindMatchUserEntity::class.java)
                callback(response.data)
            }
        }

    fun gameFound(callback: (isCreator: GameFoundEntity.GameFoundData) -> Unit) {
        findMatchSocketManager.gameFound {
            val res = gson.fromJson(it, GameFoundEntity::class.java)
            callback(res.data)
        }
    }

    fun leaveFindMatch() {
        findMatchSocketManager.leaveFindMatch()
    }
}