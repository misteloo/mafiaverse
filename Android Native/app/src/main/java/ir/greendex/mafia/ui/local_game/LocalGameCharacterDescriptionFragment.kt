package ir.greendex.mafia.ui.local_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentLocalGameCharacterDescriptionBinding
import ir.greendex.mafia.entity.local.LocalSelectDeckEntity
import ir.greendex.mafia.util.BASE_URL


class LocalGameCharacterDescriptionFragment(
    private val character: LocalSelectDeckEntity
) : BottomSheetDialogFragment() {
    private var _binding: FragmentLocalGameCharacterDescriptionBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocalGameCharacterDescriptionBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED
        // init
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            // close
            imgClose.setOnClickListener {
                dismiss()
            }

            txtCharacter.text = character.name
            imgCharacter.load(BASE_URL.plus(character.icon))
            when (character.side) {
                "citizen" -> {
                    imgSide.load(R.drawable.citizen_hat)
                    txtSide.text = "شهر"
                }
                "mafia" -> {
                    imgSide.load(R.drawable.mafia_hat)
                    txtSide.text = "مافیا"
                }
                "solo" -> {
                    imgSide.load(R.drawable.solo_hat)
                    txtSide.text = "مستقل"
                }
            }
            txtDesc.text = character.description
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}