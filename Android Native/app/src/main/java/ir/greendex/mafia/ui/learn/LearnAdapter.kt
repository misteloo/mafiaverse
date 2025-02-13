package ir.greendex.mafia.ui.learn

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemLearnBinding
import ir.greendex.mafia.entity.learn.LearnEntity

class LearnAdapter(private val items: List<LearnEntity>) :
    RecyclerView.Adapter<LearnAdapter.LearnVH>() {

    class LearnVH(val binding: LayerRvItemLearnBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LearnEntity) {
            binding.apply {
                imgCharacter.load(item.img)
                txtCharacter.text = item.name
                txtContent.text = item.content
                if (item.isMafia) imgSide.load(R.drawable.mafia_hat) else imgSide.load(R.drawable.citizen_hat)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnVH {
        return LearnVH(
            LayerRvItemLearnBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: LearnVH, position: Int) {
        val item = items[position]
        alphaAnim(holder.binding.root)
        holder.bind(item)
    }

    private fun alphaAnim(view: View) {
        AlphaAnimation(0f, 1f).apply {
            this.duration = 500
            view.startAnimation(this)
        }
    }
}