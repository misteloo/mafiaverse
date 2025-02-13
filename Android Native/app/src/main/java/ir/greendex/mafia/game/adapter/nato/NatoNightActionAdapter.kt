package ir.greendex.mafia.game.adapter.nato

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.greendex.mafia.databinding.RvItemNightActionBinding
import ir.greendex.mafia.entity.game.nato.NatoCharacters
import ir.greendex.mafia.entity.game.nato.NatoInGameNightUsers
import ir.greendex.mafia.util.BASE_URL
import javax.inject.Inject

class NatoNightActionAdapter @Inject constructor() :
    RecyclerView.Adapter<NatoNightActionAdapter.NatoNightActionVH>() {
    class NatoNightActionVH(val binding: RvItemNightActionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: NatoInGameNightUsers) {
            binding.apply {
                imgUser.load(BASE_URL.plus(user.userImage))
                if (user.natoAct) {
                    txtUsername.text = when (user.guessCharacter) {
                        NatoCharacters.GUARD -> "نگهبان"
                        NatoCharacters.DETECTIVE -> "کاراگاه"
                        NatoCharacters.DOCTOR -> "دکتر"
                        NatoCharacters.COMMANDO -> "تکاور"
                        NatoCharacters.RIFLEMAN -> "تفنگدار"
                        else -> ""
                    }
                    return
                }
                if (user.hasGun) {
                    if (user.gunKind == "fighter") txtUsername.text =
                        "تیر جنگی" else txtUsername.text = "تیر مشقی"
                } else txtUsername.text = user.userName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NatoNightActionVH {
        return NatoNightActionVH(
            RvItemNightActionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: NatoNightActionVH, position: Int) {
        val item = users[position]
        holder.binding.imgDelete.setOnClickListener {
            onUserDelete?.let {
                it(item)
            }
        }
        holder.bind(user = item)
    }

    private val users by lazy { mutableListOf<NatoInGameNightUsers>() }
    private var onUserDelete: ((NatoInGameNightUsers) -> Unit)? = null
    fun onUserDeleteCallback(callbackUser: (NatoInGameNightUsers) -> Unit) {
        onUserDelete = callbackUser
    }

    private var usersInList: ((Int) -> Unit)? = null
    fun usersInListCallback(it: (Int) -> Unit) {
        usersInList = it
    }


    fun modifierUsers(newUser: MutableList<NatoInGameNightUsers>) {
        val result = DiffUtil.calculateDiff(NatoNightActionDiffUtil(users, newUser))
        result.dispatchUpdatesTo(this)
        users.clear()
        users.addAll(newUser)

        // callback user size
        usersInList?.let {
            it(users.size)
        }
    }

    class NatoNightActionDiffUtil(
        private val oldItem: MutableList<NatoInGameNightUsers>?,
        private val newItem: MutableList<NatoInGameNightUsers>?
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newItem?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition].userId == newItem!![newItemPosition].userId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem!![oldItemPosition] == newItem!![newItemPosition]
        }
    }
}