package ir.greendex.mafia.game.adapter.general

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.databinding.LayerRvItemGameResultFreeSpeechBinding
import ir.greendex.mafia.entity.game.general.GameResultUserFreeSpeechEntity
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class GameResultFreeSpeechAdapter @Inject constructor() :
    RecyclerView.Adapter<GameResultFreeSpeechAdapter.GameResultFreeSpeechVH>() {
    class GameResultFreeSpeechVH(val binding: LayerRvItemGameResultFreeSpeechBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GameResultUserFreeSpeechEntity) {
            binding.apply {
                imgUser.load(BASE_URL.plus(item.userImage))
                txtUsername.text = item.userName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameResultFreeSpeechVH {
        return GameResultFreeSpeechVH(
            LayerRvItemGameResultFreeSpeechBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GameResultFreeSpeechVH, position: Int) {

        val item = items[position]
        holder.bind(item)
        setAlphaAnimation(holder.binding.root)
    }

    private val items by lazy { mutableListOf<GameResultUserFreeSpeechEntity>() }

    fun modifierItem(newList: List<GameResultUserFreeSpeechEntity>) {
        val result =
            DiffUtil.calculateDiff(GameResultFreeSpeechDiffUtil(newItem = newList, oldItem = items))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newList)
    }

    private fun setAlphaAnimation(view: View) {
        AlphaAnimation(0f, 1f).apply {
            duration = 500
            view.startAnimation(this)
        }
    }

    class GameResultFreeSpeechDiffUtil(
        private val oldItem: List<GameResultUserFreeSpeechEntity>,
        private val newItem: List<GameResultUserFreeSpeechEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItem.size

        override fun getNewListSize() = newItem.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].userId == newItem[newItemPosition].userId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].isTalking == newItem[newItemPosition].isTalking
        }

    }
}