package ir.greendex.mafia.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.BuildConfig
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentProfileBinding
import ir.greendex.mafia.entity.profile.ProfileEntity
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.ui.edit_profile.EditAnimBsFragment
import ir.greendex.mafia.ui.edit_profile.EditAvatarBsFragment
import ir.greendex.mafia.ui.edit_profile.EditUsernameBsFragment
import ir.greendex.mafia.ui.ticket.CreateTicketBsFragment
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.INSTAGRAM_URL
import ir.greendex.mafia.util.USER_ID
import ir.greendex.mafia.util.USER_PHONE
import ir.greendex.mafia.util.USER_TOKEN
import ir.greendex.mafia.util.WEBSITE_URL
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.extension.bindToClosingSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private val vm: ProfileVm by viewModels()
    private var username: String? = null
    private var profileData: ProfileEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // store routing
        storeRouting()
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.PROFILE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get profile
        getProfile()

        // initViews
        initViews()
    }

    private fun getProfile() {
        vm.getMyProfile()
    }

    private fun initViews() {
        binding?.apply {
            lifecycleScope.launch {
                // app version
                navigationDrawer.getHeaderView(0).also {
                    it.findViewById<TextView>(R.id.txtAppVersion).text = " نسخه ".plus(BuildConfig.VERSION_NAME)
                }

                // change navigation title color
                changeDrawerTitleColor()
                // open drawer
                layerProfile.imgMenu.setOnClickListener {
                    if (!parent.isDrawerOpen(GravityCompat.END)) parent.openDrawer(GravityCompat.END)
                }

                // drawer click
                drawerMenu()


                // server data
                vm.profileLiveData.observe(viewLifecycleOwner) {
                    profileData = it
                    it?.let {
                        bindProfileData(data = it.data)
                        // disable shimmer
                        shimmer.stopShimmer()
                        shimmer.visibility = View.GONE
                        frameProfile.visibility = View.VISIBLE
                    }
                }

                // edit username
                layerProfile.fabChangeUsername.setOnClickListener {
                    editUsername()
                }

                // edit avatar
                layerProfile.fabChangeAvatar.setOnClickListener {
                    editAvatar()
                }

                // edit anim
                layerProfile.fabChangeAnim.setOnClickListener {
                    editAnim()
                }
            }
        }
    }

    private fun editAnim() {
        profileData?.let {
            val bs = EditAnimBsFragment(currentAnim = it.data.activeAsset.anim)
            bs.bindToClosingSheet()
            bs.show(childFragmentManager, null)
            bs.onSavedCallback {
                binding?.apply {
                    layerProfile.anim.setAnimationFromUrl(BASE_URL.plus(it))
                    // store anim for home screen
                    vm.storeAnim(name = it)
                }
            }
        }
    }

    private fun editAvatar() {
        profileData?.let {
            val bs = EditAvatarBsFragment(currentImage = it.data.activeAsset.avatar)
            bs.bindToClosingSheet()
            bs.show(childFragmentManager, null)
            bs.onSavedCallback {
                vm.getMyProfile()
            }
        }
    }

    private fun editUsername() {
        username?.let {
            val bs = EditUsernameBsFragment(currentUsername = it)
            bs.bindToClosingSheet()
            bs.show(childFragmentManager, null)
            bs.onUsernameChangedCallback {
                binding?.apply {
                    layerProfile.txtUsername.text = it
                }
            }
        }
    }

    private fun drawerMenu() {
        binding?.apply {
            navigationDrawer.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.drawerMenuGameHistory -> {
                        if (parent.isDrawerOpen(GravityCompat.END)) parent.closeDrawer(GravityCompat.END)
                        hideSmoothBar()
                        findNavController().navigate(R.id.action_global_totalGamesFragment)
                    }
                    R.id.drawerMenuTransaction -> {
                        if (parent.isDrawerOpen(GravityCompat.END)) parent.closeDrawer(GravityCompat.END)
                        hideSmoothBar()
                        findNavController().navigate(R.id.action_global_transactionFragment)
                    }

                    R.id.drawerMenuIntroduction -> {
                        val inviteMessage =
                            "به جمع ما در بازی مافیا ورس بپیوند تا دوتایی جایزه بگیریم و باهم خوش بگذرونیم\nکد معرف من ${MainActivity.userId}\n" +
                                    "میتونی مافیاورس رو از لینک زیر دانلود کنی \n https://mistelo.ir"

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT, inviteMessage)
                        startActivity(Intent.createChooser(intent, "انتخاب برنامه ارسال"))
                    }

                    R.id.drawerMenuTicket -> {
                        if (parent.isDrawerOpen(GravityCompat.END)) parent.closeDrawer(GravityCompat.END)
                        val bs = CreateTicketBsFragment()
                        bs.show(childFragmentManager, null)
                    }

                    R.id.drawerInstagram -> {
                        try {
                            val uri = Uri.parse(INSTAGRAM_URL)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.instagram.android")
                            startActivity(intent)
                        } catch (_: Exception) {
                            msg("خطا در بازکردن اینستاگرام")
                        }
                        if (parent.isDrawerOpen(GravityCompat.END)) parent.closeDrawer(GravityCompat.END)
                    }

                    R.id.drawerWebSite -> {
                        try {
                            val uri = Uri.parse(WEBSITE_URL)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        } catch (_: Exception) {
                            msg("خطا در بازکردن وب سایت")
                        }
                        if (parent.isDrawerOpen(GravityCompat.END)) parent.closeDrawer(GravityCompat.END)
                    }

                    R.id.drawerExit -> {
                        if (parent.isDrawerOpen(GravityCompat.END)) parent.closeDrawer(GravityCompat.END)
                        MainActivity.userToken = null
                        MainActivity.userId = null
                        MainActivity.emitJoinToSocket = false
                        // socket off
                        vm.dcSocket()
                        // clear socket connections
                        vm.clearSocketConnections()

                        lifecycleScope.launch {
                            MainActivity.userToken = null
                            // remove
                            vm.removeItemFromLocal(
                                keys = listOf(
                                    USER_ID,
                                    USER_PHONE,
                                    USER_TOKEN
                                )
                            )

                            delay(500)
                            findNavController().navigate(R.id.action_global_splashFragment)
                            hideSmoothBar()
                        }
                    }
                }
                return@setNavigationItemSelectedListener false
            }
        }
    }

    private fun bindProfileData(data: ProfileEntity.ProfileData) {
        binding?.layerProfile?.apply {
            username = data.identity.name
            imgUserAvatar.load(
                data = BASE_URL.plus(data.activeAsset.avatar)
            )
            anim.setAnimationFromUrl(BASE_URL.plus(data.activeAsset.anim))
            txtUsername.text = data.identity.name
//            anim.setAnimationFromUrl(BASE_URL.plus(data.activeAsset.anim))
            // ranking
            txtRankWeekly.text = data.sessionRank.week.toString()
            txtRankSeasonally.text = data.sessionRank.session.toString()
            txtRankTotally.text = data.ranking.rank.toString()

            // game result
            txtMafiaGame.text = data.gameResult.gameAsMafia.toString()
            txtCitizenGame.text = data.gameResult.gameAsCitizen.toString()
            if (data.gameResult.gameAsMafia > 0) {
                txtMafiaWinPercent.text =
                    ((data.gameResult.winAsMafia * 100) / data.gameResult.gameAsMafia).toString()
                        .plus(" %")
                progressMafiaWin.progress =
                    ((data.gameResult.winAsMafia * 100) / data.gameResult.gameAsMafia).toFloat()
            } else {
                txtMafiaWinPercent.text = "0 %"
                progressMafiaWin.progress = 0f
            }

            if (data.gameResult.gameAsCitizen > 0) {
                txtCitizenWinPercent.text =
                    ((data.gameResult.winAsCitizen * 100) / data.gameResult.gameAsCitizen).toString()
                        .plus(" %")


                progressCitizenWin.progress =
                    ((data.gameResult.winAsCitizen * 100) / data.gameResult.gameAsCitizen).toFloat()
            } else {
                txtCitizenWinPercent.text = "0 %"
                progressCitizenWin.progress = 0f
            }

            // game feedback
            txtAbandonGame.text = data.points.abandon.toString().plus(" %")
            progressAbandon.progress = data.points.abandon.toFloat()
            txtComReport.text = data.points.communicationReport.toString().plus(" %")
            progressComReport.progress = data.points.communicationReport.toFloat()
            txtRoleReport.text = data.points.roleReport.toString().plus(" %")
            progressRoleReport.progress = data.points.roleReport.toFloat()
        }
    }

    private fun changeDrawerTitleColor() {
        binding?.apply {
            val menu = navigationDrawer.menu
            // game history
            val game = menu.findItem(R.id.drawerTitleGame)
            SpannableString(game.title).apply {
                setSpan(
                    TextAppearanceSpan(context, R.style.NavigationDrawerTitleStyle),
                    0,
                    this.length,
                    0
                )
                game.title = this
            }

            // transaction
            val transaction = menu.findItem(R.id.drawerTitleTransaction)
            SpannableString(transaction.title).apply {
                setSpan(
                    TextAppearanceSpan(context, R.style.NavigationDrawerTitleStyle),
                    0,
                    this.length,
                    0
                )
                transaction.title = this
            }
            // introduction
            val introduction = menu.findItem(R.id.drawerTitleIntroduction)
            SpannableString(introduction.title).apply {
                setSpan(
                    TextAppearanceSpan(context, R.style.NavigationDrawerTitleStyle),
                    0,
                    this.length,
                    0
                )
                introduction.title = this
            }

            // communication
            val communication = menu.findItem(R.id.drawerTitleCommunication)
            SpannableString(communication.title).apply {
                setSpan(
                    TextAppearanceSpan(context, R.style.NavigationDrawerTitleStyle),
                    0,
                    this.length,
                    0
                )
                communication.title = this
            }
            // social
            val socialMedia = menu.findItem(R.id.drawerTitleSocialMedia)
            SpannableString(socialMedia.title).apply {
                setSpan(
                    TextAppearanceSpan(context, R.style.NavigationDrawerTitleStyle),
                    0,
                    this.length,
                    0
                )
                socialMedia.title = this
            }

        }
    }

    override fun onResume() {
        showSmoothBar()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}