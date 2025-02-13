package ir.greendex.mafia.ui.login

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentLoginBsBinding
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading

@AndroidEntryPoint
class LoginBsFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentLoginBsBinding? = null
    private val binding get() = _binding
    private val vm: LoginVm by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED
        // initViews
        initViews()
        // set state
        vm.setState()
    }

    private fun initViews() {
        binding?.apply {
            login.setOnClickListener {
                if (edtPhone.text.toString().length < 9) {
                    Toast.makeText(context, "شماره تماس را به درستی وارد کنید", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                login.loading(progress = progress)

                // send data to server
                vm.loginRequest(
                    phone = "09".plus(edtPhone.text.toString())
                )

            }
            // focus
            edtPhone.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerPhone.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerPhone.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                }
            }

            // watcher
            edtPhone.doAfterTextChanged {
                if (it != null && it.toString().length >= 9) btnSituation(active = true)
                else btnSituation(active = false)
            }

            txtCreateAccount.setOnClickListener {
                onSignUp?.let {
                    it()
                }
                dismiss()
            }


            vm.loginCallback {
                // hide loading
                login.hideLoading(progress = progress)
                if (it != null) {
                    if (it.status) {
                        // store timer
                        vm.storeLoginTimer(timer = (System.currentTimeMillis() + 60000))
                        // store user phone
                        vm.storeUserPhone(phone = "09".plus(edtPhone.text.toString()))
                        // confirm code
                        confirmCode(phone = "09".plus(edtPhone.text.toString()))
                    } else Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                } else Toast.makeText(context, "عدم ارتباط", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun btnSituation(active: Boolean) {
        binding?.apply {
            if (active) {
                login.also {
                    it.isEnabled = true
                    it.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red500
                        )
                    )
                }
            } else {
                login.also {
                    it.isEnabled = false
                    it.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey800
                        )
                    )
                }
            }
        }
    }

    private var onSignUp: (() -> Unit)? = null
    fun onSignUpClicked(it: () -> Unit) {
        onSignUp = it
    }

    private var onRequestConfirmCode: ((String) -> Unit)? = null
    fun onRequestConfirmCodeCallback(it: (String) -> Unit) {
        onRequestConfirmCode = it
    }

    private fun confirmCode(phone: String) {
        onRequestConfirmCode?.let {
            it(phone)
        }
        dismiss()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}