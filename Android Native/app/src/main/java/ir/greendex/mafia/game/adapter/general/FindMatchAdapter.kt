package ir.greendex.mafia.game.adapter.general

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import ir.greendex.mafia.databinding.LayerRvUserJoinedFindMatchBinding
import ir.greendex.mafia.entity.game.general.FindMatchUserEntity
import ir.greendex.mafia.ui.group.adapter.GroupListAdapter
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.RV_ANIMATION_INTERVAL
import javax.inject.Inject

class FindMatchAdapter @Inject constructor(
    private val context:Context
) :
    RecyclerView.Adapter<FindMatchAdapter.FindMatchVH>() {
    inner class FindMatchVH(val binding: LayerRvUserJoinedFindMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FindMatchUserEntity.Data) {
            binding.apply {
                img.load(BASE_URL.plus(item.userImage))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindMatchVH {
        return FindMatchVH(
            LayerRvUserJoinedFindMatchBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FindMatchVH, position: Int) {
        val item = items[position]
        setAlphaAnimation(view = holder.binding.root)
        holder.bind(item)
    }

    private val items by lazy { mutableListOf<FindMatchUserEntity.Data>() }

    private fun setAlphaAnimation(view: View){
        AlphaAnimation(0f,1f).apply {
            duration = RV_ANIMATION_INTERVAL
            view.startAnimation(this)
        }
    }
    fun addItem(newItem: MutableList<FindMatchUserEntity.Data>) {
        val result = DiffUtil.calculateDiff(FindMatchDiffUtil(items, newItem))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class FindMatchDiffUtil(
        private val oldItem: MutableList<FindMatchUserEntity.Data>?,
        private val newItem: MutableList<FindMatchUserEntity.Data>?
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

