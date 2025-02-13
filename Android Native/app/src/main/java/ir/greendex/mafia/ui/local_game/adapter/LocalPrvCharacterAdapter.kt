package ir.greendex.mafia.ui.local_game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemDeckBinding
import ir.greendex.mafia.entity.local.LocalCharacterEntity
import ir.greendex.mafia.util.BASE_URL

class LocalPrvCharacterAdapter(private val items: List<LocalCharacterEntity.LocalCharacterDeck>) :
    RecyclerView.Adapter<LocalPrvCharacterAdapter.LocalPrvCharacterVH>() {
    class LocalPrvCharacterVH(val binding: LayerRvItemDeckBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocalCharacterEntity.LocalCharacterDeck) {
            binding.apply {
                imgCharacter.load(BASE_URL.plus(item.icon)) {
                    crossfade(true)
                    crossfade(500)
                }

                when (item.side) {
                    "citizen" -> imgSide.load(R.drawable.citizen_hat) {
                        crossfade(true)
                        crossfade(500)
                    }

                    "mafia" -> imgSide.load(R.drawable.mafia_hat) {
                        crossfade(true)
                        crossfade(500)
                    }

                    "solo" -> imgSide.load(R.drawable.solo_hat) {
                        crossfade(true)
                        crossfade(500)
                    }
                }

                txtCharacter.text = item.name
                txtCount.text = item.count.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalPrvCharacterVH {
        return LocalPrvCharacterVH(
            LayerRvItemDeckBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: LocalPrvCharacterVH, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}