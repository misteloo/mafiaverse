package ir.greendex.mafia.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.FragmentGroupModeratorBsBinding

@AndroidEntryPoint
class GroupModeratorBsFragment(
    private val token: String,
    private val channelId: String,
    private val creatorId: String,
    private val userId:String,
    private val gameId:String
) : BottomSheetDialogFragment() {
    private var _binding: FragmentGroupModeratorBsBinding? = null
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupModeratorBsBinding.inflate(layoutInflater)
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

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}