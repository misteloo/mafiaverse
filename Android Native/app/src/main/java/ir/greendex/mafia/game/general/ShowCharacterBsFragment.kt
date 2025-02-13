package ir.greendex.mafia.game.general

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentShowCharacterBsBinding
import ir.greendex.mafia.entity.game.general.enum_cls.ScenariosEnum

@AndroidEntryPoint
class ShowCharacterBsFragment(
    private val character: String,
    private val scenariosEnum: ScenariosEnum
) :
    BottomSheetDialogFragment() {
    private var _binding: FragmentShowCharacterBsBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowCharacterBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get scenario
        getScenario()

        // initView
        initViews()
    }

    private fun getScenario() {

    }

    private fun initViews() {
        binding?.apply {
            when (scenariosEnum) {
                ScenariosEnum.NATO -> when (character) {
                    "citizen" -> {
                        imgCharacter.load(R.drawable.citizen)
                        txtCharacter.text = "شهروند"
                    }

                    "doctor" -> {
                        imgCharacter.load(R.drawable.doctor)
                        txtCharacter.text = "دکتر"
                    }

                    "detective" -> {
                        imgCharacter.load(R.drawable.detective)
                        txtCharacter.text = "کاراگاه"
                    }

                    "commando" -> {
                        imgCharacter.load(R.drawable.commando)
                        txtCharacter.text = "تکاور"
                    }

                    "rifleman" -> {
                        imgCharacter.load(R.drawable.rifileman)
                        txtCharacter.text = "تفنگدار"
                    }

                    "guard" -> {
                        imgCharacter.load(R.drawable.guard)
                        txtCharacter.text = "نگهبان"
                    }

                    "nato" -> {
                        imgCharacter.load(R.drawable.nato)
                        txtCharacter.text = "ناتو"
                    }

                    "godfather" -> {
                        imgCharacter.load(R.drawable.godfather)
                        txtCharacter.text = "پدرخوانده"
                    }

                    "hostage_taker" -> {
                        imgCharacter.load(R.drawable.hostage_taker)
                        txtCharacter.text = "گروگانگیر"
                    }
                }
            }
        }

        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding?.apply {
                    txtCounter.text =
                        "شروع بازی تا".plus(" ").plus((millisUntilFinished / 1000L)).plus(" دیگر ")
                }
            }

            override fun onFinish() {
                callback?.let {
                    it()
                }
                dismiss()
            }

        }.start()
    }

    private var callback: (() -> Unit)? = null
    fun callbackNavigation(it: () -> Unit) {
        callback = it
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}