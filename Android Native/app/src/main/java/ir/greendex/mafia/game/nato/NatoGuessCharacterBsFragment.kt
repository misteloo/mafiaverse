package ir.greendex.mafia.game.nato

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import coil.load
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoGuessCharacterBsBinding
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.nato.NatoCharacters
import ir.greendex.mafia.game.adapter.nato.NatoGuessCharacterAdapter
import ir.greendex.mafia.util.BASE_URL

@AndroidEntryPoint
class NatoGuessCharacterBsFragment(
    private val user: InGameUsersDataEntity.InGameUserData
) : BottomSheetDialogFragment() {
    private var _binding: FragmentNatoGuessCharacterBsBinding? = null
    private val binding get() = _binding
    private lateinit var countTimer: CountDownTimer
    private lateinit var selectedCharacter: NatoCharacters

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNatoGuessCharacterBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED
        // initViews
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            imgUser.load(BASE_URL.plus(user.userImage))
            txtUserName.text = user.userName

            val list = mutableListOf(
                NatoCharacters.DETECTIVE,
                NatoCharacters.GUARD,
                NatoCharacters.COMMANDO,
                NatoCharacters.RIFLEMAN,
                NatoCharacters.DOCTOR
            )
            val adapter = NatoGuessCharacterAdapter(items = list)
            rv.layoutManager = FlexboxLayoutManager(context).apply {
                justifyContent = JustifyContent.SPACE_AROUND
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
            }
            rv.adapter = adapter
            adapter.onCharacterSelectedCallback {
                Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                selectedCharacter = it
                // show btn
                btnConfirm.also { btn ->
                    btn.visibility = View.VISIBLE
                    btn.isEnabled = true
                }
                when (it) {
                    NatoCharacters.RIFLEMAN -> imgGuess.load(R.drawable.rifileman)
                    NatoCharacters.DOCTOR -> imgGuess.load(R.drawable.doctor)
                    NatoCharacters.GUARD -> imgGuess.load(R.drawable.guard)
                    NatoCharacters.COMMANDO -> imgGuess.load(R.drawable.commando)
                    NatoCharacters.DETECTIVE -> imgGuess.load(R.drawable.detective)
                    else -> {}
                }
            }
            // start timer
            countTimer = object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progress.progressMax = (millisUntilFinished / 1000L).toFloat()
                }

                override fun onFinish() {
                    dismiss()
                    onGuessCharacter?.let {
                        it(user, null, true)
                    }
                }
            }.start()

            // on confirm
            btnConfirm.setOnClickListener {
                onGuessCharacter?.let {
                    it(user, selectedCharacter, false)
                }
                countTimer.cancel()
                dismiss()
            }
        }
    }

    private var onGuessCharacter: ((user: InGameUsersDataEntity.InGameUserData, guessCharacter: NatoCharacters?, timeUp: Boolean) -> Unit)? =
        null

    fun onGuessCharacterCallback(callback: (user: InGameUsersDataEntity.InGameUserData, guessCharacter: NatoCharacters?, timeUp: Boolean) -> Unit) {
        onGuessCharacter = callback
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        countTimer.cancel()
    }
}