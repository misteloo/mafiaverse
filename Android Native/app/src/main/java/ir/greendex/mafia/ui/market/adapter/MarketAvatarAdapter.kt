package ir.greendex.mafia.ui.market.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.databinding.LayerRvItemMarketAvatarBinding
import ir.greendex.mafia.entity.market.MarketEntity
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.extension.numberSeparator
import javax.inject.Inject

class MarketAvatarAdapter @Inject constructor() :
    RecyclerView.Adapter<MarketAvatarAdapter.MarketAvatarVH>() {

    class MarketAvatarVH(val binding: LayerRvItemMarketAvatarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarketEntity.MarketDataItemsDetail) {
            binding.apply {
                img.load(BASE_URL.plus(item.image))
                txtPrice.text = item.price.toString().toCharArray().numberSeparator()
                btnPurchase.isEnabled = item.activeForUser
                if (item.activeForUser) {
                    btnPurchase.text = "خرید محصول"
                } else {
                    btnPurchase.text = "خرید شده"
                }
            }
        }
    }

    private val items by lazy { mutableListOf<MarketEntity.MarketDataItemsDetail>() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketAvatarVH {
        return MarketAvatarVH(
            LayerRvItemMarketAvatarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MarketAvatarVH, position: Int) {
        val item = items[position]
        holder.bind(item = item)
        holder.binding.also {
            it.btnPurchase.setOnClickListener {
                onPurchaseItem?.let {
                    it(item)
                }
            }
        }
    }

    private var onPurchaseItem: ((MarketEntity.MarketDataItemsDetail) -> Unit)? = null
    fun onPurchaseAvatarCallback(it: (MarketEntity.MarketDataItemsDetail) -> Unit) {
        onPurchaseItem = it
    }

    fun modifierItem(new: List<MarketEntity.MarketDataItemsDetail>) {
        val result = DiffUtil.calculateDiff(MarketAvatarDiffUtil(oldItem = items, newItem = new))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(new)
    }

    class MarketAvatarDiffUtil(
        private val oldItem: List<MarketEntity.MarketDataItemsDetail>,
        private val newItem: List<MarketEntity.MarketDataItemsDetail>
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