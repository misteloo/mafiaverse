package ir.greendex.mafia.game.adapter.general

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.databinding.LayerRvUserQueueToSelectCharacterBinding
import ir.greendex.mafia.entity.game.general.UserQueueToPickEntity
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class UserQueueToSelectCharacterAdapter @Inject constructor() :
    RecyclerView.Adapter<UserQueueToSelectCharacterAdapter.UserQueueToSelectCharacterVH>() {
    class UserQueueToSelectCharacterVH(val binding: LayerRvUserQueueToSelectCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(it: UserQueueToPickEntity.UserQueueToPickData) {
            binding.apply {
                userImage.load(BASE_URL.plus(it.userImage))
                txtUserName.text = it.userName
            }
        }
    }

    private val items by lazy { mutableListOf<UserQueueToPickEntity.UserQueueToPickData>() }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserQueueToSelectCharacterVH {
        return UserQueueToSelectCharacterVH(
            LayerRvUserQueueToSelectCharacterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: UserQueueToSelectCharacterVH, position: Int) {
        val item = items[position]
        alphaAnimation(view = holder.binding.root)
        holder.bind(item)
    }

    private fun alphaAnimation(view:View){
        val anim = AlphaAnimation(0f,1f).apply {
            duration = 500
        }
        view.startAnimation(anim)
    }

    fun addItem(newItem: MutableList<UserQueueToPickEntity.UserQueueToPickData>) {
        val result = DiffUtil.calculateDiff(UserQueueToSelectCharacterDiffUtil(items, newItem))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class UserQueueToSelectCharacterDiffUtil(
        private val oldItem: MutableList<UserQueueToPickEntity.UserQueueToPickData>?,
        private val newItem: MutableList<UserQueueToPickEntity.UserQueueToPickData>?
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newItem?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition].userId == newItem!![newItemPosition].userId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition] == newItem!![newItemPosition]
        }
    }
}