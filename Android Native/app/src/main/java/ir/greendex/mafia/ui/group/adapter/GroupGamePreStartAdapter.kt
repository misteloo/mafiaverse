package ir.greendex.mafia.ui.group.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemGroupGamePreStartBinding
import ir.greendex.mafia.entity.channel.OnlineGameUpdatePreStartEntity
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class GroupGamePreStartAdapter @Inject constructor() :
    RecyclerView.Adapter<GroupGamePreStartAdapter.GroupGamePreStartVH>() {
    private lateinit var userId: String
    private lateinit var creatorId: String
    private var requisition = false

    inner class GroupGamePreStartVH(val binding: LayerRvItemGroupGamePreStartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OnlineGameUpdatePreStartEntity.OnLineGameUpdatePreStartData) {
            binding.apply {
                txtUsername.text = item.userName
                imgUserImage.load(BASE_URL.plus(item.userImage))
                when (item.isReady) {
                    -1 -> imgUserState.load(R.drawable.style_empty_orange)
                    0 -> imgUserState.load(R.drawable.style_empty_red)
                    1 -> imgUserState.load(R.drawable.style_empty_green)
                }
                if (userId == creatorId) {
                    // could not modifier on moderator
                    if (item.userId == creatorId) return
                    imgDelete.visibility = View.VISIBLE
                    imgConfirm.visibility = View.VISIBLE
                }
                if (!requisition){
                    imgConfirm.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupGamePreStartVH {
        return GroupGamePreStartVH(
            LayerRvItemGroupGamePreStartBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GroupGamePreStartVH, position: Int) {
        val item = items[position]
        setAlphaAnimation(view = holder.binding.root)
        holder.bind(item)
        holder.binding.apply {
            imgDelete.setOnClickListener {
                onUserDeni?.let {
                    it(false, item.userId, requisition)
                }

            }

            imgConfirm.setOnClickListener {
                onUserAccept?.let {
                    it(true, item.userId)
                }
            }
        }
    }

    private var onUserAccept: ((Boolean, String) -> Unit)? = null
    fun onUserAcceptCallback(it: (state: Boolean, userId: String) -> Unit) {
        onUserAccept = it
    }

    private var onUserDeni: ((state: Boolean, userId: String, requisition: Boolean) -> Unit)? = null
    fun onUserDeniCallback(it: (state: Boolean, userId: String, requisition: Boolean) -> Unit) {
        onUserDeni = it
    }

    private fun setAlphaAnimation(view: View) {
        AlphaAnimation(0f, 1f).apply {
            duration = 1000
            view.startAnimation(this)
        }
    }

    private val items by lazy { mutableListOf<OnlineGameUpdatePreStartEntity.OnLineGameUpdatePreStartData>() }

    fun setPage(requisition: Boolean) {
        this.requisition = requisition
    }

    fun setCreatorId(creatorId: String) {
        this.creatorId = creatorId
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun modifierItem(newList: List<OnlineGameUpdatePreStartEntity.OnLineGameUpdatePreStartData>) {
        val result =
            DiffUtil.calculateDiff(GroupGamePreStartDiffUtil(oldItem = items, newItem = newList))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newList)
    }


    class GroupGamePreStartDiffUtil(
        private val oldItem: List<OnlineGameUpdatePreStartEntity.OnLineGameUpdatePreStartData>?,
        private val newItem: List<OnlineGameUpdatePreStartEntity.OnLineGameUpdatePreStartData>?
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newItem?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition].userId === newItem!![newItemPosition].userId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition] == newItem!![newItemPosition]
        }

    }

}