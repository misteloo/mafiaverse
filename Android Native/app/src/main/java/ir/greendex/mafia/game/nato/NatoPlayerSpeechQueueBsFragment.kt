package ir.greendex.mafia.game.nato

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.FragmentNatoPlayerSpeechQueueBsBinding
import ir.greendex.mafia.entity.game.general.InGameTurnSpeechEntity
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.game.adapter.general.NatoUsersSpeechQueueAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NatoPlayerSpeechQueueBsFragment(
    private val details: MutableList<InGameTurnSpeechEntity.InGameTurnSpeechQueueData>,
    private val users: MutableList<InGameUsersDataEntity.InGameUserData>
) : BottomSheetDialogFragment() {
    private var _binding: FragmentNatoPlayerSpeechQueueBsBinding? = null
    private val binding get() = _binding

    // injection
    @Inject
    lateinit var adapter: NatoUsersSpeechQueueAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNatoPlayerSpeechQueueBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED

        // initViews
        initViews()


    }

    private fun initViews() {
        binding?.apply {
            // rv
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rv.adapter = adapter

            // filter list
            lifecycleScope.launch {
                filterList().collect {
                    adapter.addItem(it as MutableList<InGameTurnSpeechEntity.InGameTurnSpeechQueueData>,users)
                }
            }
        }
    }

    private fun filterList(): Flow<List<InGameTurnSpeechEntity.InGameTurnSpeechQueueData>> {
        return flow {
            val list = mutableListOf<InGameTurnSpeechEntity.InGameTurnSpeechQueueData>()
            details.forEach { data ->
                if (!data.pass) list.add(
                    InGameTurnSpeechEntity.InGameTurnSpeechQueueData(
                        userId = data.userId,
                        userIndex = data.userIndex,
                        speechStatus = data.speechStatus,
                        challengeUsed = data.challengeUsed,
                        pass = false
                    )
                )
            }
            emit(list)
        }.flowOn(Dispatchers.IO)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}