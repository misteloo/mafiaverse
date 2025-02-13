package ir.greendex.mafia.game.nato

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoRiffleManBsBinding
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.util.BASE_URL

@AndroidEntryPoint
class NatoRiffleManBsFragment(
    private val fighterGunEnable: Boolean,
    private val user: InGameUsersDataEntity.InGameUserData
) : BottomSheetDialogFragment() {
    private var _binding: FragmentNatoRiffleManBsBinding? = null
    private val binding get() = _binding
    private var gunKind = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNatoRiffleManBsBinding.inflate(layoutInflater)
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
            // close
            fabClose.setOnClickListener {
                dismiss()
            }
            imgUser.load(BASE_URL.plus(user.userImage))
            txtUserName.text = user.userName
            cardFighterGun.isClickable = fighterGunEnable

            cardFighterGun.setOnClickListener {
                if (user.userId == NatoFragment.myUserId){
                    Toast.makeText(
                        context,
                        "شما نمیتوانید به خودتان تیر جنگی بدهید",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                cardWatterGun.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey500))
                cardFighterGun.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red500))
                gunKind = "fighter"
                btnConfirm.also {
                    it.visibility = View.VISIBLE
                    it.isEnabled = true
                }
            }

            cardWatterGun.setOnClickListener {
                cardWatterGun.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red500))
                cardFighterGun.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey500))
                gunKind = "lighter"
                btnConfirm.also {
                    it.visibility = View.VISIBLE
                    it.isEnabled = true
                }
            }

            btnConfirm.setOnClickListener {
                onGivenGun?.let {
                    it(user , gunKind)
                }
                dismiss()
            }


            
        }
    }
    private var onGivenGun:((user:InGameUsersDataEntity.InGameUserData , gun :String)->Unit)?=null
    fun onGivenGunCallback(it:(user:InGameUsersDataEntity.InGameUserData , gun :String)->Unit){
        onGivenGun = it
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}