package ir.greendex.mafia.ui.game_history.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemTotalGameHistoryBinding
import ir.greendex.mafia.entity.game_history.TotalGameHistoryEntity
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

class TotalGameAdapter @Inject constructor(
    private val context: Context
) : RecyclerView.Adapter<TotalGameAdapter.TotalGameVH>() {

    inner class TotalGameVH(val binding: LayerRvItemTotalGameHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TotalGameHistoryEntity.GameTotalHistoryListEntity) {
            binding.apply {
                when (item.role) {
                    "شهروند" -> imgCharacter.load(R.drawable.citizen)
                    "دکتر" -> imgCharacter.load(R.drawable.doctor)
                    "کاراگاه" -> imgCharacter.load(R.drawable.detective)
                    "تفنگدار" -> imgCharacter.load(R.drawable.rifileman)
                    "تکاور" -> imgCharacter.load(R.drawable.commando)
                    "نگهبان" -> imgCharacter.load(R.drawable.guard)
                    "ناتو" -> imgCharacter.load(R.drawable.nato)
                    "گروگانگیر" -> imgCharacter.load(R.drawable.hostage_taker)
                    "پدر خوانده" -> imgCharacter.load(R.drawable.godfather)
                }

                txtCharacter.text = item.role
                if (item.isWinner) {
                    ImageWinOrLose.load(R.drawable.image_winner)
                    txtWinOrLose.text = "برد"
                } else {
                    ImageWinOrLose.load(R.drawable.image_losser)
                    txtWinOrLose.text = "باخت"
                }

                // date
                val persian = PersianDate(item.date)
                txtDate.text = persian.shYear.toString().plus("/").plus(persian.shMonth).plus("/")
                    .plus(persian.shDay).plus("-").plus(persian.hour).plus(":").plus(persian.minute)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalGameVH {
        return TotalGameVH(
            LayerRvItemTotalGameHistoryBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TotalGameVH, position: Int) {
        val item = items[position]
        holder.bind(
            item
        )
        holder.binding.root.setOnClickListener {
            onSelectedGame?.let {
                it(item.gameId)
            }
        }
        alphaAnim(holder.binding.root)
    }


    private val items by lazy { mutableListOf<TotalGameHistoryEntity.GameTotalHistoryListEntity>() }

    private fun alphaAnim(view: View) {
        AlphaAnimation(0f,1f).apply {
            duration = 500
            view.startAnimation(this)
        }
    }

    private var onSelectedGame: ((String) -> Unit)? = null
    fun onSelectedGameCallback(it: (String) -> Unit) {
        onSelectedGame = it
    }


    fun modifierItem(newItem:List<TotalGameHistoryEntity.GameTotalHistoryListEntity>){
        val result = DiffUtil.calculateDiff(TotalGameHistoryDiffUtil(
            oldList = items , newList = newItem
        ))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class TotalGameHistoryDiffUtil(
        private val oldList: List<TotalGameHistoryEntity.GameTotalHistoryListEntity>,
        private val newList:List<TotalGameHistoryEntity.GameTotalHistoryListEntity>
    ):DiffUtil.Callback(){
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].gameId == newList[newItemPosition].gameId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
}