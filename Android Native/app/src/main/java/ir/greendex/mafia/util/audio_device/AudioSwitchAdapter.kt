package ir.greendex.mafia.util.audio_device

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemAudioSwitchItemBinding
import ir.greendex.mafia.entity.audio_switch.AudioSwitchEntity

class AudioSwitchAdapter(private val context: Context) :
    RecyclerView.Adapter<AudioSwitchAdapter.AudioSwitchVH>() {
    inner class AudioSwitchVH(val binding: LayerRvItemAudioSwitchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AudioSwitchEntity) {
            binding.apply {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioSwitchVH {
        return AudioSwitchVH(
            LayerRvItemAudioSwitchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: AudioSwitchVH, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.binding.apply {
            card.setOnClickListener {
                // disable other options
                items.forEach {
                    it.selected = false
                }
                // selected
                item.selected = true
                // bind to rv
                modifierItem(newItem = items)
                // callback
                onSelect?.let {
                    it(item)
                }
            }
        }
    }

    private var onSelect: ((AudioSwitchEntity) -> Unit)? = null
    fun onSelectCallback(it: (AudioSwitchEntity) -> Unit) {
        onSelect = it
    }

    private val items by lazy { mutableListOf<AudioSwitchEntity>() }
    fun modifierItem(newItem: List<AudioSwitchEntity>) {
        val res = DiffUtil.calculateDiff(AudioSwitchDiffUtil(oldItem = items, newItem = newItem))
        res.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class AudioSwitchDiffUtil(
        private val oldItem: List<AudioSwitchEntity>,
        private val newItem: List<AudioSwitchEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItem.size

        override fun getNewListSize() = newItem.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].audioDevice == newItem[newItemPosition].audioDevice
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] == newItem[newItemPosition]
        }

    }
}