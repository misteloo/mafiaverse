package ir.greendex.mafia.ui.group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemGroupListBinding
import ir.greendex.mafia.entity.group.GroupListEntity
import ir.greendex.mafia.util.RV_ANIMATION_INTERVAL
import javax.inject.Inject

class GroupListAdapter @Inject constructor(private val context: Context) :
    RecyclerView.Adapter<GroupListAdapter.GroupListVH>() {
    private var myUserId: String? = null

    inner class GroupListVH(val binding: LayerRvItemGroupListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupListEntity.GroupListData) {
            binding.apply {
                imgGroup.load(R.drawable.test)
                txtGroupName.text = item.groupName
                if (item.unreadMessage > 0) {
                    txtUnreadMessage.also {
                        it.visibility = View.VISIBLE
                        it.text = item.unreadMessage.toString()
                    }
                } else {
                    txtUnreadMessage.also {
                        it.text = ""
                        it.visibility = View.INVISIBLE
                    }
                }

                if (item.notificationStatus) imgNotificationOff.visibility =
                    View.INVISIBLE else imgNotificationOff.visibility = View.VISIBLE
            }
        }
    }

    fun setUserId(myUserId: String?) {
        this.myUserId = myUserId
    }

    private val items by lazy { mutableListOf<GroupListEntity.GroupListData>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListVH {
        return GroupListVH(
            LayerRvItemGroupListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GroupListVH, position: Int) {
        val item = items[position]
        // animation
        setAlphaAnimation(view = holder.binding.root)
        holder.bind(item = item)
        holder.binding.root.setOnClickListener {
            onGroupSelected?.let {
                it(item)
            }
        }
        holder.binding.framePin.setOnClickListener {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAlphaAnimation(view: View) {
        AlphaAnimation(0f, 1f).apply {
            duration = RV_ANIMATION_INTERVAL
            view.startAnimation(this)
        }
    }

    fun addItem(newItem: MutableList<GroupListEntity.GroupListData>) {
        val result = DiffUtil.calculateDiff(GroupListDiffUtil(newItem = newItem, oldItem = items))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    private var onGroupSelected: ((GroupListEntity.GroupListData) -> Unit)? = null
    fun onGroupSelectedCallback(it: (GroupListEntity.GroupListData) -> Unit) {
        onGroupSelected = it
    }

    class GroupListDiffUtil(
        private val newItem: MutableList<GroupListEntity.GroupListData>?,
        private val oldItem: MutableList<GroupListEntity.GroupListData>?
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newItem?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newItem!![newItemPosition].groupId == oldItem!![oldItemPosition].groupId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newItem!![newItemPosition] == oldItem!![oldItemPosition]
        }

    }
}