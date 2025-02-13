package ir.greendex.mafia.ui.local_game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.greendex.mafia.databinding.LayerRvItemLocalJoinedUserBinding
import ir.greendex.mafia.entity.local.LocalUsersJoinedEntity
import javax.inject.Inject

class LocalShowRoleAdapter @Inject constructor() :
    RecyclerView.Adapter<LocalShowRoleAdapter.LocalShowRoleVH>() {

    class LocalShowRoleVH(val binding: LayerRvItemLocalJoinedUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LocalUsersJoinedEntity.LocalJoinedUsersDetail) {
            binding.apply {
                txtUsername.text = item.userName
                txtRole.text = item.userRole
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalShowRoleVH {
        return LocalShowRoleVH(
            LayerRvItemLocalJoinedUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: LocalShowRoleVH, position: Int) {
        val item = items[position]
        holder.bind(item = item)
    }

    private val items by lazy { mutableListOf<LocalUsersJoinedEntity.LocalJoinedUsersDetail>() }


    fun modifierItem(newItem: List<LocalUsersJoinedEntity.LocalJoinedUsersDetail>) {
        val res = DiffUtil.calculateDiff(LocalShowRoleDifUtil(old = items, new = newItem))
        res.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class LocalShowRoleDifUtil(
        private val old: List<LocalUsersJoinedEntity.LocalJoinedUsersDetail>,
        private val new: List<LocalUsersJoinedEntity.LocalJoinedUsersDetail>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition].userId == new[newItemPosition].userId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }

    }
}