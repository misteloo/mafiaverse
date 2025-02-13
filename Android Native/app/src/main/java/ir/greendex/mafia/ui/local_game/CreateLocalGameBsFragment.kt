package ir.greendex.mafia.ui.local_game

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentCreateLocalGameBsBinding

@AndroidEntryPoint
class CreateLocalGameBsFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCreateLocalGameBsBinding? = null
    private val binding get() = _binding

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogThemeNoFloating
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateLocalGameBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED

        // initView
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            // back
            btnClose.setOnClickListener {
                dismiss()
            }

            edtNumber.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerInput.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerInput.hintTextColor = ColorStateList.valueOf(
                        Color.WHITE
                    )
                }
            }

            btnCreate.setOnClickListener {
                if (edtNumber.text.toString().isNotEmpty()) {
                    onCreateLocal?.let { it(edtNumber.text.toString()) }
                    dismiss()
                    onDestroy()
                } else {
                    Toast.makeText(context, "تعداد بازیکن مشخص نشده", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private var onCreateLocal: ((String) -> Unit)? = null
    fun onCreateLocalCallback(it: (String) -> Unit) {
        onCreateLocal = it
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}