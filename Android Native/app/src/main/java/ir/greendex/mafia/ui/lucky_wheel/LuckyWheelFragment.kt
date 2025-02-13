package ir.greendex.mafia.ui.lucky_wheel

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.RotateAnimation
import androidx.core.animation.doOnEnd
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.adivery.sdk.Adivery
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.AdListener
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentLuckyWheelBinding
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.AD_REWARDED_VIDEO
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading
import ir.greendex.mafia.util.sound.SoundManager
import ir.samanjafari.easycountdowntimer.CountDownInterface
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LuckyWheelFragment : BaseFragment() {
    private var _binding: FragmentLuckyWheelBinding? = null
    private val binding get() = _binding
    private val vm: LuckyVm by viewModels()
    private var currentDegree: Float = 0f
    private var totalRotation = ((360f * 5)) // Total rotation angle for 5 turns
    private val rotationSpeed = 5000L // Time in milliseconds for 5 turns


    // injection
    @Inject
    lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // store routing
        storeRouting()

        // get lucky wheel status
        getLuckyWheelStatus()

    }

    private fun getLuckyWheelStatus() {
        MainActivity.userToken?.let {
            vm.getLuckyWheelStatus(it)
        }
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.LUCKY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLuckyWheelBinding.inflate(layoutInflater)
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

            // ad request
            btnShowAd.setOnClickListener {
                if (!MainActivity.rewardedAdAvailable) {
                    msg("ویدیو در دسترس نیست")
                    return@setOnClickListener
                }
                requestAd()
            }
            // ad listeners
            adListener()

            // spin wheel
            btnSpin.setOnClickListener {
                btnSpin.loading(progress = progressSpin)
                MainActivity.userToken?.let { token ->
                    vm.spinLuckyWheel(token = token) { callback ->
                        lifecycleScope.launch {
                            btnSpin.hideLoading(progress = progressSpin)
                            btnSpin.visibility = View.GONE
                            if (callback != null) {
                                totalRotation += vm.configureRotation(percent = callback.percent)
                                // spin
                                spinWheel(newRemain = callback.nextSpin , goldCount = vm.goldCount(percent = callback.percent))
                            }
                        }
                    }
                }
            }


            // observe
            vm.getLuckyWheelStatusLiveData.observe(viewLifecycleOwner) {
                lifecycleScope.launch {
                    if (it != null) {
                        frameLoading.visibility = View.GONE
                        if (it.isReady) {
                            cardCounter.visibility = View.GONE
                            btnShowAd.visibility = View.VISIBLE
                        } else {
                            cardCounter.visibility = View.VISIBLE
                            // timer
                            countDownTimer(remain = it.timerRemain)
                        }
                    } else msg("عدم ارتباط")
                }
            }
        }
    }

    private fun adListener() {
        binding?.apply {
            MainActivity.adListener(object: AdListener {
                override fun onRewardedAdGranted(granted: Boolean) {
                    if (granted){
                        btnSpin.isEnabled = true
                        btnSpin.visibility = View.VISIBLE

                        // spin
                        btnShowAd.visibility = View.GONE
                    }
                }

            })
        }
    }


    private fun countDownTimer(remain: Long) = lifecycleScope.launch {
        binding?.apply {
            val parsingData = vm.exportLeftTime(futureTime = remain).await()
            counter.setTime(parsingData[0], parsingData[1], parsingData[2], parsingData[3])
            counter.startTimer()
            counter.setOnTick(object : CountDownInterface {
                override fun onTick(time: Long) {

                }

                override fun onFinish() {
                    cardCounter.visibility = View.GONE
                    btnShowAd.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun requestAd() {
        if (Adivery.isLoaded(AD_REWARDED_VIDEO)){
            Adivery.showAd(AD_REWARDED_VIDEO)
        }
    }


    private fun spinWheel(newRemain: Long , goldCount : Int) {
        val rotation = RotateAnimation(
            currentDegree,
            totalRotation,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotation.duration = rotationSpeed
        rotation.fillAfter = true
        rotation.interpolator = OvershootInterpolator()
        rotation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {

                binding?.apply {
                    animHappy.setAnimation(R.raw.happyness)
                    animHappy.playAnimation()
                    cardCounter.visibility = View.VISIBLE
                    cardWinningGold.visibility = View.VISIBLE
                    // animation
                    val animator = ValueAnimator.ofInt(0,goldCount)
                    animator.duration = 1000
                    animator.addUpdateListener {
                        txtWinningGold.text = animator.animatedValue.toString()
                    }
                    animator.start()
                    // play coin sound
                    animator.doOnEnd {
                        soundManager.coinSound()
                    }

                }
                context?.let {
                    vm.playSound()
                }

                // timer
                countDownTimer(remain = newRemain)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        binding?.apply {
            imgWheel.startAnimation(rotation)
        }
//        currentDegree += 60
    }

    override fun onDestroy() {
        /*nativeBannerId?.let {
            TapsellPlus.destroyNativeBanner(requireActivity(), it)
        }*/
        _binding = null
        super.onDestroy()
    }
}