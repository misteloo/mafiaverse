package ir.greendex.mafia.game.vm.general

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.game.general.CharactersEntity
import ir.greendex.mafia.entity.game.general.RandomCharacterEntity
import ir.greendex.mafia.entity.game.general.UserQueueToPickEntity
import ir.greendex.mafia.entity.game.general.enum_cls.ScenariosEnum
import ir.greendex.mafia.game.general.listener.SelectCharacterListener
import ir.greendex.mafia.repository.local.GetLocalRepositoryEnum
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.Encryption
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.SELECTED_SCENARIO
import ir.greendex.mafia.util.base.BaseVm
import ir.greendex.mafia.util.socket.FindMatchSocketManager
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SelectCharacterVm @Inject constructor(
    private val gson: Gson,
    private val encryption: Encryption
) : BaseVm() {
    private lateinit var findMatchSocketManager: FindMatchSocketManager
    private lateinit var selectCharacterListener: SelectCharacterListener

    fun storeRouting(route: Routing) = viewModelScope.launch{
        localRepository.storeData(ROUTER,route.name)
    }
    fun setSocket(
        findMatchSocketManager: FindMatchSocketManager,
        selectCharacterListener: SelectCharacterListener
    ) {
        this.findMatchSocketManager = findMatchSocketManager
        this.selectCharacterListener = selectCharacterListener
    }

    fun getMatchScenario(callback: (it: ScenariosEnum) -> Unit) = viewModelScope.launch {
        val scenario = when (localRepository.getStoreData(
            SELECTED_SCENARIO,
            GetLocalRepositoryEnum.String
        ) as String) {
            "nato" -> ScenariosEnum.NATO
            else -> ScenariosEnum.NATO
        }
        callback(scenario)
    }

    fun getCharacters(callback: (character: MutableList<CharactersEntity.CharacterData>) -> Unit) {
        findMatchSocketManager.getCharacters {
            val data = gson.fromJson(it, CharactersEntity::class.java)
            val enc = encryption.encryptDecrypt(data.encryptedData)
            val jsnArr = JSONArray(enc)
            val list = mutableListOf<CharactersEntity.CharacterData>()
            for (i in 0 until jsnArr.length()) {
                val parse = JSONObject(jsnArr[i].toString())
                list.add(
                    CharactersEntity.CharacterData(
                        name = parse.getString("name"),
                        selected = parse.getBoolean("selected"),
                        selectedBy = parse.getString("selected_by"),
                        id = parse.getInt("id")
                    )
                )
            }
            callback(list)
        }
    }

    fun yourTurnToPick() {
        findMatchSocketManager.yourTurnToPick {
            selectCharacterListener.onYourTurnToPick()
        }
    }

    fun abandonSelection(){
        findMatchSocketManager.abandon {
            selectCharacterListener.onAbandon()
        }
    }

    fun readyToChooseCharacter() {
        findMatchSocketManager.readyToChooseCharacter()
    }

    fun randomCharacter() {
        findMatchSocketManager.randomCharacter {
            val result = gson.fromJson(it, RandomCharacterEntity::class.java)
            selectCharacterListener.onRandomCharacter(result.data.name)
        }
    }

    fun offListeners() {
        findMatchSocketManager.removeBasicListener()
    }

    fun selectCharacter(index: Int) {
        val indexObj = JSONObject().apply {
            put("index", index)
        }
        val obj = JSONObject().apply {
            put("op", "selected_character")
            put("data", indexObj)
        }
        findMatchSocketManager.selectCharacter(obj)
    }

    fun usersTurnToPick() {
        findMatchSocketManager.userTurnToPick {
            val result = gson.fromJson(it, UserQueueToPickEntity::class.java)
            selectCharacterListener.onUsersTurnToPick(it = result.data)
        }
    }
}
