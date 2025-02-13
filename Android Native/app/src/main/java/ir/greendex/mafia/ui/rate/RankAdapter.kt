package ir.greendex.mafia.ui.rate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemRankBinding
import ir.greendex.mafia.entity.rate.RateRvEntity
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.extension.disableDoubleClick
import javax.inject.Inject

class RankAdapter @Inject constructor() :
    RecyclerView.Adapter<RankAdapter.RankVH>() {
    class RankVH(val binding: LayerRvItemRankBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RateRvEntity) {
            binding.apply {
                imgUser.load(BASE_URL.plus(item.image))
                txtUserIndex.text = item.index.toString()
                txtUsername.text = item.name
                txtCup.text = item.rank
                txtPrize.text = item.gold
                when (item.place) {
                    1 -> imgMedal.load(R.drawable.rate_first_place)
                    2 -> imgMedal.load(R.drawable.rate_second_place)
                    3 -> imgMedal.load(R.drawable.rate_place_3)
                    else -> imgMedal.load(null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankVH {
        return RankVH(
            LayerRvItemRankBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RankVH, position: Int) {
        val item = items[position]
        holder.bind(item = item)
        alphaAnim(holder.binding.root)

        // click
        holder.binding.root.setOnClickListener {
            holder.binding.root.disableDoubleClick(timer = 2)
            onClickedUser?.let {
                it(item.userId)
            }
        }
    }

    private var onClickedUser: ((String) -> Unit)? = null
    fun onClickedUserCallback(it: (String) -> Unit) {
        onClickedUser = it
    }

    private fun alphaAnim(view: View) {
        AlphaAnimation(0f, 1f).apply {
            duration = 500
            view.startAnimation(this)
        }
    }

    private val items by lazy { mutableListOf<RateRvEntity>() }
    fun modifierItem(newItem: List<RateRvEntity>) {
        val result = DiffUtil.calculateDiff(RankDiffUtil(oldList = items, newList = newItem))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class RankDiffUtil(
        private val oldList: List<RateRvEntity>?,
        private val newList: List<RateRvEntity>?
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList?.size ?: 0

        override fun getNewListSize() = newList?.size ?: 0

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList!![oldItemPosition].userId == newList!![newItemPosition].userId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList!![oldItemPosition] == newList!![newItemPosition]
        }
    }
}