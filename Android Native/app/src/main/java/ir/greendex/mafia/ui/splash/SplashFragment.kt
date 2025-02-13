package ir.greendex.mafia.ui.splash

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentSplashBinding
import ir.greendex.mafia.ui.confirm_code.ConfirmCodeBsFragment
import ir.greendex.mafia.ui.login.LoginBsFragment
import ir.greendex.mafia.ui.signup.SignUpBsFragment
import ir.greendex.mafia.util.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding
    private val vm: SplashVm by viewModels()
    private lateinit var resendTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // check user signed
        checkUserSigned()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: ")
        // initViews
        initViews()

    }

    private fun checkUserSigned() {
        vm.getUserToken {
            if (it == "not_found") vm.getSignUpTimer()
            else findNavController().navigate(R.id.action_global_homeFragment)
        }
    }

    private fun login() {
        val loginBs = LoginBsFragment()
        loginBs.show(childFragmentManager, null)
        loginBs.isCancelable = false
        loginBs.onSignUpClicked {
            signUp()
        }

        loginBs.onRequestConfirmCodeCallback {
            lifecycleScope.launch {
                delay(500)
                confirmCode(phone = it, login = true)
            }
        }
    }

    private fun confirmCode(phone: String, login: Boolean, introducer: String? = null) {
        val confirmCodeBs =
            ConfirmCodeBsFragment(login = login, phone = phone, introducer = introducer)
        confirmCodeBs.show(childFragmentManager, null)
        confirmCodeBs.isCancelable = false
        // confirm code callback to navigation
        confirmCodeBs.whichPlaceShouldNavigateCallback {
            if (it == "login") login()
            else signUp()
        }

        // navigate

        confirmCodeBs.onNavigationToHomeCallback {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }
    }

    private fun signUp() {
        Log.i(TAG, "signUp: $")
        lifecycleScope.launch {
            val signUpBs = SignUpBsFragment()
            signUpBs.show(childFragmentManager, null)
            signUpBs.isCancelable = false

            signUpBs.onLoginClickedCallback {
                login()
//                signUpBs.dismiss()
            }

            signUpBs.onRequestConfirmCode { phone, intro ->
                confirmCode(phone = phone, login = false, introducer = intro)
            }
        }
    }


    private fun initViews() = lifecycleScope.launch {
        binding?.apply {

            // timer
            vm.getSignUpTimerLiveData.observe(viewLifecycleOwner) { timer ->
                if (timer == -1L || timer < System.currentTimeMillis()) {
                    signUp()
                } else {
                    vm.getUserPhone { phone ->
                        if (phone != "not_found") confirmCode(phone = phone, login = false)
                        return@getUserPhone
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (::resendTimer.isInitialized) resendTimer.cancel()
    }


}