package ir.greendex.mafia.ui.group.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemGroupSearchBinding
import ir.greendex.mafia.entity.channel.SearchChannelEntity
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading
import javax.inject.Inject

class GroupSearchAdapter @Inject constructor(private val context: Context) :
    RecyclerView.Adapter<GroupSearchAdapter.GroupSearchVH>() {

    inner class GroupSearchVH(val binding: LayerRvItemGroupSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchChannelEntity.SearchChannelData) {
            binding.apply {
                imgGroup.load(BASE_URL.plus(item.channelImage))
                txtChannelName.text = item.channelName
                txtCup.text = item.cup.toString()
                txtUserCount.text = item.users.toString()
                if (item.pendingRequest) {
                    btnSendRequest.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context, R.color.orange
                        )
                    )
                    btnSendRequest.text = "لغو"
                }
                if (item.isMember) btnSendRequest.text = "عضو کانال"
                if (item.loading) btnSendRequest.loading(progress = progress)
                else btnSendRequest.hideLoading(progress = progress, enable = !item.isMember)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupSearchVH {
        return GroupSearchVH(
            LayerRvItemGroupSearchBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GroupSearchVH, position: Int) {
        val item = items[position]
        holder.bind(item = item)
        setAlphaAnimation(view = holder.binding.root)
        holder.binding.btnSendRequest.setOnClickListener {
            onJoinRequest?.let {
                it(item.channelId, position)
            }
        }
    }

    private fun setAlphaAnimation(view: View) {
        AlphaAnimation(0f, 1f).apply {
            duration = 1000
            view.startAnimation(this)
        }
    }

    private val items by lazy { mutableListOf<SearchChannelEntity.SearchChannelData>() }

    private var onJoinRequest: ((channelId: String, position: Int) -> Unit)? = null
    fun onJoinRequestCallback(callback: (channelId: String, position: Int) -> Unit) {
        onJoinRequest = callback
    }

    fun modifierItem(newItem: List<SearchChannelEntity.SearchChannelData>) {
        val result = DiffUtil.calculateDiff(GroupSearchDiffUtil(oldItem = items, newItem = newItem))
        result.dispatchUpdatesTo(this@GroupSearchAdapter)
        items.clear()
        items.addAll(newItem)
    }

    class GroupSearchDiffUtil(
        private val oldItem: List<SearchChannelEntity.SearchChannelData>,
        private val newItem: List<SearchChannelEntity.SearchChannelData>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem.size
        }

        override fun getNewListSize(): Int {
            return newItem.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].channelId == newItem[newItemPosition].channelId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].loading == newItem[newItemPosition].loading
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val (_, _, _, _, _, _, _, loadingOld) = oldItem[oldItemPosition]
            val (_, _, _, _, _, _, _, loadingNew) = newItem[newItemPosition]

            return if (loadingOld == loadingNew) null
            else Bundle().apply {
                putString("", "")
            }

        }

    }
}
