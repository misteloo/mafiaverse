package ir.greendex.mafia.util.base

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.repository.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
open class BaseVm @Inject constructor() :
    ViewModel() {
    companion object {
        const val TAG = "LOG"
    }

    @Inject
    lateinit var localRepository: LocalRepository

}