package ir.greendex.mafia.game.nato

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoNightIdleBinding
import jp.wasabeef.glide.transformations.BlurTransformation

@AndroidEntryPoint
class NatoNightIdleFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentNatoNightIdleBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNatoNightIdleBinding.inflate(layoutInflater)
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

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}