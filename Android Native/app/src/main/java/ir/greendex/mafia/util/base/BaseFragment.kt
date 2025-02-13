package ir.greendex.mafia.util.base

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentBaseBinding
import ir.greendex.mafia.util.vibrate.Vibrate
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment : Fragment() {
    private val vm: BaseVm by viewModels()
    private var _binding: FragmentBaseBinding? = null
    private val binding get() = _binding


    companion object {
        const val TAG = "LOG"
    }

    // injection
    @Inject
    lateinit var vibrate: Vibrate


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentBaseBinding.inflate(layoutInflater)
        return binding?.root
    }

    fun msg(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        vibrate.normalVibrate()
    }

    fun longMsg(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        vibrate.longVibrate()
    }

    fun showSmoothBar() {
        activity?.apply {
            findViewById<MotionLayout>(R.id.mainActivityParent).transitionToEnd()
        }
    }

    fun hideSmoothBar() {
        activity?.apply {
            findViewById<MotionLayout>(R.id.mainActivityParent).transitionToStart()
        }
    }

    fun showSnack(msg: String) {
        binding?.apply {
            val snack = Snackbar.make(this.root, msg, Snackbar.LENGTH_LONG)
            ViewCompat.setLayoutDirection(snack.view, ViewCompat.LAYOUT_DIRECTION_RTL)
            snack.show()
            // vibrate
            vibrate.longVibrate()
        }
    }

    override fun onResume() {
        super.onResume()
        var clicked = false
        activity?.onBackPressedDispatcher?.addCallback {
            if (clicked) {
                activity?.finish()
            } else {
                msg("برای خروج دوباره کلیک کنید")
                object : CountDownTimer(2000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        clicked = true
                    }

                    override fun onFinish() {
                        clicked = false
                    }

                }.start()
            }
        }
    }
}