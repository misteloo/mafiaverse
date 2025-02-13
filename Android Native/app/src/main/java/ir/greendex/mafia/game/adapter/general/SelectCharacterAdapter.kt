package ir.greendex.mafia.game.adapter.general

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvSelectCharacterBinding
import ir.greendex.mafia.entity.game.general.CharactersEntity
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class SelectCharacterAdapter @Inject constructor() :
    RecyclerView.Adapter<SelectCharacterAdapter.SelectCharacterVH>() {
    class SelectCharacterVH(val binding: LayerRvSelectCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(it: CharactersEntity.CharacterData) {
            binding.apply {
                if (!it.selected){
                    imgCharacter.load(R.drawable.round_question_mark_24)
                }else imgCharacter.load(BASE_URL.plus(it.selectedBy))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCharacterVH {
        return SelectCharacterVH(
            LayerRvSelectCharacterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: SelectCharacterVH, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.binding.root.setOnClickListener {
            if (myTurnToPick) {
                if (!item.selected) {
                    selectedCharacter?.let {
                        it(item.id, item.name)
                    }
                } else {
                    selectedCharacter?.let {
                        it(null, null)
                    }
                }
            }else{
                myTurnError?.let {
                    it()
                }
            }
        }
    }

    private var selectedCharacter: ((index: Int?, character: String?) -> Unit)? = null
    private var myTurnToPick = false

    private var myTurnError: (() -> Unit)? = null
    fun myTurnErrorCallback(it: () -> Unit) {
        myTurnError = it
    }

    fun setMyTurn() {
        myTurnToPick = true
    }

    fun selectedCharacterCallback(it: (index: Int?, character: String?) -> Unit) {
        selectedCharacter = it
    }

    private val items by lazy { mutableListOf<CharactersEntity.CharacterData>() }

    fun addItem(newItem: MutableList<CharactersEntity.CharacterData>) {
        val result = DiffUtil.calculateDiff(SelectCharacterDiffUtil(items, newItem))
        result.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class SelectCharacterDiffUtil(
        private val oldItem: MutableList<CharactersEntity.CharacterData>?,
        private val newItem: MutableList<CharactersEntity.CharacterData>?
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newItem?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition].id == newItem!![newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition] == newItem!![newItemPosition]
        }

    }
}