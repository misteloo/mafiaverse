package ir.greendex.mafia.ui.edit_profile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.SimpleResponse
import ir.greendex.mafia.repository.server.ServerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditUsernameBsVm @Inject constructor(private val serverRepository: ServerRepository) :
    ViewModel() {

    fun checkUsername(newName: String, callback: (SimpleResponse?) -> Unit) =
        viewModelScope.launch {
            val obj = JsonObject().apply {
                addProperty("new_name", newName)
            }

            serverRepository.checkUsername(obj).collect {
                callback(it)
            }
        }

    fun changeUsername(newName:String,token:String , callback: (SimpleResponse?) -> Unit) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("new_name", newName)
            addProperty("token",token)
        }
        serverRepository.changeUsername(obj).collect {
            callback(it)
        }
    }
}