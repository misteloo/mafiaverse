package ir.greendex.mafia.ui.local_game.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.local.LocalBase64Entity
import ir.greendex.mafia.entity.local.LocalCharacterEntity
import ir.greendex.mafia.entity.local.LocalErrorEntity
import ir.greendex.mafia.entity.local.LocalPrvDeckEntity
import ir.greendex.mafia.entity.local.LocalSelectDeckEntity
import ir.greendex.mafia.entity.local.LocalUserCharacterEntity
import ir.greendex.mafia.entity.local.LocalUsersJoinedEntity
import ir.greendex.mafia.entity.local.LocalUsersJoiningEntity
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.ui.local_game.listeners.LocalGameListener
import ir.greendex.mafia.ui.local_game.listeners.LocalGameVmListener
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.socket.LocalGameSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LocalGameVm @Inject constructor(
    private val gson: Gson,
    private val localRepository: LocalRepository
) : ViewModel() {
    private lateinit var callback: LocalGameVmListener
    private lateinit var localGameSocketManager: LocalGameSocketManager
    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }

    fun setLocalGameSocketManager(localGameSocketManager: LocalGameSocketManager) {
        this.localGameSocketManager = localGameSocketManager
    }

    fun initLocalSocket(listener: LocalGameVmListener) {
        this.callback = listener
        localGameSocketManager.init(object : LocalGameListener {
            override fun onGetDeck(it: String) {
                val res = gson.fromJson(it, LocalCharacterEntity::class.java)
                callback.onGetDeck(data = res)
            }

            override fun onBase64(it: String) {
                val res = gson.fromJson(it, LocalBase64Entity::class.java)
                callback.onBase64(data = res.data)
            }

            override fun onError(it: String) {
                val res = gson.fromJson(it, LocalErrorEntity::class.java)
                callback.onError(res.data)
            }

            override fun onCheckPrvDeck(it: String) {
                val res = gson.fromJson(it, LocalPrvDeckEntity::class.java)
                callback.onCheckPrvDeck(data = res.data.deckList)
            }

            override fun onUsersJoining(it: String) {
                val res = gson.fromJson(it, LocalUsersJoiningEntity::class.java)
                callback.onUsersJoining(res.data)
            }

            override fun onUsersJoined(it: String) {
                val res = gson.fromJson(it, LocalUsersJoinedEntity::class.java)
                callback.onUsersJoined(data = res.data.users)
            }

            override fun onUserCharacter(it: String) {
                val res = gson.fromJson(it,LocalUserCharacterEntity::class.java)
                callback.onUserCharacter(data = res.data)
            }

        })
    }

    fun createLocalGame(playerCount: String) {
        val obj = JSONObject().apply {
            put("player_count", playerCount)
        }
        localGameSocketManager.emit(data = obj, event = "create_local_game")
    }

    fun getDeck() {
        val op = JSONObject().apply {
            put("op", "get_deck")
        }
        localGameSocketManager.emit(op)
    }

    fun setDeck(list: ArrayList<LocalSelectDeckEntity>) = viewModelScope.launch(Dispatchers.IO) {
        Log.i("LOG", "setDeck called: ")
        val data = JSONArray()
        val job = CoroutineScope(Dispatchers.IO).launch {
            list.forEach { i ->
                val obj = JSONObject().apply {
                    put("id", i.id)
                }
                data.put(obj)
            }
        }
        job.join()
        val op = JSONObject().apply {
            put("op", "set_deck")
            put("data", data)
        }
        localGameSocketManager.emit(op)

    }

    fun joinLocalGame(gameId: String, name: String) {
        val data = JSONObject().apply {
            put("game_id", gameId)
            put("name", name)
        }
        val op = JSONObject().apply {
            put("op", "join_local_game")
            put("data", data)
        }
        Log.i("LOG", "joinLocalGame: $op")
        localGameSocketManager.emit(op)
    }

    fun startPick() {
        val op = JSONObject().apply {
            put("op", "pick_cart")
        }
        localGameSocketManager.emit(op)
    }

    fun turnOffSocket() {
        localGameSocketManager.turnOff()
    }

    fun leaveLocalGame() {
        val obj = JSONObject().apply {
            put("op", "leave_local_game")
        }

        localGameSocketManager.emit(obj)
    }
}