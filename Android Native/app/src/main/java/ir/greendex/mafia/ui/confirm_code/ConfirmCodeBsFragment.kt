package ir.greendex.mafia.ui.confirm_code

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentConfirmCodeBsBinding
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading

@AndroidEntryPoint
class ConfirmCodeBsFragment(
    private val login: Boolean = false,
    private val phone: String,
    private val introducer: String?
) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentConfirmCodeBsBinding? = null
    private val binding get() = _binding
    private val vm: ConfirmCodeVm by viewModels()
    private lateinit var resendTimer: CountDownTimer

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogThemeNoFloating
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // store routing
        storeRouting()
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.CONFIRM)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirmCodeBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        // initViews
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            edtConfirmCode.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerConfirmCode.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerConfirmCode.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                } else {
                    layerConfirmCode.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerConfirmCode.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                }
            }

            btnConfirm.setOnClickListener {
                // hide btn
                btnConfirm.loading(progress = progress)
                // check inputs
                if (checkInputConfirmCode()) {
                    // send data
                    if (login) {
                        vm.loginConfirmCode(phone = phone, code = edtConfirmCode.text.toString()) {
                            if (it != null) {
                                if (it.status) {
                                    // remove sign up timer
                                    vm.removeSignUpTimer()
                                    // token
                                    MainActivity.userToken = it.data.token
                                    // dismiss
                                    dismiss()
                                    // callback
                                    onNavigationToHome?.let {
                                        it()
                                    }
                                } else Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                            } else Toast.makeText(context, "عدم ارتباط", Toast.LENGTH_SHORT).show()
                            // hide loading
                            btnConfirm.hideLoading(progress = progress)
                        }
                    } else {
                        vm.confirmCode(
                            phone = phone,
                            introducer = introducer,
                            code = edtConfirmCode.text.toString()
                        ) {
                            if (it != null) {
                                if (it.status) {
                                    // remove sign up timer
                                    vm.removeSignUpTimer()
                                    // token
                                    MainActivity.userToken = it.data.token
                                    // dismiss
                                    dismiss()
                                    // callback
                                    onNavigationToHome?.let {
                                        it()
                                    }
                                } else Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                            } else Toast.makeText(context, "عدم ارتباط", Toast.LENGTH_SHORT).show()
                            // hide loading
                            btnConfirm.hideLoading(progress = progress)
                        }
                    }
                } else {
                    Toast.makeText(context, "کد تایید را به درستی وارد کنید", Toast.LENGTH_SHORT)
                        .show()
                    // hide loading
                    btnConfirm.hideLoading(progress = progress)
                }
            }
            // resend
            btnResend.setOnClickListener {
                resendTimer.cancel()

                vm.whichPlaceShouldNavigate { login, signUp ->
                    if (login) {
                        whichPlaceShouldNavigate?.let {
                            it("login")
                        }
                    }
                    if (signUp) {
                        whichPlaceShouldNavigate?.let {
                            it("signup")
                        }
                    }
                    dismiss()
                }

            }
            // resend timer
            vm.getTimer {
                resendTimer = object : CountDownTimer(it - System.currentTimeMillis(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val timeLeft = (millisUntilFinished / 1000L)
                        btnResend.text = " ارسال دوباره ".plus(timeLeft)
                    }

                    override fun onFinish() {
                        btnResend.text = " ارسال دوباره "
                        btnResend.isEnabled = true
                        btnResend.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red500
                            )
                        )
                    }
                }.start()
            }

            // watcher
            edtConfirmCode.doAfterTextChanged {
                if ((!it.isNullOrEmpty()) && (it.length == 4)) btnConfirmSituation(active = true)
                else btnConfirmSituation(active = false)
            }
        }
    }

    private fun btnConfirmSituation(active:Boolean){
        binding?.apply {
            if (active){
                btnConfirm.also {
                    it.isEnabled = true
                    it.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.red500))
                }
            }else {
                btnConfirm.also {
                    it.isEnabled = false
                    it.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.grey800))
                }
            }
        }
    }

    private var onNavigationToHome: (() -> Unit)? = null
    fun onNavigationToHomeCallback(it: () -> Unit) {
        onNavigationToHome = it
    }

    private var whichPlaceShouldNavigate: ((String) -> Unit)? = null

    fun whichPlaceShouldNavigateCallback(it: (String) -> Unit) {
        whichPlaceShouldNavigate = it
    }

    private fun checkInputConfirmCode(): Boolean {
        binding?.apply {
            if (edtConfirmCode.text.toString().isEmpty()) return false
            if (edtConfirmCode.text.toString().length < 4) return false
        }
        return true
    }

    override fun onDestroy() {
        _binding = null
        if (::resendTimer.isInitialized) {
            resendTimer.cancel()
        }
        super.onDestroy()
    }
}