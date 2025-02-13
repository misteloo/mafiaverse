package ir.greendex.mafia.ui.group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemGroupDetailBinding
import ir.greendex.mafia.entity.group.GroupDetailEntity
import javax.inject.Inject

class GroupDetailAdapter @Inject constructor(
    private val context: Context
) : RecyclerView.Adapter<GroupDetailAdapter.GroupDetailVH>() {
    inner class GroupDetailVH(val binding: LayerRvItemGroupDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupDetailEntity.GroupDetailUsersData) {
            binding.apply {
                imgUser.load(R.drawable.mafia_icon)
                txtUsername.text = item.userName
                when (item.grade) {
                    "normal" -> txtGrade.text = "عضو"
                    "co" -> {
                        txtGrade.text = "کمک مدیر"
                        txtGrade.setTextColor(ContextCompat.getColor(context, R.color.orange))
                    }

                    "leader" -> {
                        txtGrade.text = "مدیر"
                        txtGrade.setTextColor(ContextCompat.getColor(context, R.color.green800))
                    }
                }
                if (isMaster) layerOption.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupDetailVH {
        return GroupDetailVH(
            LayerRvItemGroupDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GroupDetailVH, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    private val items by lazy { mutableListOf<GroupDetailEntity.GroupDetailUsersData>() }
    private var isMaster = false

    fun getMaster(isMaster: Boolean) {
        this.isMaster = isMaster
    }


    fun addItem(newItem:MutableList<GroupDetailEntity.GroupDetailUsersData>){
        val result = DiffUtil.calculateDiff(GroupDetailDiffUtil(oldItem = items,newItem = newItem))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    inner class GroupDetailDiffUtil(
        private var oldItem: MutableList<GroupDetailEntity.GroupDetailUsersData>?,
        private val newItem:MutableList<GroupDetailEntity.GroupDetailUsersData>?
    ):DiffUtil.Callback(){
        override fun getOldListSize(): Int {
            return oldItem?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newItem?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition].userName == newItem!![newItemPosition].userName
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition] == newItem!![newItemPosition]
        }

    }

}