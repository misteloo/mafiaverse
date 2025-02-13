package ir.greendex.mafia.ui.local_game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentQrCodeScannerBinding
import ir.greendex.mafia.util.base.BaseFragment
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QrCodeScannerFragment : BaseFragment() {
    private var _binding: FragmentQrCodeScannerBinding? = null
    private val binding get() = _binding
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQrCodeScannerBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            // back
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }
            codeScanner = CodeScanner(requireContext(), codeFrame)
            codeScanner.camera = CodeScanner.CAMERA_BACK
            codeScanner.decodeCallback = DecodeCallback {
                lifecycleScope.launch {
                    val index = (it.text.toString().indexOf('?') + 1)
                    val gameId = it.text.toString().substring(index, it.text.length)

                    findNavController().previousBackStackEntry?.savedStateHandle?.set("game_id",gameId.substring(8,gameId.length))
                    findNavController().popBackStack()
                }
            }


            codeScanner.errorCallback = ErrorCallback {
                lifecycleScope.launch {
                    val snack = Snackbar.make(
                        binding!!.root,
                        "خطا در اسکن دوباره تلاش کنید",
                        Snackbar.LENGTH_LONG
                    )
                    ViewCompat.setLayoutDirection(snack.view, ViewCompat.LAYOUT_DIRECTION_RTL)
                    snack.show()
                    vibrate.longVibrate()
                }
            }

            codeFrame.setOnClickListener {
                codeScanner.startPreview()
            }
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onResume() {
        codeScanner.startPreview()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}