package ir.greendex.mafia.game.general

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentInGameReportBsBinding
import ir.greendex.mafia.entity.game.general.enum_cls.InGameReportTypeEnum
import ir.greendex.mafia.util.sound.SoundManager
import javax.inject.Inject

@AndroidEntryPoint
class InGameReportBsFragment(
    private val verifiedGame: Boolean
) : BottomSheetDialogFragment() {

    private var _binding: FragmentInGameReportBsBinding? = null
    private val binding get() = _binding

    // injection
    @Inject
    lateinit var soundManager: SoundManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInGameReportBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            // change style
            cardAge.setOnClickListener {
                if (!verifiedGame) {
                    Toast.makeText(
                        context,
                        "فقط برای بازی های احراز شده",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                changeStyle(type = InGameReportTypeEnum.AGE)
            }

            cardRole.setOnClickListener {
                changeStyle(type = InGameReportTypeEnum.SPOILING)
            }

            cardSwearing.setOnClickListener {
                changeStyle(type = InGameReportTypeEnum.SWEARING)
            }

            // report
            btnConfirm.setOnClickListener {
                soundManager.reportSound()
            }
        }
    }

    private fun changeStyle(type: InGameReportTypeEnum) {
        binding?.apply {
            btnConfirm.isEnabled = true
            btnConfirm.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red500))
            when (type) {
                InGameReportTypeEnum.AGE -> {
                    linearAge.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red500
                        )
                    )
                    txtAge.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    imgAge.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
                    // other
                    // role
                    linearRole.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    txtRole.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    imgRole.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey700
                        )
                    )
                    // swear
                    linearSwearing.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    txtSwear.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    imgSwear.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey700
                        )
                    )
                }

                InGameReportTypeEnum.SPOILING -> {
                    linearRole.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red500
                        )
                    )
                    txtRole.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    imgRole.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
                    // other
                    // age
                    linearAge.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    txtAge.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    imgAge.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey700))
                    // swear
                    linearSwearing.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    txtSwear.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    imgSwear.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey700
                        )
                    )
                }

                InGameReportTypeEnum.SWEARING -> {
                    linearSwearing.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red500
                        )
                    )
                    txtSwear.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    imgSwear.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
                    // other
                    // age
                    linearAge.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    txtAge.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    imgAge.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey700))
                    // role
                    linearRole.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    txtRole.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    imgRole.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey700
                        )
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}