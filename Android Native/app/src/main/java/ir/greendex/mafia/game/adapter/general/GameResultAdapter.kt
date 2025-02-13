package ir.greendex.mafia.game.adapter.general

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemGameResultBinding
import ir.greendex.mafia.entity.game.general.EndGameResultEntity
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class GameResultAdapter @Inject constructor() :
    RecyclerView.Adapter<GameResultAdapter.GameResultVH>() {

    class GameResultVH(val binding: LayerRvItemGameResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EndGameResultEntity.EndGameResultUsers) {
            binding.apply {
                img.load(BASE_URL.plus(item.userImage))
                txtUsername.text = item.userName
                when (item.role) {
                    "پدر خوانده" -> imgCharacter.load(R.drawable.godfather)
                    "ناتو" -> imgCharacter.load(R.drawable.nato)
                    "گروگانگیر" -> imgCharacter.load(R.drawable.hostage_taker)
                    "شهروند" -> imgCharacter.load(R.drawable.citizen)
                    "کاراگاه" -> imgCharacter.load(R.drawable.detective)
                    "دکتر" -> imgCharacter.load(R.drawable.doctor)
                    "تکاور" -> imgCharacter.load(R.drawable.commando)
                    "تفنگدار" -> imgCharacter.load(R.drawable.rifileman)
                    "نگهبان" -> imgCharacter.load(R.drawable.guard)
                }
                txtCharacter.text = item.role
                txtXp.text = item.xp.toString()
                txtCup.text = if (item.point > 0) "+".plus(item.point) else item.point.toString()
                txtGold.text = if (item.gold > 0) "+".plus(item.gold) else "0"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameResultVH {
        return GameResultVH(
            LayerRvItemGameResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GameResultVH, position: Int) {
        val item = items[position]
        holder.bind(item)
        alphaAnimation(holder.binding.root)
    }

    private val items by lazy { mutableListOf<EndGameResultEntity.EndGameResultUsers>() }

    private fun alphaAnimation(view:View){
        AlphaAnimation(0f,1f).apply {
            duration = 1500
            view.startAnimation(this)
        }
    }
    fun modifierItem(newItem:List<EndGameResultEntity.EndGameResultUsers>){
        val result = DiffUtil.calculateDiff(GameResultDiffUtil(oldItem = items,newItem = newItem))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }
    class GameResultDiffUtil(
        private val oldItem: List<EndGameResultEntity.EndGameResultUsers>,
        private val newItem: List<EndGameResultEntity.EndGameResultUsers>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItem.size

        override fun getNewListSize() = newItem.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].userId == newItem[newItemPosition].userId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] == newItem[newItemPosition]
        }
    }
}