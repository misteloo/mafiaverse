package ir.greendex.mafia.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.SimpleResponse
import ir.greendex.mafia.entity.sing_up.SignUpEntity
import ir.greendex.mafia.repository.local.GetLocalRepositoryEnum
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.util.FIREBASE_TOKEN
import ir.greendex.mafia.util.PRE_STEP_CONFIRM_CODE
import ir.greendex.mafia.util.SIGN_UP_TIMER
import ir.greendex.mafia.util.USER_PHONE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpVm @Inject constructor(
    private val serverRepository: ServerRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    fun checkUsername(newName: String, callback: (SimpleResponse?) -> Unit) =
        viewModelScope.launch {
            val obj = JsonObject().apply {
                addProperty("new_name", newName)
            }

            serverRepository.checkUsername(obj).collect {
                callback(it)
            }
        }

    fun setState() = viewModelScope.launch(Dispatchers.IO){
        localRepository.storeData(PRE_STEP_CONFIRM_CODE,"signup")
    }

    private suspend fun getFirebaseToken(): String {
        return withContext(Dispatchers.IO) {
            async {
                localRepository.getStoreData(
                    FIREBASE_TOKEN,
                    GetLocalRepositoryEnum.String
                ) as String
            }
        }.await()
    }

    fun singUp(
        username: String,
        phone: String,
        identificationCode: String,
        response: (SignUpEntity?) -> Unit
    ) =
        viewModelScope.launch {
            val obj = JsonObject().apply {
                addProperty("phone", phone)
                addProperty("name", username)
                addProperty("identification_code",identificationCode)
                addProperty("firebase_token", getFirebaseToken())
            }
            serverRepository.signUp(obj = obj).collect {
                Log.i("LOG", "singUp: $it")
                response(it)
            }
        }

    fun storeSignUpTimer(timer: Long) = viewModelScope.launch {
        localRepository.storeData(SIGN_UP_TIMER, timer)
    }

    fun storeUserPhone(phone: String) = viewModelScope.launch {
        localRepository.storeData(USER_PHONE, phone)
    }


}