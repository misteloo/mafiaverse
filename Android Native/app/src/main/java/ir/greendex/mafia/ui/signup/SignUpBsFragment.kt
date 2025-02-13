package ir.greendex.mafia.ui.signup

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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentSignUpBsBinding
import ir.greendex.mafia.util.dialog.DialogManager
import ir.greendex.mafia.util.extension.badWords
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading
import ir.greendex.mafia.util.extension.textChangeCallback
import ir.greendex.mafia.util.sound.SoundManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpBsFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentSignUpBsBinding? = null
    private val binding get() = _binding
    private val vm: SignUpVm by viewModels()
    private val dialogManager by lazy { DialogManager(context, soundManager) }

    // injection
    @Inject
    lateinit var soundManager: SoundManager
    override fun getTheme(): Int {
        return R.style.BottomSheetDialogThemeNoFloating
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED

        // initViews
        initViews()

        // set state
        vm.setState()
    }

    private fun initViews() {
        binding?.apply {
            txtLogin.setOnClickListener {
                onLogin?.let {
                    it()
                }
                dismiss()
            }

            edtUsername.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerUsername.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerUsername.hintTextColor = ColorStateList.valueOf(
                        Color.WHITE
                    )
                } else {
                    layerUsername.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerUsername.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                }
            }

            edtPhone.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerPhone.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerPhone.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                } else {
                    layerPhone.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerPhone.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                }
            }
            // username validation
            edtUsername.textChangeCallback(timeout = 600) {
                lifecycleScope.launch {
                    layerUsername.helperText = ""
                    layerUsername.error = ""
                    val res = checkExceptions(it)
                    if (res) {
                        layerUsername.helperText = "در حال بررسی..."
                        layerUsername.setHelperTextColor(
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.orange
                                )
                            )
                        )
                        vm.checkUsername(newName = edtUsername.text.toString()) { serverResponse ->
                            lifecycleScope.launch {
                                if (serverResponse != null) {
                                    if (serverResponse.status) {
                                        layerUsername.helperText = "این نام مجاز است"
                                        edtPhoneSituation(active = true)
                                        layerUsername.setHelperTextColor(
                                            ColorStateList.valueOf(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.green800
                                                )
                                            )
                                        )
                                    } else {
                                        edtPhoneSituation(active = false)
                                        layerUsername.helperText = serverResponse.msg
                                        layerUsername.setHelperTextColor(
                                            ColorStateList.valueOf(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.red500
                                                )
                                            )
                                        )
                                    }
                                } else {
                                    edtPhoneSituation(active = false)
                                    layerUsername.error = "عدم ارتباط"
                                }
                            }
                        }
                    } else edtPhoneSituation(active = false)
                }
            }
            // phone validation
            edtPhone.doAfterTextChanged {
                if (it != null && it.toString().length >= 9) btnContinueSituation(active = true)
                else btnContinueSituation(active = false)
            }
            btnContinue.setOnClickListener {
                // show loading
                btnContinue.loading(progress = progress)
                // send data to server
                vm.singUp(
                    username = edtUsername.text.toString(),
                    phone = "09".plus(edtPhone.text.toString()),
                    identificationCode = txtIdentification.text.toString()
                ) {
                    // hide loading
                    btnContinue.hideLoading(progress = progress)
                    if (it != null) {
                        if (it.status) {
                            // store timer
                            vm.storeSignUpTimer(timer = (System.currentTimeMillis() + 60000))
                            // store user phone
                            vm.storeUserPhone(phone = "09".plus(edtPhone.text.toString()))
                            // confirm code
                            confirmCode(phone = "09".plus(edtPhone.text.toString()))
                        } else Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(context, "عدم ارتباط", Toast.LENGTH_SHORT).show()
                }
            }

            // introduction code
            cb.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // show dialog
                    dialogManager.identificationDialog { code ->
                        if (code == null) {
                            cb.isChecked = false
                            return@identificationDialog
                        }
                        txtIdentification.also {
                            it.visibility = View.VISIBLE
                            it.text = code
                        }
                    }
                } else {
                    txtIdentification.also {
                        it.visibility = View.INVISIBLE
                        it.text = ""
                    }
                }
            }
        }
    }

    private fun edtPhoneSituation(active: Boolean) {
        binding?.apply {
            if (active) edtPhone.isEnabled = true
            else {
                edtPhone.also {
                    it.isEnabled = false
                    it.text?.clear()
                }
            }
        }
    }

    private fun btnContinueSituation(active: Boolean) {
        binding?.apply {
            if (active) {
                btnContinue.also {
                    it.isEnabled = true
                    it.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red500
                        )
                    )
                }
            } else {
                btnContinue.also {
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

    private fun checkExceptions(it: String?): Boolean {
        binding?.apply {
            return if (it.isNullOrEmpty()) {
                layerUsername.error = "نام کاربری نمیتواند خالی باشد"
                false
            } else if (it.badWords()) {
                layerUsername.error = "این نام کاربری مجاز نیست"
                false
            } else if (it.length<5) {
                layerUsername.error = "طول نام کاربری باید حداقل برابر 5 باشد"
                false
            }else {
                layerUsername.error = ""
                true
            }
        }
        return false
    }

    private fun confirmCode(phone: String) {
        binding?.apply {
            onConfirmCode?.let {
                it(phone, txtIdentification.text.toString())
            }
        }
    }


    private fun checkSingUpInputs(): Boolean {
        binding?.apply {
            if (edtUsername.text.toString().isEmpty()) {
                layerUsername.error = "نام کاربری نمیتواند خالی باشد"
                return false
            } else if (edtUsername.text.toString().badWords()) {
                layerUsername.error = "نام کاربری مجاز نیست"
                return false
            } else if (edtPhone.text.toString().isEmpty()

            ) {
                layerPhone.error = "شماره تماس نمیتواند خالی باشد"
                return false
            } else if (edtPhone.length() < 9) {
                layerPhone.error = "شماره تماس به درستی وارد نشده"
                return false
            }
        }
        return true
    }

    private var onLogin: (() -> Unit)? = null
    fun onLoginClickedCallback(it: () -> Unit) {
        onLogin = it
    }

    private var onConfirmCode: ((String, String?) -> Unit)? = null
    fun onRequestConfirmCode(it: (String, String?) -> Unit) {
        onConfirmCode = it
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}