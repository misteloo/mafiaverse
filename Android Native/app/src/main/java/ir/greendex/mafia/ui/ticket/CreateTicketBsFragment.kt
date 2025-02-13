package ir.greendex.mafia.ui.ticket

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentCreateTicketBsBinding

@AndroidEntryPoint
class CreateTicketBsFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCreateTicketBsBinding? = null
    private val binding get() = _binding

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogThemeNoFloating
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateTicketBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            // focus color
            edtMsg.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerContent.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerContent.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                } else {
                    layerContent.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerContent.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}