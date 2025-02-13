package ir.greendex.mafia.game.adapter.nato

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.databinding.LayerRvItemModeratorLogContentBinding
import ir.greendex.mafia.databinding.LayerRvItemModeratorLogSubUsersBinding
import ir.greendex.mafia.databinding.LayerRvItemServerMessageBinding
import ir.greendex.mafia.entity.game.general.ModeratorLogEntity
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class ModeratorLogAdapter @Inject constructor(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val SERVER = 1
        const val EVENT = 2
    }

    class ModeratorServerLog(val binding: LayerRvItemServerMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ModeratorLogEntity.ModeratorLogData) {
            binding.apply {
                txtMsg.text = data.headerMsg
            }
        }
    }

    inner class ModeratorEventLog(val binding: LayerRvItemModeratorLogContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ModeratorLogEntity.ModeratorLogData) {
            binding.apply {
                val from = data.content.from
                val to = data.content.to
                // from part
                imgFrom.load(BASE_URL.plus(from.userImage))
                txtUsername.text = from.userName
                txtRole.text = from.character

                // to part
                rv.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager
                        .VERTICAL, false
                )
                rv.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
                rv.adapter = ModeratorLogEventAdapter(items = to)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].msgType == "server") SERVER else EVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SERVER -> {
                ModeratorServerLog(
                    LayerRvItemServerMessageBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            EVENT -> {
                ModeratorEventLog(
                    LayerRvItemModeratorLogContentBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            else -> {
                ModeratorServerLog(
                    LayerRvItemServerMessageBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder.itemViewType) {
            SERVER -> {
                (holder as ModeratorServerLog).apply {
                    bind(data = item)
                }
            }

            EVENT -> {
                (holder as ModeratorEventLog).apply {
                    bind(data = item)
                }
            }
        }
    }

    private val items by lazy { mutableListOf<ModeratorLogEntity.ModeratorLogData>() }

    fun modifierItem(newList: List<ModeratorLogEntity.ModeratorLogData>) {
        val res = DiffUtil.calculateDiff(ModeratorLogDiffUtil(newItem = newList, oldItem = items))
        res.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newList)
    }

    class ModeratorLogDiffUtil(
        private val oldItem: List<ModeratorLogEntity.ModeratorLogData>,
        private val newItem: List<ModeratorLogEntity.ModeratorLogData>
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


    class ModeratorLogEventAdapter(private val items: List<ModeratorLogEntity.ModeratorLogData.ModeratorLogContent.ModeratorLogContentUserData>) :
        RecyclerView.Adapter<ModeratorLogEventAdapter.ModeratorLogEventVH>() {
        class ModeratorLogEventVH(val binding: LayerRvItemModeratorLogSubUsersBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(item: ModeratorLogEntity.ModeratorLogData.ModeratorLogContent.ModeratorLogContentUserData) {
                binding.apply {
                    imgUser.load(BASE_URL.plus(item.userImage))
                    txtUserIndex.text = item.userIndex.plus(1).toString()
                    txtUsername.text = item.userName
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModeratorLogEventVH {
            return ModeratorLogEventVH(
                LayerRvItemModeratorLogSubUsersBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: ModeratorLogEventVH, position: Int) {
            val item = items[position]
            holder.bind(item)
        }
    }

}