package ir.greendex.mafia.ui.transaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.transaction.TransactionEntity
import ir.greendex.mafia.repository.server.ServerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionVm @Inject constructor(private val serverRepository: ServerRepository) :
    ViewModel() {
    val transactionHistory = MutableLiveData<List<TransactionEntity.TransactionData>>()

    fun getTransactionHistory(token: String) = viewModelScope.launch(Dispatchers.IO) {
        val body = JsonObject().apply {
            addProperty("token", token)
        }
        serverRepository.userTransactionHistory(body).collect {
            if (it != null){
                val sort = it.data.sortedByDescending {soring->
                    soring.date
                }

                transactionHistory.postValue(sort)
            }
        }
    }
}