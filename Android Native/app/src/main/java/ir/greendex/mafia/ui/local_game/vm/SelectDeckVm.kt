package ir.greendex.mafia.ui.local_game.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.util.ROUTER
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectDeckVm @Inject constructor(
    private val localRepository: LocalRepository
) : ViewModel() {
    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }
}