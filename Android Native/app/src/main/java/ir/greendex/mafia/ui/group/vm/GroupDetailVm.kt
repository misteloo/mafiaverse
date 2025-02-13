package ir.greendex.mafia.ui.group.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.repository.server.ServerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailVm @Inject constructor(private val serverRepository: ServerRepository) :
    ViewModel() {
    val channelDetailLiveData = MutableLiveData<String>()
    fun getDetails(channelId: String) = viewModelScope.launch{
        val obj = JsonObject().apply {
            addProperty("channel_id",channelId)
        }

        serverRepository.getChannelDetail(body = obj).collect {
            Log.i("LOG", "getDetails: $it")
        }

    }
}