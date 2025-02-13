package ir.greendex.mafia.util.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.FragmentInGameClickedOnUserBsBinding

@AndroidEntryPoint
class InGameClickedOnUserFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentInGameClickedOnUserBsBinding? = null
    private val binding get() = _binding
    private lateinit var callback: InGameClickedOnUserListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInGameClickedOnUserBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            // add friend
            cardFriendRequest.setOnClickListener {
                callback.onMenuItemClicked(InGameClickedOnUserEnum.ADD_FRIEND)
                dismiss()
            }
            // report role spoiling
            cardReportRoleSpoiling.setOnClickListener {
                callback.onMenuItemClicked(InGameClickedOnUserEnum.ROLE_SPOILING)
                dismiss()
            }
            // report swearing
            cardReportSwearing.setOnClickListener {
                callback.onMenuItemClicked(InGameClickedOnUserEnum.SWEARING)
                dismiss()
            }
        }
    }

    fun onItemClicked(callback: InGameClickedOnUserListener) {
        this.callback = callback
    }
}