package ir.greendex.mafia.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.entity.game.general.enum_cls.ScenariosEnum
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.repository.local.GetLocalRepositoryEnum
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.util.HOME_BODY_ANIM
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.SELECTED_SCENARIO
import ir.greendex.mafia.util.base.BaseVm
import ir.greendex.mafia.util.socket.SocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeVm @Inject constructor(
    private val gson: Gson,
    private val serverRepository: ServerRepository
) : BaseVm() {

    val getHomeBodyAnimLiveData = MutableLiveData<String>()
    fun getHomeBodyAnim() = viewModelScope.launch(Dispatchers.IO) {
        // home full body anim
        val result =
            localRepository.getStoreData(HOME_BODY_ANIM, GetLocalRepositoryEnum.String) as String

        getHomeBodyAnimLiveData.postValue(result)
    }

    fun userHasEnoughGoldToFindMatch(status: (Boolean) -> Unit) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("token", MainActivity.userToken)
        }
        serverRepository.findMatchGold(body = obj).collect {
            it?.status?.let { res ->
                status(res)
            }
        }
    }

    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }

    fun setFindingMatchScenario(scenariosEnum: ScenariosEnum) = viewModelScope.launch {
        localRepository.storeData(SELECTED_SCENARIO, scenariosEnum.name)
    }


}