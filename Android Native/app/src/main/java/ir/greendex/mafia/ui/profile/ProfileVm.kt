package ir.greendex.mafia.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.entity.profile.ProfileEntity
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.HOME_BODY_ANIM
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.socket.SocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileVm @Inject constructor(
    private val serverRepository: ServerRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }

    fun storeAnim(name:String) = viewModelScope.launch {
        localRepository.storeData(HOME_BODY_ANIM , name)
    }


    val profileLiveData = MutableLiveData<ProfileEntity?>()
    fun getMyProfile() = viewModelScope.launch {
        val body = JsonObject().apply {
            addProperty("token", MainActivity.userToken)
        }
        serverRepository.getMyProfile(body).collect {
            profileLiveData.postValue(it)
        }
    }


    fun removeItemFromLocal(keys: List<String>) = viewModelScope.launch {
        keys.forEach {
            localRepository.remove(it)
        }
    }

    fun dcSocket() {
        SocketManager.disconnectSocket()
    }

    fun clearSocketConnections() = viewModelScope.launch(Dispatchers.IO) {
        // clear socket connections
        SocketManager.clearNatoGameSocket()
        SocketManager.clearLocalGameSocketArray()
        SocketManager.clearChannelSocketArray()
        SocketManager.clearEndGameResultSocketArray()
        SocketManager.clearChannelGameSocketArray()
    }

}