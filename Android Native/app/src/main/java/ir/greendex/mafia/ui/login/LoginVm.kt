package ir.greendex.mafia.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.SimpleResponse
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
class LoginVm @Inject constructor(
    private val localRepository: LocalRepository,
    private val serverRepository: ServerRepository,
) : ViewModel() {

    fun setState() = viewModelScope.launch(Dispatchers.IO) {
        localRepository.storeData(PRE_STEP_CONFIRM_CODE, "login")
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

    fun storeLoginTimer(timer: Long) = viewModelScope.launch {
        localRepository.storeData(SIGN_UP_TIMER, timer)
    }

    fun storeUserPhone(phone: String) = viewModelScope.launch {
        localRepository.storeData(USER_PHONE, phone)
    }

    private var login: ((SimpleResponse?) -> Unit)? = null
    fun loginCallback(it: (SimpleResponse?) -> Unit) {
        login = it
    }

    fun loginRequest(phone: String) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("phone", phone)
            addProperty("firebase_token", getFirebaseToken())
        }
        serverRepository.login(obj = obj).collect { response ->
            login?.let {
                it(response)
            }
        }
    }

}