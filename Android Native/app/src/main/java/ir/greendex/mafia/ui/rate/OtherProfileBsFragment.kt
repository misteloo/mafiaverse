package ir.greendex.mafia.ui.rate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.greendex.mafia.databinding.FragmentOtherProfileBsBinding
import ir.greendex.mafia.entity.rate.OtherProfileEntity
import ir.greendex.mafia.util.BASE_URL
import java.text.NumberFormat


class OtherProfileBsFragment(private val profile: OtherProfileEntity.OtherProfileData) :
    BottomSheetDialogFragment() {
    private var _binding: FragmentOtherProfileBsBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtherProfileBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED

        // view
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            // close
            fabClose.setOnClickListener {
                dismiss()
            }
            // name
            txtUsername.text = profile.detail.name
            // avatar and anim
            img.load(BASE_URL.plus(profile.detail.assets.avatar))
            anim.setAnimationFromUrl(BASE_URL.plus(profile.detail.assets.anim))

            // total
            txtTotalWin.text = profile.detail.points.win.toString()
            txtTotalLose.text = profile.detail.points.lose.toString()

            // percentages

            // mafia win
            val mafiaWinPercentage = calPercentage(
                total = profile.detail.gamesResult.gameAsMafia,
                win = profile.detail.gamesResult.winAsMafia
            )
            // citizen win
            val citizenWinPercentage = calPercentage(
                total = profile.detail.gamesResult.gameAsCitizen,
                win = profile.detail.gamesResult.winAsCitizen
            )
            // progress win
            progressMafiaWin.progress = mafiaWinPercentage
            progressCitizenWin.progress = citizenWinPercentage

            // txt win
            // mafia
            txtMafiaWin.text = decimalFormatter().format(mafiaWinPercentage)
            // citizen
            txtCitizenWin.text = decimalFormatter().format(citizenWinPercentage)

            // reports last 25 game
            txtAbandon.text = profile.detail.points.abandon.toString()
            txtComReport.text = profile.detail.points.comReport.toString()
            txtRoleReport.text = profile.detail.points.roleReport.toString()
        }
    }

    private fun decimalFormatter():NumberFormat{
        val formatter = NumberFormat.getNumberInstance()
        formatter.minimumFractionDigits = 1
        formatter.maximumFractionDigits = 1
        return formatter
    }
    private fun calPercentage(total: Int, win: Int): Float {

        return if(win * 100f > 0f) (win * 100f) / total.toFloat() else 0f
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}