package ir.greendex.mafia.ui.group

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentGroupListBinding
import ir.greendex.mafia.databinding.LayerCreateChannelBinding
import ir.greendex.mafia.entity.SimpleResponse
import ir.greendex.mafia.entity.channel.MyChannelsEntity
import ir.greendex.mafia.entity.channel.SearchChannelEntity
import ir.greendex.mafia.entity.group.GroupListEntity
import ir.greendex.mafia.ui.group.adapter.GroupListAdapter
import ir.greendex.mafia.ui.group.adapter.GroupSearchAdapter
import ir.greendex.mafia.ui.group.vm.GroupListVm
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading
import ir.greendex.mafia.util.extension.textChangeFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GroupListFragment : BaseFragment() {
    private var _binding: FragmentGroupListBinding? = null
    private val binding get() = _binding
    private val vm: GroupListVm by viewModels()
    private val navArgs: GroupListFragmentArgs by navArgs()
    private val searchList by lazy { mutableListOf<SearchChannelEntity.SearchChannelData>() }
    private val channelList by lazy { mutableListOf<MyChannelsEntity.MyChannelsData>() }
    private lateinit var createChannel: BottomSheetDialog
    private lateinit var token: String

    // injection
    @Inject
    lateinit var groupListAdapter: GroupListAdapter

    @Inject
    lateinit var searchAdapter: GroupSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupListBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get user token
        getToken()

        // initViews
        initViews()

        // search group
        searchGroup()

        // get my channels
        getMyChannels()

    }

    private fun getMyChannels() {
        vm.getMyGroupList()
    }

    private fun getToken() = lifecycleScope.launch {
        token = vm.getUserToken()
    }

    private fun initViews() {
        binding?.apply {
            // back
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }

            // create
            fabAddGroup.setOnClickListener {
                createChannel()
            }

            // rv
            rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rvSearch.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            // bind adapter to rv
            rvList.adapter = groupListAdapter
            rvSearch.adapter = searchAdapter
            groupListAdapter.setUserId(navArgs.userId)

            // request to join channel
            searchAdapter.onJoinRequestCallback { channelId, position ->
                lifecycleScope.launch {
                    // find channel
                    searchList.find {
                        it.channelId == channelId
                    }?.let {
                        it.loading = true
                        searchList[position] = it
                        searchAdapter.modifierItem(newItem = searchList)
                        rvSearch.adapter?.notifyItemChanged(searchList.indexOf(it))
                        // send request to server
                        vm.joinToChannel(
                            channelId = channelId
                        ) { callback ->
                            joinToChannel(callback, position, it)
                        }
                    }
                }
            }
            // search
            imgSearch.setOnClickListener {
                // show animated
                val slide = Slide(Gravity.TOP).apply {
                    duration = 500
                    addTarget(frameSearch)
                }
                TransitionManager.beginDelayedTransition(parent, slide)
                frameSearch.visibility = View.VISIBLE
                rvList.visibility = View.GONE
                rvSearch.visibility = View.VISIBLE
                fabAddGroup.visibility = View.GONE
                shimmerList.visibility = View.GONE
            }

            // close search
            imgCloseSearch.setOnClickListener {
                val slide = Slide(Gravity.TOP).apply {
                    duration = 500
                    addTarget(frameSearch)
                }
                TransitionManager.beginDelayedTransition(parent, slide)
                frameSearch.visibility = View.GONE
                rvList.visibility = View.VISIBLE
                rvSearch.visibility = View.GONE
                fabAddGroup.visibility = View.VISIBLE
                if (channelList.isEmpty()) shimmerList.visibility = View.VISIBLE
            }

            // channel list
            vm.getMyGroupListLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    if (it.status && it.data.isNotEmpty()) {
                        channelList.clear()
                        channelList.addAll(it.data)
                        shimmerList.visibility = View.GONE
                        imgEmpty.visibility = View.INVISIBLE
                        val arr = mutableListOf<GroupListEntity.GroupListData>()
                        channelList.forEach { data ->
                            arr.add(
                                GroupListEntity.GroupListData(
                                    data.channelId,
                                    data.channelName,
                                    "data.channelImage",
                                    data.unreadMessage,
                                    true
                                )
                            )
                        }
                        groupListAdapter.addItem(arr)
                    } else {
                        shimmerList.visibility = View.GONE
                        imgEmpty.visibility = View.VISIBLE
                    }
                }
            }

            //rv
            rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) fabAddGroup.visibility = View.GONE
                    else fabAddGroup.visibility = View.VISIBLE
                }
            })


            groupListAdapter.onGroupSelectedCallback {

                hideSmoothBar()
                val action = GroupListFragmentDirections.actionGroupListFragmentToGroupFragment(
                    groupId = it.groupId,
                    groupImage = it.groupImage,
                    groupName = it.groupName,
                    token = token,
                    userId = navArgs.userId
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun joinToChannel(
        callback: SimpleResponse?,
        position: Int,
        it: SearchChannelEntity.SearchChannelData
    ) {
        lifecycleScope.launch {
            binding?.apply {
                if (callback == null) {
                    it.loading = false
                    searchList[position] = it
                    rvSearch.adapter?.notifyItemChanged(searchList.indexOf(it))
                    msg("عدم ارتباط")
                    return@apply
                }
                if (callback.status) {
                    delay(500)
                    vm.getMyGroupList()
                    it.loading = false
                    it.isMember = true
                    it.users++
                    searchList[position] = it
                    rvSearch.adapter?.notifyItemChanged(searchList.indexOf(it))
                } else {
                    longMsg(callback.msg)
                }
                it.loading = false
                searchList[position] = it
                rvSearch.adapter?.notifyItemChanged(searchList.indexOf(it))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun searchGroup() = lifecycleScope.launch {
        binding?.apply {
            edtSearch.textChangeFlow().debounce(500).collect {
                if (it?.isNotEmpty() == true) {
                    vm.searchChannel(name = it.toString()) { list ->
                        if (list.isEmpty()) {
                            val snack =
                                Snackbar.make(parent, "محتوایی یافت نشد", Snackbar.LENGTH_SHORT)
                            snack.setActionTextColor(Color.WHITE)
                            ViewCompat.setLayoutDirection(
                                snack.view,
                                ViewCompat.LAYOUT_DIRECTION_RTL
                            )
                            snack.show()
                        }
                        searchList.clear()
                        searchList.addAll(list)
                        // bind to rv
                        searchAdapter.modifierItem(newItem = searchList)
                    }
                }
                if (it?.isEmpty() == true) {
                    searchAdapter.modifierItem(emptyList())
                }
            }
        }
    }

    private fun createChannel() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogThemeNoFloating)
        createChannel = dialog
        val view = LayerCreateChannelBinding.inflate(layoutInflater)
        dialog.setContentView(view.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.setCancelable(true)
        dialog.show()
        view.apply {
            edtName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerName.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerName.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                }
            }

            edtDesc.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerDesc.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerDesc.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                }
            }

            btnCreate.setOnClickListener {
                if (edtName.text.toString().isEmpty() || edtDesc.text.toString().isEmpty()) {
                    msg("مقادیر را وارد کنید")
                    return@setOnClickListener
                }
                btnCreate.loading(progress = progress)
                vm.createChannel(edtName.text.toString(), edtDesc.text.toString()) {
                    if (it != null)
                        if (it.status) {
                            dialog.dismiss()
                            getMyChannels()
                        } else {
                            longMsg(it.msg)
                        }
                    else msg("عدم ارتباط")
                    btnCreate.hideLoading(progress = progress)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
