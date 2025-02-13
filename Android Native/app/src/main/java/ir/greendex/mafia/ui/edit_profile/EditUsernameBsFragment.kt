package ir.greendex.mafia.ui.edit_profile

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentEditUsernameBsBinding
import ir.greendex.mafia.ui.edit_profile.vm.EditUsernameBsVm
import ir.greendex.mafia.util.extension.badWords
import ir.greendex.mafia.util.extension.hideAnim
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading
import ir.greendex.mafia.util.extension.showAnim
import ir.greendex.mafia.util.extension.textChangeCallback
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditUsernameBsFragment(private val currentUsername: String) : BottomSheetDialogFragment() {
    private var _binding: FragmentEditUsernameBsBinding? = null
    private val binding get() = _binding
    private val vm: EditUsernameBsVm by viewModels()

    override fun getTheme() = R.style.BottomSheetDialogThemeNoFloating

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditUsernameBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED
        // init views
        initViews()
    }

    private fun initViews() = lifecycleScope.launch {
        binding?.apply {
            edtName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerName.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerName.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                } else {
                    layerName.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerName.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                }
            }
            // username
            edtName.textChangeCallback(500) {
                lifecycleScope.launch {
                    changeUsernameStatus(name = it)
                }
            }

            // change
            btnChange.setOnClickListener {
                btnChange.loading(progress)
                changeUsername(newName = edtName.text.toString())
            }
        }
    }

    private fun changeUsername(newName: String) {
        binding?.apply {
            layerName.error = ""
            vm.changeUsername(newName = newName, token = MainActivity.userToken!!) {
                Log.i("LOG", "changeUsername: $it")
                lifecycleScope.launch {
                    if (it != null) {
                        if (it.status) {
                            // callback
                            onUsernameChanged?.let {
                                it(newName)
                            }
                            dismiss()
                        } else layerName.error = it.msg
                    } else layerName.error = "عدم ارتباط"

                    btnChange.hideLoading(progress)
                }
            }
        }
    }

    private fun changeUsernameStatus(name: CharSequence?) {
        binding?.apply {
            layerName.error = ""
            val res = checkExceptions(name.toString())
            if (res) {
                layerName.helperText = ""
                layerName.helperText = "در حال بررسی..."
                layerName.setHelperTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.orange
                        )
                    )
                )
                vm.checkUsername(newName = name.toString()) { serverResponse ->
                    lifecycleScope.launch {
                        if (serverResponse != null) {
                            if (serverResponse.status) {
                                // show btn
                                parent.showAnim(btnChange)
                                // btn color
                                btnChange.setBackgroundColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.red500
                                    )
                                )
                                layerName.helperText = "این نام مجاز است"
                                layerName.setHelperTextColor(
                                    ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.green800
                                        )
                                    )
                                )
                            } else layerName.error = serverResponse.msg
                        } else layerName.error = "عدم ارتباط"
                    }

                }
            } else parent.hideAnim(btnChange)
        }
    }

    private fun checkExceptions(it: String?): Boolean {
        binding?.apply {
            return if (it.isNullOrEmpty()) {
                layerName.error = "نام کاربری نمیتواند خالی باشد"
                false
            } else if (it == currentUsername) {
                layerName.error = "نام کاربری نمیتواند یکسان باشد"
                false
            } else if (it.badWords()) {
                layerName.error = "این نام کاربری مجاز نیست"
                false
            }else if (it.length<5) {
                layerName.error = "طول نام کاربری باید حداقل برابر 5 باشد"
                false
            } else {
                layerName.error = ""
                btnChange.isEnabled = true
                true
            }
        }
        return false
    }

    private var onUsernameChanged: ((String) -> Unit)? = null
    fun onUsernameChangedCallback(it: (String) -> Unit) {
        onUsernameChanged = it
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}