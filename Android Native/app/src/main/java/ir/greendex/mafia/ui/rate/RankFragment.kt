package ir.greendex.mafia.ui.rate

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.TransitionManager
import coil.load
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentRankBinding
import ir.greendex.mafia.entity.rate.OtherProfileEntity
import ir.greendex.mafia.entity.rate.RankingEntity
import ir.greendex.mafia.entity.rate.RateRvEntity
import ir.greendex.mafia.entity.rate.RateType
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.extension.bindToClosingSheet
import ir.samanjafari.easycountdowntimer.CountDownInterface
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RankFragment : BaseFragment() {
    private var _binding: FragmentRankBinding? = null
    private val binding get() = _binding
    private val vm: RankVm by viewModels()

    private val rankingDataList by lazy { mutableListOf<RankingEntity.RankingModel>() }
    private val rankDailyList by lazy { mutableListOf<RankingEntity.RankingDataModel>() }
    private val rankWeeklyList by lazy { mutableListOf<RankingEntity.RankingDataModel>() }
    private val rankSeasonallyList by lazy { mutableListOf<RankingEntity.RankingDataModel>() }

    private val tabList by lazy { mutableListOf<TabLayout.Tab>() }

    private lateinit var selfDaily: RankingEntity.RankingDataModel
    private lateinit var selfWeekly: RankingEntity.RankingDataModel
    private lateinit var selfSeasonally: RankingEntity.RankingDataModel

    private lateinit var dailyViewTimer: CountDownInterface
    private lateinit var weeklyViewTimer: CountDownInterface
    private lateinit var seasonallyViewTimer: CountDownInterface

    private lateinit var lm: LinearLayoutManager

    // injection
    @Inject
    lateinit var adapter: RankAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // store routing
        storeRouting()

        // get rank
        getRank()
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.RATING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get ranking data
        getRanking()

        // get self
        getSelf()

        lifecycleScope.launch {
            // init
            initViews()
        }
    }

    private fun initViews() {
        binding?.apply {
            // change self layer background color
            includeLayerSelf.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey700
                )
            )

            tabLayout()
            lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            // rv
            rv.layoutManager = lm
            rv.adapter = adapter
            // observe
            vm.getRankingDataLiveData.observe(viewLifecycleOwner) {
                if (it.status) {
                    rankingDataList.clear()
                    rankingDataList.addAll(it.data.ranking)

                    // disable shimmer
                    shimmer.visibility = View.GONE

                    rv.visibility = View.VISIBLE
                    // bind to view
                    bindRankingDataToViews(type = RateType.DAILY)
                    // show self layer
                    showSelfLayer()
                    // enable tab touch
                    touchOnTab(enable = true)

                } else {
                    msg(it.msg)
                }
            }

            // profile
            adapter.onClickedUserCallback {
                vm.checkUserProfile(userId = it) {response ->
                    if (response != null) {
                        showOtherProfile(profile = response.data)
                    } else msg("عدم ارتباط")
                }
            }
        }
    }
    private fun showOtherProfile(profile:OtherProfileEntity.OtherProfileData){
        val bs = OtherProfileBsFragment(profile = profile)
        bs.show(childFragmentManager,null)
        bs.bindToClosingSheet()
    }
    private fun getSelf() {
        // daily
        vm.getSelfDailyLiveData.observe(viewLifecycleOwner) {
            selfDaily = it
        }

        // weekly
        vm.getSelfWeeklyLiveData.observe(viewLifecycleOwner) {
            selfWeekly = it
        }

        // seasonally
        vm.getSelfSeasonallyLiveData.observe(viewLifecycleOwner) {
            selfSeasonally = it
        }
    }

    private fun getRanking() {
        // daily
        vm.rankDailyList.observe(viewLifecycleOwner) {
            if (rankDailyList.isNotEmpty()) rankDailyList.clear()
            this.rankDailyList.addAll(it)
        }

        // weekly
        vm.rankWeeklyList.observe(viewLifecycleOwner) {
            if (this.rankWeeklyList.isNotEmpty()) rankWeeklyList.clear()
            this.rankWeeklyList.addAll(it)
        }

        // seasonally
        vm.rankSeasonallyList.observe(viewLifecycleOwner) {
            if (this.rankSeasonallyList.isNotEmpty()) rankSeasonallyList.clear()
            this.rankSeasonallyList.addAll(it)
        }

    }

    private fun bindRankingDataToViews(type: RateType) = lifecycleScope.launch {
        binding?.apply {
            when (type) {
                RateType.DAILY -> {

                    // timer
                    bindCountDownTimerToView(timer = vm.dailyTimeLeft, type = RateType.DAILY)
                    val daily = async {
                        val list = mutableListOf<RateRvEntity>()
                        rankDailyList.forEach {
                            list.add(
                                RateRvEntity(
                                    index = it.rate,
                                    userId = it.userId,
                                    image = it.userAvatar.userImage,
                                    name = it.userIdentity.userName,
                                    gold = it.prize.toString(),
                                    rank = it.sessionRank.toString(),
                                    place = if (it.rate in (1..3)) it.rate else 0
                                )
                            )
                        }
                        list
                    }
                    // rv
                    adapter.modifierItem(newItem = daily.await())
                    // self

                    bindSelfDataToView(it = selfDaily)
                }

                RateType.WEEKLY -> {
                    val weekly = async {
                        val list = mutableListOf<RateRvEntity>()
                        rankWeeklyList.forEach {
                            list.add(
                                RateRvEntity(
                                    index = it.rate,
                                    userId = it.userId,
                                    image = it.userAvatar.userImage,
                                    name = it.userIdentity.userName,
                                    gold = it.prize.toString(),
                                    rank = it.sessionRank.toString(),
                                    place = if (it.rate in (1..3)) it.rate else 0
                                )
                            )
                        }
                        list
                    }

                    adapter.modifierItem(newItem = weekly.await())
                    // self
                    bindSelfDataToView(it = selfWeekly)
                    // timer
                    bindCountDownTimerToView(timer = vm.weeklyTimeLeft, type = RateType.WEEKLY)
                }

                RateType.SEASONALLY -> {
                    val seasonally = async {
                        val list = mutableListOf<RateRvEntity>()
                        rankSeasonallyList.forEach {
                            list.add(
                                RateRvEntity(
                                    index = it.rate,
                                    userId = it.userId,
                                    image = it.userAvatar.userImage,
                                    name = it.userIdentity.userName,
                                    gold = it.prize.toString(),
                                    rank = it.sessionRank.toString(),
                                    place = if (it.rate in (1..3)) it.rate else 0
                                )
                            )
                        }
                        list
                    }
                    adapter.modifierItem(newItem = seasonally.await())
                    // self
                    bindSelfDataToView(it = selfSeasonally)
                    // timer
                    bindCountDownTimerToView(
                        timer = vm.seasonallyTimeLeft,
                        type = RateType.SEASONALLY
                    )
                }
            }
        }
    }

    private fun bindCountDownTimerToView(timer: Long, type: RateType) = lifecycleScope.launch {
        binding?.apply {
            val timeLeft =
                vm.exportLeftTime(time = ((timer - System.currentTimeMillis()) / 1000).toFloat())

            try {
                countDown.pause()
            } catch (_: Exception) {
            }

            countDown.setTime(0, 0, 0, 0)
            when (type) {
                RateType.DAILY -> {
                    if (timer < System.currentTimeMillis()) return@apply
                    countDown.setTime(
                        timeLeft[0],
                        timeLeft[1],
                        timeLeft[2],
                        timeLeft[3]
                    )
                    dailyViewTimer = object : CountDownInterface {
                        override fun onTick(time: Long) {

                        }

                        override fun onFinish() {
                        }
                    }
                    countDown.setOnTick(dailyViewTimer)
                    countDown.startTimer()
                }

                RateType.WEEKLY -> {
                    if (timer < System.currentTimeMillis()) return@apply
                    countDown.setTime(
                        timeLeft[0],
                        timeLeft[1],
                        timeLeft[2],
                        timeLeft[3]
                    )
                    weeklyViewTimer = object : CountDownInterface {
                        override fun onTick(time: Long) {

                        }

                        override fun onFinish() {
                        }
                    }
                    countDown.setOnTick(weeklyViewTimer)
                    countDown.startTimer()
                }

                RateType.SEASONALLY -> {

                    if (timer < System.currentTimeMillis()) return@apply
                    countDown.setTime(
                        timeLeft[0],
                        timeLeft[1],
                        timeLeft[2],
                        timeLeft[3]
                    )
                    seasonallyViewTimer = object : CountDownInterface {
                        override fun onTick(time: Long) {

                        }

                        override fun onFinish() {
                        }
                    }
                    countDown.setOnTick(seasonallyViewTimer)
                    countDown.startTimer()
                }
            }
        }
    }

    private fun bindSelfDataToView(it: RankingEntity.RankingDataModel) {

        binding?.includeLayerSelf?.apply {
            imgUser.load(BASE_URL.plus(it.userAvatar.userImage))
            txtUserIndex.text = it.rate.toString()
            txtUsername.text = it.userIdentity.userName
            txtPrize.text = it.prize.toString()
            txtCup.text = it.sessionRank.toString()
            if (it.rate in (1..3)) {
                when (it.rate) {
                    1 -> imgMedal.load(R.drawable.rate_first_place)
                    2 -> imgMedal.load(R.drawable.rate_second_place)
                    3 -> imgMedal.load(R.drawable.rate_place_3)
                }
            }
        }
    }

    private fun tabLayout() {
        binding?.apply {
            val dailyTab = tab.newTab().apply {
                text = "روزانه"
                tag = RateType.DAILY
            }
            val weekTab = tab.newTab().apply {
                text = "هفتگی"
                tag = RateType.WEEKLY
            }
            val seasonTab = tab.newTab().apply {
                text = "فصلی"
                tag = RateType.SEASONALLY
            }
            tab.addTab(dailyTab)
            tab.addTab(weekTab)
            tab.addTab(seasonTab)
            if (tabList.isEmpty()) {
                tabList.add(dailyTab)
                tabList.add(weekTab)
                tabList.add(seasonTab)
            }

            // disable tab click
            touchOnTab(enable = false)

            tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.tag?.let {
                        lifecycleScope.launch {
                            if (lm.findFirstCompletelyVisibleItemPosition() == 0) {
                                bindRankingDataToViews(type = it as RateType)
                            } else {
                                val job = async {
                                    rv.smoothScrollToPosition(0)
                                }
                                job.join()
                                delay(300)
                                bindRankingDataToViews(type = it as RateType)
                            }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })

        }
    }

    private fun touchOnTab(enable: Boolean) {
        tabList.forEach {
            it.view.isClickable = enable
        }
    }

    private fun getRank() {
        vm.getRankingData()
    }

    private fun showSelfLayer() {
        binding?.apply {
            val anim = Slide(Gravity.BOTTOM).apply {
                duration = 700
                addTarget(layerSelf)
            }

            TransitionManager.beginDelayedTransition(root, anim)
            layerSelf.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        showSmoothBar()
        super.onResume()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}