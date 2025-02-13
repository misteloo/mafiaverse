package ir.greendex.mafia.ui.confirm_code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.sing_up.ConfirmCodeEntity
import ir.greendex.mafia.repository.local.GetLocalRepositoryEnum
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.PRE_STEP_CONFIRM_CODE
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.SIGN_UP_TIMER
import ir.greendex.mafia.util.USER_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmCodeVm @Inject constructor(
    private val serverRepository: ServerRepository,
    private val localRepository: LocalRepository
) :
    ViewModel() {

    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }

    fun loginConfirmCode(phone: String, code: String, response: (ConfirmCodeEntity?) -> Unit) =
        viewModelScope.launch {
            val obj = JsonObject().apply {
                addProperty("phone", phone)
                addProperty("code", code)
            }
            serverRepository.loginConfirmCode(obj = obj).collect {
                response(it)
                if (it != null) if (it.status) localRepository.storeData(USER_TOKEN, it.data.token)
            }
        }

    fun confirmCode(phone: String, code: String,introducer:String?, response: (ConfirmCodeEntity?) -> Unit) =
        viewModelScope.launch {
            val obj = JsonObject().apply {
                addProperty("phone", phone)
                addProperty("code", code)
                addProperty("introducer",introducer)
            }
            serverRepository.signUpConfirmCode(obj = obj).collect {
                response(it)
                if (it != null) if (it.status) localRepository.storeData(USER_TOKEN, it.data.token)
            }
        }

    fun getTimer(timer: (Long) -> Unit) = viewModelScope.launch {

        if (localRepository.contain(SIGN_UP_TIMER)) {
            timer(localRepository.getStoreData(SIGN_UP_TIMER, GetLocalRepositoryEnum.Long) as Long)
        } else {
            timer(-1L)
        }
    }

    fun whichPlaceShouldNavigate(it: (login: (Boolean), signUp: (Boolean)) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            when (localRepository.getStoreData(
                PRE_STEP_CONFIRM_CODE,
                GetLocalRepositoryEnum.String
            ) as String) {
                "login" -> it(true, false)
                "signup" -> it(false, true)
            }

        }


    fun removeSignUpTimer() = viewModelScope.launch{
        localRepository.remove(SIGN_UP_TIMER)
    }
}