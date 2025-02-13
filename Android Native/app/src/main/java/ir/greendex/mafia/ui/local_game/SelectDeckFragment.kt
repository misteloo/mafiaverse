package ir.greendex.mafia.ui.local_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.FragmentSelectDeckBinding
import ir.greendex.mafia.entity.local.LocalCharacterEntity
import ir.greendex.mafia.entity.local.LocalSelectDeckEntity
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.ui.local_game.adapter.LocalSelectDeckAdapter
import ir.greendex.mafia.ui.local_game.vm.SelectDeckVm
import ir.greendex.mafia.util.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SelectDeckFragment : BaseFragment() {
    private var _binding: FragmentSelectDeckBinding? = null
    private val binding get() = _binding
    private val characterList by lazy { mutableListOf<LocalCharacterEntity.LocalCharacterDeck>() }
    private val rvItem by lazy { mutableListOf<LocalSelectDeckEntity>() }
    private val vm: SelectDeckVm by viewModels()
    private var playerCount = 0

    companion object {
        private val selectedItem by lazy { arrayListOf<LocalSelectDeckEntity>() }
        private fun clearDeck() {
            selectedItem.clear()
        }

        private var onSelectedDeck: ((ArrayList<LocalSelectDeckEntity>) -> Unit)? = null
        fun onSelectedDeckCallback(it: (ArrayList<LocalSelectDeckEntity>) -> Unit) {
            onSelectedDeck = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = arguments?.getParcelable<LocalCharacterEntity>("local_deck")
        arguments?.getInt("player_count")?.let {
            playerCount = it
        }
        data?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val job = CoroutineScope(Dispatchers.IO).launch {
                    it.data.forEach { item ->
                        characterList.add(item)
                    }
                }
                job.join()
                characterList.forEach {
                    rvItem.add(
                        LocalSelectDeckEntity(
                            id = it.id,
                            name = it.name,
                            icon = it.icon,
                            description = it.description,
                            side = it.side,
                            multi = it.multi,
                            selected = false
                        )
                    )
                }
            }
        }

        // store routing
        storeRouting()
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.LOCAL)
    }

    // injection
    @Inject
    lateinit var adapter: LocalSelectDeckAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectDeckBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init view
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            // cancel
            btnCancel.setOnClickListener {
                selectedItem.clear()

                findNavController().popBackStack()
            }

            txtPlayerCount.text = playerCount.toString()

            // rv
            rv.layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.SPACE_AROUND
            }
            rv.adapter = adapter
            adapter.modifierItem(newItem = rvItem)

            // more
            adapter.onMoreCallback {
                characterDescription(it)
            }


            // on select
            adapter.onSelectCallback { selectDeckEntity, checked ->
                if (checked) selectedItem.add(selectDeckEntity)
                else selectedItem.removeAll {
                    it.id == selectDeckEntity.id
                }
                txtCardCount.text = selectedItem.size.toString()

            }
            // increase decrease
            adapter.onIncDecCallback { selectDeckEntity, inc ->
                if (inc) selectedItem.add(selectDeckEntity)
                else selectedItem.remove(selectDeckEntity)
                txtCardCount.text = selectedItem.size.toString()
            }

            btnSetDeck.setOnClickListener {
                if (playerCount != selectedItem.size) {
                    msg("تعداد نقش با بازیکن یکسان نیست")
                    return@setOnClickListener
                }
                val array = arrayListOf<LocalSelectDeckEntity>()
                selectedItem.forEach {
                    array.add(it)
                }
                // callback
                onSelectedDeck?.let {
                    it(array)
                    clearDeck()
                }
                selectedItem.clear()
                findNavController().popBackStack()
            }
        }
    }

    private fun characterDescription(character:LocalSelectDeckEntity){
        val bs = LocalGameCharacterDescriptionFragment(character)
        bs.show(childFragmentManager,null)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}