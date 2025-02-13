package ir.greendex.mafia.ui.transaction

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemTransactionBinding
import ir.greendex.mafia.entity.transaction.TransactionEntity
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.extension.numberSeparator
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

class TransactionAdapter @Inject constructor(
    private val context: Context
) :
    RecyclerView.Adapter<TransactionAdapter.TransactionVH>() {

    inner class TransactionVH(val binding: LayerRvItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionEntity.TransactionData) {
            binding.apply {
                txtTitle.text = item.note
                if (item.device == "web") {
                    imgDevice.load(R.drawable.ic_website)
                    txtDevice.text = "سایت"
                } else {
                    imgDevice.load(R.drawable.round_phone_iphone_24)
                    txtDevice.text = "برنامه"
                }
                txtGold.text = item.gold.toString().toCharArray().numberSeparator().plus(" سکه ")
                if (item.price.isNotEmpty()) {
                    txtPrice.text = item.price.toCharArray().numberSeparator().plus(" تومان ")
                } else {
                    txtPrice.text = "-"
                }

                if (item.type == "gold") productImage.load(R.drawable.gold)
                else productImage.load(BASE_URL.plus(item.item))

                // date
                PersianDate(item.date).apply {
                    txtDate.text =
                        shYear.toString().plus("/").plus(shMonth).plus("/").plus(shDay).plus("-")
                            .plus(hour).plus(":").plus(minute)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        return TransactionVH(
            LayerRvItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TransactionVH, position: Int) {
        val item = items[position]
        holder.bind(item)
        alphaAnim(view = holder.binding.root)
    }

    private fun alphaAnim(view:View){
        AlphaAnimation(0f,1f).apply {
            duration = 500
            view.startAnimation(this)
        }
    }

    private val items by lazy { mutableListOf<TransactionEntity.TransactionData>() }
    fun modifierItem(newItem: List<TransactionEntity.TransactionData>) {
        val result =
            DiffUtil.calculateDiff(TransactionHistoryDiffUtil(oldItem = items, newItem = newItem))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class TransactionHistoryDiffUtil(
        private val oldItem: List<TransactionEntity.TransactionData>,
        private val newItem: List<TransactionEntity.TransactionData>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItem.size

        override fun getNewListSize() = newItem.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].date == newItem[newItemPosition].date
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] == newItem[newItemPosition]
        }

    }
}