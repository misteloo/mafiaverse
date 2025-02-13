package ir.greendex.mafia.ui.edit_profile.adapter

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
import ir.greendex.mafia.databinding.LayerRvItemEditAvatarBinding
import ir.greendex.mafia.entity.edit_profile.UserItemsEntity
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class EditAvatarAdapter @Inject constructor(private val context: Context) :
    RecyclerView.Adapter<EditAvatarAdapter.EditAvatarVH>() {

    inner class EditAvatarVH(val binding: LayerRvItemEditAvatarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserItemsEntity.UserItemData.UserItemsList) {
            binding.apply {
                img.load(BASE_URL.plus(item.image))
                btn.isEnabled = !item.active
                if (!item.active) {
                    btn.setBackgroundColor(ContextCompat.getColor(context, R.color.red500))
                    btn.text = "پیش نمایش"
                } else {
                    btn.setBackgroundColor(ContextCompat.getColor(context, R.color.grey500))
                    btn.text = "فعال"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditAvatarVH {
        return EditAvatarVH(
            LayerRvItemEditAvatarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: EditAvatarVH, position: Int) {
        val item = items[position]
        holder.bind(item)
        alphaAnim(view = holder.binding.root)
        holder.binding.apply {
            btn.setOnClickListener {
                onActive?.let {
                    it(item)
                }
                updateItem(item = item)
            }
        }
    }


    private fun alphaAnim(view: View) {
        AlphaAnimation(0f, 1f).apply {
            duration = 500
            view.startAnimation(this)
        }
    }

    private val items by lazy { mutableListOf<UserItemsEntity.UserItemData.UserItemsList>() }

    private var onActive: ((UserItemsEntity.UserItemData.UserItemsList) -> Unit)? = null
    fun onActiveCallback(it: (UserItemsEntity.UserItemData.UserItemsList) -> Unit) {
        onActive = it
    }

    private fun updateItem(item: UserItemsEntity.UserItemData.UserItemsList) {

        items.indexOfFirst {
            it.active
        }.apply {
            if (this != -1) {
                items[this] = items[this].also {
                    it.active = false
                    // callback
                    notifyItemChanged(this)
                }
            }
        }

        items.indexOfFirst {
            it.id == item.id
        }.apply {
            if (this != -1) {
                items[this] = items[this].also {
                    it.active = true
                    // callback
                    notifyItemChanged(this)
                }
            }
        }
    }

    fun modifierItem(newItem: List<UserItemsEntity.UserItemData.UserItemsList>) {
        val result = DiffUtil.calculateDiff(EditAvatarDiffUtil(oldItem = items, newItem = newItem))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }


    class EditAvatarDiffUtil(
        private val oldItem: List<UserItemsEntity.UserItemData.UserItemsList>,
        private val newItem: List<UserItemsEntity.UserItemData.UserItemsList>
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