package ir.greendex.mafia.ui.group.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.SimpleResponse
import ir.greendex.mafia.entity.channel.MyChannelsEntity
import ir.greendex.mafia.entity.channel.SearchChannelEntity
import ir.greendex.mafia.repository.local.GetLocalRepositoryEnum
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.util.USER_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupListVm @Inject constructor(
    private val localRepository: LocalRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    suspend fun getUserToken() = withContext(Dispatchers.IO) {
        return@withContext async {
            localRepository.getStoreData(
                USER_TOKEN,
                GetLocalRepositoryEnum.String
            ) as String
        }
    }.await()

    fun createChannel(name: String, channelDesc: String,callback:(SimpleResponse?)->Unit) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("channel_name", name)
            addProperty("channel_desc", channelDesc)
            addProperty("token", getUserToken())
        }
        serverRepository.createChannel(body = obj).collect {
            callback(it)
        }
    }

    val getMyGroupListLiveData = MutableLiveData<MyChannelsEntity?>()
    fun getMyGroupList() = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("token", getUserToken())
        }
        serverRepository.getMyChannels(body = obj).collect {
            Log.i("LOG", "getMyGroupList: $it")
            getMyGroupListLiveData.postValue(it)
        }
    }

    fun getSpecificChannel(id: String) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("token", getUserToken())
            addProperty("channel_id", id)
        }
        serverRepository.getSpecificChannel(body = obj).collect {
            Log.i("LOG", "getSpecificChannel: $it")
        }
    }

    fun searchChannel(
        name: String,
        callback: (List<SearchChannelEntity.SearchChannelData>) -> Unit
    ) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("channel_name", name)
            addProperty("token",getUserToken())
        }
        serverRepository.searchChannel(body = obj).collect {
            if (it != null) callback(it.data)
        }
    }

    fun joinToChannel(channelId: String,response:(SimpleResponse?)->Unit) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("channel_id", channelId)
            addProperty("token", getUserToken())
        }
        serverRepository.joinToChannel(body = obj).collect {
            response(it)
        }
    }
}