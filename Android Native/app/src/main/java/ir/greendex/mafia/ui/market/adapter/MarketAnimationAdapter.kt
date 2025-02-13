package ir.greendex.mafia.ui.market.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.greendex.mafia.databinding.LayerRvItemMarketAnimBinding
import ir.greendex.mafia.entity.market.MarketEntity
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class MarketAnimationAdapter @Inject constructor() :
    RecyclerView.Adapter<MarketAnimationAdapter.MarketAnimationVH>() {

    class MarketAnimationVH(val binding: LayerRvItemMarketAnimBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarketEntity.MarketDataItemsDetail) {
            binding.apply {
                anim.setAnimationFromUrl(BASE_URL.plus(item.anim))
                txtPrice.text = item.price.toString()
                btnPurchase.isEnabled = item.activeForUser
                if (item.activeForUser) {
                    btnPurchase.text = "خرید محصول"
                } else {
                    btnPurchase.text = "خرید شده"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketAnimationVH {
        return MarketAnimationVH(
            LayerRvItemMarketAnimBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MarketAnimationVH, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.binding.btnPurchase.setOnClickListener {
            onPurchaseItem?.let {
                it(item)
            }
        }
    }

    private val items by lazy { mutableListOf<MarketEntity.MarketDataItemsDetail>() }

    private var onPurchaseItem: ((MarketEntity.MarketDataItemsDetail) -> Unit)? = null
    fun onPurchaseAnimItemCallback(it: (MarketEntity.MarketDataItemsDetail) -> Unit) {
        onPurchaseItem = it
    }

    fun modifierItem(newItem: List<MarketEntity.MarketDataItemsDetail>) {
        val result = DiffUtil.calculateDiff(
            MarketAnimationDiffUtil(
                oldItem = items,
                newItem = newItem
            )
        )
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class MarketAnimationDiffUtil(
        private val newItem: List<MarketEntity.MarketDataItemsDetail>,
        private val oldItem: List<MarketEntity.MarketDataItemsDetail>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItem.size

        override fun getNewListSize() = newItem.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].id == newItem[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] == newItem[newItemPosition]
        }

    }
}