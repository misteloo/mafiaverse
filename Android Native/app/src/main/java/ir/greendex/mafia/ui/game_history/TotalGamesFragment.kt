package ir.greendex.mafia.ui.game_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.databinding.FragmentTotalGamesBinding
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.ui.game_history.adapter.TotalGameAdapter
import ir.greendex.mafia.ui.game_history.vm.TotalGameVm
import ir.greendex.mafia.util.base.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class TotalGamesFragment : BaseFragment() {
    private var _binding: FragmentTotalGamesBinding? = null
    private val binding get() = _binding
    private val vm: TotalGameVm by viewModels()

    // injection
    @Inject
    lateinit var adapter: TotalGameAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTotalGamesBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init
        initViews()

        // get history
        getHistory()

        // store routing
        storeRouting()
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.GAME_HISTORY)
    }

    private fun getHistory() {
        MainActivity.userToken?.let { token ->
            vm.getTotalGameHistory(token = token)
        }
    }

    private fun initViews() {
        binding?.apply {
            // back
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }

            // rv
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rv.adapter = adapter

            vm.getTotalGameHistoryLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    if (it.isEmpty()) {
                        shimmer.visibility = View.GONE
                        shimmer.stopShimmer()
                        layerEmptyBox.visibility = View.VISIBLE
                    } else {
                        shimmer.visibility = View.GONE
                        shimmer.stopShimmer()
                        it.reversed().apply {
                            adapter.modifierItem(newItem = this)
                            rv.visibility = View.VISIBLE
                        }
                    }
                } else msg("عدم ارتباط")
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}