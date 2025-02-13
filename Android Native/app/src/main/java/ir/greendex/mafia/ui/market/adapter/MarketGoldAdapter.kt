package ir.greendex.mafia.ui.market.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.databinding.LayerRvItemMarketGoldBinding
import ir.greendex.mafia.entity.market.MarketGoldEntity
import ir.greendex.mafia.util.extension.numberSeparator
import ir.greendex.mafia.util.extension.span
import javax.inject.Inject

class MarketGoldAdapter @Inject constructor() :
    RecyclerView.Adapter<MarketGoldAdapter.MarketGoldVH>() {

    class MarketGoldVH(val binding: LayerRvItemMarketGoldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarketGoldEntity) {
            binding.apply {

                img.load(item.srcImage)
                txtCount.text = " بسته ".plus(item.count).plus(" تایی ")
                if (item.off) {
                    imgOff.visibility = View.VISIBLE
                    layerOff.visibility = View.VISIBLE
                    layerPercentOff.visibility = View.VISIBLE
                    txtPercentOff.text = item.offPercent
                    txtOffPrice.text = item.currentPrice.toCharArray().numberSeparator().plus(" تومان ")
                    item.lastPrice?.let {
                        txtPrice.text = it.toCharArray().numberSeparator().plus(" تومان ").span()
                    }
                }else {
                    txtPrice.text = item.currentPrice.toCharArray().numberSeparator().plus(" تومان ")
                    imgOff.visibility = View.INVISIBLE
                    layerPercentOff.visibility = View.INVISIBLE
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketGoldVH {
        return MarketGoldVH(
            LayerRvItemMarketGoldBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MarketGoldVH, position: Int) {
        val item = items[position]
        holder.bind(item = item)
        holder.binding.btnPurchase.setOnClickListener {
            purchaseGold?.let {
                it(item)
            }
        }
    }

    private val items by lazy { mutableListOf<MarketGoldEntity>() }

    private var purchaseGold:((MarketGoldEntity)->Unit)?=null
    fun purchaseGoldCallback(it:(MarketGoldEntity)->Unit){
        purchaseGold = it
    }

    fun modifierItem(newItems: List<MarketGoldEntity>) {
        val result = DiffUtil.calculateDiff(MarketGoldDiffUtil(oldItem = items, newItem = newItems))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItems)
    }

    class MarketGoldDiffUtil(
        private val oldItem: List<MarketGoldEntity>,
        private val newItem: List<MarketGoldEntity>
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
