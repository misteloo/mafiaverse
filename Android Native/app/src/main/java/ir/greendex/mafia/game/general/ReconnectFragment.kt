package ir.greendex.mafia.game.general

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.FragmentReconnectBinding
import ir.greendex.mafia.entity.game.general.enum_cls.NatoGameEventEnum
import ir.greendex.mafia.game.nato.NatoFragment
import ir.greendex.mafia.game.nato.NatoFragmentDirections
import ir.greendex.mafia.game.vm.general.ReconnectVm
import ir.greendex.mafia.util.base.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReconnectFragment : BaseFragment() {
    private var _binding: FragmentReconnectBinding? = null
    private val binding get() = _binding
    private val vm: ReconnectVm by viewModels()
    private val args by navArgs<ReconnectFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReconnectBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            delay(500)
            vm.sendReconnect()
        }

        vm.onReceiveReconnectData { usersData, userAction, gameEvent, roomId, character, joinType, roles , gameId ->

            lifecycleScope.launch {
                usersData.asFlow().flowOn(Dispatchers.IO).onStart {
                    NatoFragment.inGameUsers.clear()
                }.onCompletion {
                    NatoFragment.inGameUsers.addAll(usersData)
                }.collect{}

                userAction.asFlow().flowOn(Dispatchers.IO).onStart {
                    NatoFragment.userActionHistory.clear()
                }.onCompletion {
                    NatoFragment.userActionHistory.addAll(userAction)
                }.collect{}
                /*// users data
                NatoFragment.inGameUsers.clear()
                NatoFragment.inGameUsers.addAll(usersData)
                // user action histories
                NatoFragment.userActionHistory.clear()
                NatoFragment.userActionHistory.addAll(userAction)*/
                // game event
                NatoFragment.currentGameEvent = gameEvent
                if (gameEvent == NatoGameEventEnum.NIGHT || gameEvent == NatoGameEventEnum.DAY || gameEvent == NatoGameEventEnum.CHAOS) {
                    NatoFragment.mainGameEvent = gameEvent
                }
                // player roles
                if (roles.isNotEmpty()) NatoFragment.playerRoles.addAll(roles)

                // navigation
                val action = NatoFragmentDirections.actionToGameNatoFragment(
                    character = if (character != "not_found") character else null,
                    userId = args.userId,
                    joinType = joinType,
                    roomId = roomId,
                    hasGameEvent = true,
                    usersData = usersData.toString(),
                    fromReconnect = true,
                    gameId = gameId
                )
                findNavController().navigate(action)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}