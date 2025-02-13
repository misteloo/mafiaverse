package ir.greendex.mafia.ui.group.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ir.greendex.mafia.databinding.LayerMessageMeBinding
import ir.greendex.mafia.databinding.LayerMessageOtherBinding
import ir.greendex.mafia.databinding.LayerServerMessageInGroupBinding
import ir.greendex.mafia.entity.channel.ChannelMessageEntity
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

class GroupAdapter @Inject constructor() : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val MY_MESSAGE = 1
        private const val OTHER_MESSAGE = 2
        private const val SERVER_MESSAGE = 3
    }

    private lateinit var userId: String
    private lateinit var myMessageLayer: LayerMessageMeBinding
    private lateinit var otherMessageLayer: LayerMessageOtherBinding
    private lateinit var serverMessageLayer: LayerServerMessageInGroupBinding

    inner class OtherMessageVH(val binding: LayerMessageOtherBinding) : ViewHolder(binding.root) {
        init {
            otherMessageLayer = binding
        }

        fun bind(item: ChannelMessageEntity.ChannelMessageData) {

            binding.apply {
                val time = PersianDate(item.messageTime)
                txtMessage.text = item.message
                val min = if (time.minute < 10) "0".plus(time.minute) else time.minute.toString()
                val hour = if (time.hour < 10) "0".plus(time.hour) else time.hour.toString()
                txtTime.text = hour.plus(" : ").plus(min)
                txtUsername.text = item.userName
            }
        }
    }

    inner class MyMessageVH(val binding: LayerMessageMeBinding) : ViewHolder(binding.root) {
        init {
            myMessageLayer = binding
        }

        fun bind(item: ChannelMessageEntity.ChannelMessageData) {
            binding.apply {
                val time = PersianDate(item.messageTime)
                txtMessage.text = item.message
                val min = if (time.minute < 10) "0".plus(time.minute) else time.minute.toString()
                val hour = if (time.hour < 10) "0".plus(time.hour) else time.hour.toString()
                txtTime.text = min.plus(" : ").plus(hour)
                txtUsername.text = item.userName
            }
        }
    }

    inner class ServerMessageVH(val binding: LayerServerMessageInGroupBinding) :
        ViewHolder(binding.root) {
        init {
            serverMessageLayer = binding
        }

        fun bind(item: ChannelMessageEntity.ChannelMessageData) {
            binding.apply {
                txtServerMessage.text = item.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            MY_MESSAGE -> MyMessageVH(
                LayerMessageMeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            OTHER_MESSAGE -> OtherMessageVH(
                LayerMessageOtherBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            SERVER_MESSAGE -> ServerMessageVH(
                LayerServerMessageInGroupBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> MyMessageVH(
                LayerMessageMeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return if (items[position].messageType == "server") SERVER_MESSAGE
        else if (items[position].userId == this.userId) MY_MESSAGE
        else OTHER_MESSAGE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        when (holder.itemViewType) {
            MY_MESSAGE -> {
                val keep = holder as MyMessageVH
                setAlphaAnimation(keep.binding.root)
                keep.bind(item)
            }

            OTHER_MESSAGE -> {
                val keep = holder as OtherMessageVH
                setAlphaAnimation(keep.binding.root)
                keep.bind(item)
            }

            SERVER_MESSAGE -> {
                val keep = holder as ServerMessageVH
                setAlphaAnimation(keep.binding.root)
                keep.bind(item)
            }
        }
    }

    private fun setAlphaAnimation(view: View) {
        AlphaAnimation(0f, 1f).apply {
            duration = 500
            view.startAnimation(this)
        }
    }

    private val items = mutableListOf<ChannelMessageEntity.ChannelMessageData>()

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun addItem(newItem: List<ChannelMessageEntity.ChannelMessageData>) {
        val res = DiffUtil.calculateDiff(GroupDiffUtil(oldItem = items, newItem = newItem))
        res.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class GroupDiffUtil(
        private val newItem: List<ChannelMessageEntity.ChannelMessageData>?,
        private val oldItem: List<ChannelMessageEntity.ChannelMessageData>?
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newItem?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition].messageId === newItem!![newItemPosition].messageId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition].message === newItem!![newItemPosition].message
        }
    }
}