package ir.greendex.mafia.game.adapter.general

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemNatoMafiaVisitationBinding
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.nato.NatoMafiaVisitationEntity
import ir.greendex.mafia.entity.game.nato.NatoCharacters
import ir.greendex.mafia.util.BASE_URL

class NatoMafiaVisitationAdapter(
    private val users: List<InGameUsersDataEntity.InGameUserData>,
    private val mafia: List<NatoMafiaVisitationEntity>
) :
    RecyclerView.Adapter<NatoMafiaVisitationAdapter.NatoMafiaVisitationVH>() {
    inner class NatoMafiaVisitationVH(val binding: LayerRvItemNatoMafiaVisitationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: NatoMafiaVisitationEntity) {
            binding.apply {
                users.find { find ->
                    item.userId == find.userId
                }?.let { let ->
                    imgUser.load(BASE_URL.plus(let.userImage))
                    txtUserIndex.text = (let.userIndex + 1).toString()
                    txtUsername.text = let.userName
                    when (item.role) {
                        NatoCharacters.NATO -> {
                            imgCharacter.load(R.drawable.nato)
                            txtCharacter.text = "ناتو"
                        }

                        NatoCharacters.GODFATHER -> {
                            imgCharacter.load(R.drawable.godfather)
                            txtCharacter.text = "پدرخوانده"
                        }

                        NatoCharacters.HOSTAGE_TAKER -> {
                            imgCharacter.load(R.drawable.hostage_taker)
                            txtCharacter.text = "گروگانگیر"
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NatoMafiaVisitationVH {
        return NatoMafiaVisitationVH(
            LayerRvItemNatoMafiaVisitationBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount() = mafia.size

    override fun onBindViewHolder(holder: NatoMafiaVisitationVH, position: Int) {
        val mafia = mafia[position]
        holder.bind(item =  mafia)
    }
}