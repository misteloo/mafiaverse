package ir.greendex.mafia.ui.game_history.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.game_history.TotalGameHistoryEntity
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.util.ROUTER
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TotalGameVm @Inject constructor(
    private val serverRepository: ServerRepository,
    private val localRepository: LocalRepository
) :
    ViewModel() {

    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }

    val getTotalGameHistoryLiveData = MutableLiveData<List<TotalGameHistoryEntity.GameTotalHistoryListEntity>?>()
    fun getTotalGameHistory(token:String) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("token",token)
        }

        serverRepository.totalGames(obj).collect {
            getTotalGameHistoryLiveData.postValue(it?.data)
        }

    }
}