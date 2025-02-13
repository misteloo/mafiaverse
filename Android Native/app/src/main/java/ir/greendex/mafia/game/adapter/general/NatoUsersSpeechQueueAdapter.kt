package ir.greendex.mafia.game.adapter.general

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerRvItemUsersSpeechQueueBinding
import ir.greendex.mafia.entity.game.general.InGameTurnSpeechEntity
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.util.BASE_URL

import javax.inject.Inject

class NatoUsersSpeechQueueAdapter @Inject constructor(private val context: Context) :
    RecyclerView.Adapter<NatoUsersSpeechQueueAdapter.UserSpeechQueueVH>() {
    inner class UserSpeechQueueVH(val binding: LayerRvItemUsersSpeechQueueBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(
            details: InGameTurnSpeechEntity.InGameTurnSpeechQueueData,
            user: InGameUsersDataEntity.InGameUserData
        ) {
            binding.apply {
                val textColor = when (details.speechStatus) {
                    "introduction" -> ContextCompat.getColor(context, R.color.white)
                    "turn" -> ContextCompat.getColor(context, R.color.green800)
                    "defence" -> ContextCompat.getColor(context, R.color.red500)
                    "challenge" -> ContextCompat.getColor(context, R.color.orange)
                    else -> ContextCompat.getColor(context, R.color.red500)
                }
                val text = when (details.speechStatus) {
                    "introduction" -> "معرفی"
                    "turn" -> "نوبت"
                    "defence" -> "دفاعیه"
                    "challenge" -> "چالش"
                    else -> "سخن آخر"
                }
                txtUserIndex.text = (details.userIndex + 1).toString()
                txtUserName.text = user.userName
                txtSpeechType.apply {
                    setTextColor(textColor)
                    this.text = text
                }
                imgUser.load(BASE_URL.plus(user.userImage))
                fabChallengeStatus.backgroundTintList = when (details.challengeUsed) {
                    true -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red500))
                    false -> ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.green800
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSpeechQueueVH {
        return UserSpeechQueueVH(
            LayerRvItemUsersSpeechQueueBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: UserSpeechQueueVH, position: Int) {
        val item = items[position]
        holder.bind(item, users[position])
    }

    private val items by lazy { mutableListOf<InGameTurnSpeechEntity.InGameTurnSpeechQueueData>() }
    private val users by lazy { mutableListOf<InGameUsersDataEntity.InGameUserData>() }

    fun addItem(
        newItem: MutableList<InGameTurnSpeechEntity.InGameTurnSpeechQueueData>,
        users: MutableList<InGameUsersDataEntity.InGameUserData>
    ) {
        this.users.clear()
        this.users.addAll(users)
        val res = DiffUtil.calculateDiff(UsersSpeechQueueDiffUtil(items, newItem))
        res.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItem)
    }

    class UsersSpeechQueueDiffUtil(
        private val oldItem: MutableList<InGameTurnSpeechEntity.InGameTurnSpeechQueueData>?,
        private val newItem: MutableList<InGameTurnSpeechEntity.InGameTurnSpeechQueueData>?
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItem?.size ?: 0

        override fun getNewListSize() = newItem?.size ?: 0

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition].userId == newItem!![newItemPosition].userId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition] == newItem!![newItemPosition]
        }
    }
}
