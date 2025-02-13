package ir.greendex.mafia.ui.edit_profile.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.SimpleResponse
import ir.greendex.mafia.entity.edit_profile.UserItemsEntity
import ir.greendex.mafia.repository.server.ServerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAnimBsVm @Inject constructor(private val serverRepository: ServerRepository):ViewModel() {
    val getUserItemLiveData = MutableLiveData<List<UserItemsEntity.UserItemData.UserItemsList>?>()
    fun getUserItems(type: String, token: String) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("token", token)
        }

        serverRepository.userItems(obj).collect {
            if (it == null) getUserItemLiveData.postValue(null)
            else {
                val filterItem = it.data.items.filter { filter ->
                    filter.type == type
                }
                getUserItemLiveData.postValue(filterItem)
            }
        }
    }

    fun saveChange(id: String,token:String, response: (SimpleResponse?) -> Unit) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("token",token)
            addProperty("section","table")
            addProperty("item_id",id)
        }

        Log.i("LOG", "saveChange: $obj")

        serverRepository.editProfileItem(obj).collect {
            Log.i("LOG", "saveChange: $it")
            response(it)
        }
    }
}