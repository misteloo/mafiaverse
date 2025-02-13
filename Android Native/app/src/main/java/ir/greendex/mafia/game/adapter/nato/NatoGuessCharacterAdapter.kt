package ir.greendex.mafia.game.adapter.nato

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvNatoGuessCharacterBinding
import ir.greendex.mafia.entity.game.nato.NatoCharacters

class NatoGuessCharacterAdapter(private val items: MutableList<NatoCharacters>) :
    RecyclerView.Adapter<NatoGuessCharacterAdapter.NatoGuessCharacterVH>() {

    class NatoGuessCharacterVH(val binding: LayerRvNatoGuessCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NatoCharacters) {
            binding.apply {
                when (item) {
                    NatoCharacters.DOCTOR -> {
                        imgCharacter.load(R.drawable.doctor)
                        txtCharacter.text = "دکتر"
                    }

                    NatoCharacters.RIFLEMAN -> {
                        imgCharacter.load(R.drawable.rifileman)
                        txtCharacter.text = "تفنگدار"
                    }

                    NatoCharacters.COMMANDO -> {
                        imgCharacter.load(R.drawable.commando)
                        txtCharacter.text = "تکاور"
                    }

                    NatoCharacters.DETECTIVE -> {
                        imgCharacter.load(R.drawable.detective)
                        txtCharacter.text = "کاراگاه"
                    }

                    NatoCharacters.GUARD -> {
                        imgCharacter.load(R.drawable.guard)
                        txtCharacter.text = "نگهبان"
                    }

                    else -> {
                        imgCharacter.load(R.drawable.citizen)
                        txtCharacter.text = "شهروند"
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NatoGuessCharacterVH {
        return NatoGuessCharacterVH(
            LayerRvNatoGuessCharacterBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: NatoGuessCharacterVH, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.binding.root.setOnClickListener {
            onCharacterSelected?.let {
                it(item)
            }
        }
    }

    private var onCharacterSelected: ((NatoCharacters) -> Unit)? = null
    fun onCharacterSelectedCallback(callback: (NatoCharacters) -> Unit) {
        onCharacterSelected = callback
    }

}