package ir.greendex.mafia.ui.local_game.adapter

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
import ir.greendex.mafia.databinding.LayerRvItemDeckBinding
import ir.greendex.mafia.entity.local.LocalSelectDeckEntity
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class LocalSelectDeckAdapter @Inject constructor(
    private val context: Context
) :
    RecyclerView.Adapter<LocalSelectDeckAdapter.SelectDeckVH>() {
    inner class SelectDeckVH(val binding: LayerRvItemDeckBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocalSelectDeckEntity) {
            binding.apply {
                imgCharacter.load(BASE_URL.plus(item.icon)){
                    crossfade(true)
                    crossfade(100)
                }
                txtCharacter.text = item.name
                when (item.side) {
                    "citizen" -> imgSide.load(R.drawable.citizen_hat)
                    "mafia" -> imgSide.load(R.drawable.mafia_hat)
                    "solo" -> imgSide.load(R.drawable.solo_hat)
                }

                txtCount.text = item.count.toString()
                if (item.selected) {
                    root.strokeColor = ContextCompat.getColor(context,R.color.red500)
                }else {
                    root.strokeColor = ContextCompat.getColor(context,R.color.grey800)
                }

                if (item.selected && item.multi){
                    fabInc.visibility = View.VISIBLE
                    fabDec.visibility = View.VISIBLE
                }else {
                    fabInc.visibility = View.GONE
                    fabDec.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectDeckVH {
        return SelectDeckVH(
            LayerRvItemDeckBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SelectDeckVH, position: Int) {
        val item = items[position]
        holder.bind(item)
        // set alpha anim
        setAlphaAnimation(view = holder.binding.root)

        holder.binding.apply {
            root.setOnClickListener {
                item.selected = !item.selected
                if (!item.selected) {
                    item.count = 0
                    onSelect?.let {
                        it(item,false)
                    }
                }
                else {
                    item.count = 1
                    onSelect?.let {
                        it(item,true)
                    }
                }
                holder.bind(item)

            }

            root.setOnLongClickListener {
                onMore?.let {
                    it(item)
                }
                return@setOnLongClickListener false
            }

            fabDec.setOnClickListener {
                if (item.selected){
                    if (item.count == 1) return@setOnClickListener
                    item.count -= 1
                    holder.bind(item)
                    onIncDec?.let {
                        it(item,false)
                    }
                }
            }

            fabInc.setOnClickListener {
                if (item.selected){
                    item.count += 1
                    holder.bind(item)
                    onIncDec?.let {
                        it(item,true)
                    }
                }
            }
        }
    }

    private val items by lazy { mutableListOf<LocalSelectDeckEntity>() }

    private fun setAlphaAnimation(view:View){
        AlphaAnimation(0f,1f).apply {
            duration = 500
            view.startAnimation(this)
        }
    }

    private var onMore: ((LocalSelectDeckEntity) -> Unit)? = null
    fun onMoreCallback(it: (LocalSelectDeckEntity) -> Unit) {
        onMore = it
    }

    private var onSelect: ((LocalSelectDeckEntity, checked: Boolean) -> Unit)? = null
    fun onSelectCallback(it: (LocalSelectDeckEntity, checked: Boolean) -> Unit) {
        onSelect = it
    }

    private var onIncDec: ((LocalSelectDeckEntity, inc:Boolean) -> Unit)? = null
    fun onIncDecCallback(it: (LocalSelectDeckEntity, inc:Boolean) -> Unit) {
        onIncDec = it
    }

    fun modifierItem(newItem: List<LocalSelectDeckEntity>) {
        val result = DiffUtil.calculateDiff(SelectDeckDifUtil(items, newItem))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class SelectDeckDifUtil(
        private val oldItem: List<LocalSelectDeckEntity>,
        private val newItem: List<LocalSelectDeckEntity>
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