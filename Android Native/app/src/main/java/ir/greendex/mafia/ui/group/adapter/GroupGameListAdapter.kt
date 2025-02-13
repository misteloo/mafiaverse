package ir.greendex.mafia.ui.group.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemChannelGameBinding
import ir.greendex.mafia.entity.channel.ChannelGameData
import javax.inject.Inject

class GroupGameListAdapter @Inject constructor() :
    RecyclerView.Adapter<GroupGameListAdapter.ChannelGameVH>() {
    private lateinit var userId: String

    inner class ChannelGameVH(val binding: LayerRvItemChannelGameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChannelGameData) {
            binding.apply {
                txtEntireGold.text = item.entireGold.toString()
                item.users.filter {
                    it.accepted
                }.apply {
                    txtUsers.text = this.size.toString().plus("/10")
                }

                if (item.finished) {
                    txtGameStatus.text = "اتمام"
                    imgFinished.load(R.drawable.style_empty_red)
                } else {
                    txtGameStatus.text = "شروع نشده"
                    imgFinished.load(R.drawable.style_empty_orange)
                }
                if (item.started) {
                    txtGameStatus.text = "در حال بازی"
                    imgFinished.load(R.drawable.style_empty_green)
                }

                // disable manage button
                if (item.creatorId != userId) btnManage.visibility = View.GONE
                // disable join item
                if (item.creatorId == userId) btnSendRequest.visibility = View.GONE
                item.users.find {
                    it.userId == userId
                }.let { let ->
                    // find user on list
                    if (let != null) {
                        if (let.accepted) {
                            btnSendRequest.isEnabled = false
                            btnSendRequest.text = "تایید شده"
                            if (item.creatorId == userId) {
                                btnManage.visibility = View.VISIBLE
                            }
                        } else {
                            // pending
                            btnSendRequest.text = "منتظر تایید"
                            btnSendRequest.isEnabled = false
                        }
                    } else {
                        // pending
                        btnSendRequest.text = "درخواست پیوستن"
                        btnSendRequest.isEnabled = true
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelGameVH {
        return ChannelGameVH(
            LayerRvItemChannelGameBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ChannelGameVH, position: Int) {
        val item = items[position]
        holder.bind(item = item)
        setAlphaAnimation(view = holder.binding.root)
        holder.binding.apply {
            btnSendRequest.setOnClickListener {
                if (item.started) return@setOnClickListener
                onRequestJoin?.let {
                    it(item.gameId)
                }
            }

            btnManage.setOnClickListener {
                onManage?.let {
                    it(item.gameId)
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: ChannelGameVH,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        Log.i("LOG", "onBindViewHolder: $payloads")
    }

    private fun setAlphaAnimation(view: View) {
        AlphaAnimation(0f, 1f).apply {
            duration = 1000
            view.startAnimation(this)
        }
    }

    private var onManage: ((String) -> Unit)? = null
    fun onManageClickedCallback(it: (String) -> Unit) {
        onManage = it
    }

    private var onRequestJoin: ((String) -> Unit)? = null
    fun onRequestJoinCallback(it: (String) -> Unit) {
        onRequestJoin = it
    }


    private val items = mutableListOf<ChannelGameData>()

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun addItem(newItem: List<ChannelGameData>) {
        val result = DiffUtil.calculateDiff(ChannelGameDiffUtil(newItem = newItem, oldItem = items))
        items.clear()
        items.addAll(newItem)
        result.dispatchUpdatesTo(this)
    }

    class ChannelGameDiffUtil(
        private val oldItem: List<ChannelGameData>,
        private val newItem: List<ChannelGameData>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem.size
        }

        override fun getNewListSize(): Int {
            return newItem.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].gameId === newItem[newItemPosition].gameId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] == newItem[newItemPosition]
        }

    }
}