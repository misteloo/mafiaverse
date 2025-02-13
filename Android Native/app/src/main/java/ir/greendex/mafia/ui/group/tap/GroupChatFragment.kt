package ir.greendex.mafia.ui.group.tap

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.databinding.FragmentGroupChatBinding
import ir.greendex.mafia.entity.channel.ChannelMessageEntity
import ir.greendex.mafia.ui.group.adapter.GroupAdapter
import ir.greendex.mafia.util.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class GroupChatFragment : BaseFragment() {
    private var _binding: FragmentGroupChatBinding? = null
    private val binding get() = _binding
    private val channelMessage by lazy { mutableListOf<ChannelMessageEntity.ChannelMessageData>() }
    private var dbMessageEndPage by Delegates.notNull<Int>()
    private var dbMessageCurrentPage = 0
    private var dbMessageStartOffset = 0
    private var scrollRvToEnd = true
    private var unreadSize = 0
    private lateinit var layoutManager: LinearLayoutManager

    // injection
    @Inject
    lateinit var adapter: GroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupChatBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // views
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            //set user id to adapter
            adapter.setUserId(userId = MainActivity.userId!!)

            // rv
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            rv.layoutManager = layoutManager
            rv.adapter = adapter


            // scroll rv
            rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == 1 || newState == 2) {
                        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && dbMessageEndPage != dbMessageCurrentPage) {
                            dbMessageCurrentPage++
                            dbMessageStartOffset += 15
                            scrollRvToEnd = false
                            getLocalMsg?.let {
                                it(dbMessageStartOffset)
                            }
                        } else {
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    if (channelMessage[layoutManager.findLastCompletelyVisibleItemPosition()] == channelMessage.last()) {
                                        unreadSize = 0
                                        withContext(Dispatchers.Main) {
                                            hideUnreadLayer()
                                        }
                                    }
                                } catch (_: Exception) {
                                }
                            }
                        }
                    }
                }
            })

            // send message
            floatingActionButton.setOnClickListener {
                if (edtMsg.text.toString().isEmpty()) return@setOnClickListener
                sendMsg?.let {
                    it(edtMsg.text.toString())
                }
                edtMsg.text.clear()
            }

            // scroll to end of rv when soft keyboard open
            rv.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    rv.scrollBy(0, oldBottom - bottom)
                }
            }

            // scroll to rv end by click
            layerUnread.fabScrollToEnd.setOnClickListener {
                unreadSize = 0
                hideUnreadLayer()
                scrollRvToEnd()
            }
        }
    }

    private var sendMsg: ((String) -> Unit)? = null
    fun sendMsgCallback(it: (String) -> Unit) {
        sendMsg = it
    }

    private var getLocalMsg: ((Int) -> Unit)? = null
    fun getLocalMessageCallback(it: (Int) -> Unit) {
        getLocalMsg = it
    }

    fun bindMessagesToRv(
        message: ChannelMessageEntity.ChannelMessageData? = null,
        messageList: List<ChannelMessageEntity.ChannelMessageData> = emptyList(),
        scrollToEnd: Boolean? = null,
        fromLocal: Boolean
    ) {
        scrollRvToEnd = scrollToEnd == true

        lifecycleScope.launch {
            // define layer should scroll to end or not
            if (::layoutManager.isInitialized) {
                scrollRvToEnd =
                    layoutManager.findLastCompletelyVisibleItemPosition() == channelMessage.lastIndex
            }

            if (messageList.isNotEmpty()) {
                val job = CoroutineScope(Dispatchers.IO).launch {
                    channelMessage.addAll(messageList)
                }
                job.join()
                channelMessage.sortedBy {
                    it.messageTime
                }.apply {
                    adapter.addItem(newItem = this)
                }

            }
            message?.let {
                val job = CoroutineScope(Dispatchers.IO).launch {
                    channelMessage.add(it)
                }
                job.join()
                channelMessage.sortedBy {
                    it.messageTime
                }.apply {
                    adapter.addItem(newItem = this)
                }
            }
            // scroll to end
            if (scrollRvToEnd) {
                scrollRvToEnd()
            } else {
                if (!fromLocal){
                    if (messageList.isNotEmpty()) unreadSize += messageList.size else unreadSize++
                    showUnreadLayer(unreadSize = unreadSize)
                }
            }
        }
    }

    fun dbMessageSize(size: Int) {
        dbMessageEndPage = size
    }

    private fun scrollRvToEnd() {
        binding?.apply {
            rv.postDelayed({
                try {
                    rv.smoothScrollToPosition(adapter.itemCount - 1)
                } catch (_: Exception) {
                }
            }, 150)
        }
    }

    private fun showUnreadLayer(unreadSize: Int) {
        binding?.apply {
            layerUnread.txtUnread.text = unreadSize.toString()
            val show = Slide(Gravity.BOTTOM).apply {
                duration = 200
                addTarget(layerUnread.root)
            }
            TransitionManager.beginDelayedTransition(parent, show)
            layerUnread.root.visibility = View.VISIBLE
        }
    }

    private fun hideUnreadLayer() {
        binding?.apply {
            val show = Slide(Gravity.BOTTOM).apply {
                duration = 200
                addTarget(layerUnread.root)
            }
            TransitionManager.beginDelayedTransition(parent, show)
            layerUnread.root.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}