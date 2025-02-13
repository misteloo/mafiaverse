package ir.greendex.mafia.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.repository.local.GetLocalRepositoryEnum
import ir.greendex.mafia.util.SIGN_UP_TIMER
import ir.greendex.mafia.util.USER_PHONE
import ir.greendex.mafia.util.USER_TOKEN
import ir.greendex.mafia.util.base.BaseVm
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashVm @Inject constructor() : BaseVm() {
    fun getUserToken(callback: (callback: String) -> Unit) = viewModelScope.launch {
        if (localRepository.contain(USER_TOKEN)) {
            callback(
                localRepository.getStoreData(
                    key = USER_TOKEN,
                    type = GetLocalRepositoryEnum.String
                ) as String
            )
        } else {
            callback("not_found")
        }
    }

    fun getUserPhone(phone: (String) -> Unit) = viewModelScope.launch {
        phone(localRepository.getStoreData(USER_PHONE, GetLocalRepositoryEnum.String) as String)
    }

    val getSignUpTimerLiveData = MutableLiveData<Long>()
    fun getSignUpTimer() = viewModelScope.launch {

        if (localRepository.contain(SIGN_UP_TIMER)) {
            getSignUpTimerLiveData.postValue(
                localRepository.getStoreData(
                    SIGN_UP_TIMER,
                    GetLocalRepositoryEnum.Long
                ) as Long
            )
        } else getSignUpTimerLiveData.postValue(-1L)
    }

}