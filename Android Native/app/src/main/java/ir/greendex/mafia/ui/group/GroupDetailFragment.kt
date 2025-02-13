package ir.greendex.mafia.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentGroupDetailBinding
import ir.greendex.mafia.entity.group.GroupDetailEntity
import ir.greendex.mafia.ui.group.adapter.GroupDetailAdapter
import ir.greendex.mafia.ui.group.vm.GroupDetailVm
import ir.greendex.mafia.util.base.BaseFragment
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GroupDetailFragment : BaseFragment() {
    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding
    private val vm: GroupDetailVm by viewModels()
    private val args: GroupDetailFragmentArgs by navArgs()

    // injection
    @Inject
    lateinit var adapter: GroupDetailAdapter

    private lateinit var channelId: Deferred<String>

    override

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            channelId = async { args.channelId }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupDetailBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initViews
        initViews()

        // get channel detail
        getChannelDetail()
    }


    private fun initViews() {
        binding?.apply {

            // back
            imgBack.setOnClickListener {
                findNavController().navigate(R.id.action_groupDetailFragment_pop_including)
            }
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rv.adapter = adapter


            val item = mutableListOf<GroupDetailEntity.GroupDetailUsersData>().apply {
                add(GroupDetailEntity.GroupDetailUsersData("mmd", "", "normal"))
                add(GroupDetailEntity.GroupDetailUsersData("farbod", "", "leader"))
                add(GroupDetailEntity.GroupDetailUsersData("peyman", "", "co"))
                add(GroupDetailEntity.GroupDetailUsersData("mehdi", "", "co"))
                add(GroupDetailEntity.GroupDetailUsersData("amin", "", "normal"))
                add(GroupDetailEntity.GroupDetailUsersData("armin", "", "normal"))
                add(GroupDetailEntity.GroupDetailUsersData("ramin", "", "normal"))
                add(GroupDetailEntity.GroupDetailUsersData("os karim", "", "co"))
                add(GroupDetailEntity.GroupDetailUsersData("os karim", "", "co"))
                add(GroupDetailEntity.GroupDetailUsersData("os karim", "", "co"))
                add(GroupDetailEntity.GroupDetailUsersData("os karim", "", "co"))
            }
            adapter.getMaster(true)
            adapter.addItem(item)
        }
    }


    private fun getChannelDetail() = lifecycleScope.launch {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}