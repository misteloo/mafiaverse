package ir.greendex.mafia.game.vm.general

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.Socket
import ir.greendex.mafia.entity.game.general.GameActionEntity
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.general.ReconnectEntity
import ir.greendex.mafia.entity.game.general.UsersCharacterEntity
import ir.greendex.mafia.entity.game.general.enum_cls.NatoGameEventEnum
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.socket.SocketManager
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ReconnectVm @Inject constructor(private val socket: Socket, private val gson: Gson) :
    ViewModel() {

    fun sendReconnect() {
        val op = JSONObject().apply {
            put("op", "reconnect")
        }
        socket.emit("game_handle", op)
    }

    fun onReceiveReconnectData(
        reconnectData: (
            inGameUsersData: List<InGameUsersDataEntity.InGameUserData>,
            userActionHistory: List<GameActionEntity.GameActionData>,
            gameEvent: NatoGameEventEnum,
            roomId: String,
            character: String?,
            joinType: String,
            roles: List<UsersCharacterEntity.UsersCharacterData>,
            gameId: String
        ) -> Unit
    ) {
        SocketManager.onReconnectReceivedData {
            Log.i(BaseFragment.TAG, "reconnect: $it")
            val parse = gson.fromJson(it, ReconnectEntity::class.java)
            val gameEvent: NatoGameEventEnum = when (parse.data.gameEvent) {
                "day" -> NatoGameEventEnum.DAY
                "action" -> NatoGameEventEnum.ACTION
                "vote" -> NatoGameEventEnum.VOTE
                "night" -> NatoGameEventEnum.NIGHT
                "chaos" -> NatoGameEventEnum.CHAOS
                "end" -> NatoGameEventEnum.END
                "target_cover" -> NatoGameEventEnum.TARGET_COVERT_ABOUT
                else -> NatoGameEventEnum.DAY
            }

            reconnectData(
                parse.data.usersData,
                parse.data.userHistory,
                gameEvent,
                parse.data.roomId,
                parse.data.character,
                parse.data.joinType,
                parse.data.roles,
                parse.data.gameId
            )
//            Log.i("LOG", "receiveReconnectData: $it")
//            reconnectData(it)
        }
    }
}