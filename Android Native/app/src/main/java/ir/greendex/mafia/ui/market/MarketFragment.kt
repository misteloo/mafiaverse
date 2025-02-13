package ir.greendex.mafia.ui.market

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.databinding.FragmentMarketBinding
import ir.greendex.mafia.databinding.LayerDialogConfirmPurchaseItemBinding
import ir.greendex.mafia.entity.market.MarketEntity
import ir.greendex.mafia.entity.market.MarketGoldEntity
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.ui.market.adapter.MarketAnimationAdapter
import ir.greendex.mafia.ui.market.adapter.MarketAvatarAdapter
import ir.greendex.mafia.ui.market.adapter.MarketGoldAdapter
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.extension.bindToClosingDialog
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading
import ir.greendex.mafia.util.sound.SoundManager
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MarketFragment : BaseFragment() {
    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding
    private val vm: MarketVm by viewModels()
    private var currentUserGold = 0
    private var purchaseGold: MarketGoldEntity? = null
    private lateinit var dialogConfirmPurchase: AlertDialog


    // injection
    @Inject
    lateinit var goldAdapter: MarketGoldAdapter

    @Inject
    lateinit var avatarAdapter: MarketAvatarAdapter

    @Inject
    lateinit var animAdapter: MarketAnimationAdapter

    @Inject
    lateinit var popupMenu: PopupMenu

    @Inject
    lateinit var soundManager: SoundManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get market items
        marketItems()

        // store routing
        storeRouting()
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.MARKET)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMarketBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // connect to payment
        connectToPayment()

        // initViews
        initViews()

        // ad
        ad()

    }

    private fun ad(){
        binding?.apply {
            bannerAd.loadAd()
        }
    }

    private fun connectToPayment() {
        vm.connectToPayment()
    }

    private fun initViews() {
        binding?.apply {
            rvGold.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            rvGold.adapter = goldAdapter
            rvAvatar.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            rvAvatar.adapter = avatarAdapter

            rvAnim.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            rvAnim.adapter = animAdapter


            // gold
            vm.goldItemLiveData.observe(viewLifecycleOwner) {
                goldAdapter.modifierItem(newItems = it)
                shimmer.visibility = View.GONE
                shimmer.stopShimmer()
                layerMain.visibility = View.VISIBLE
            }

            // avatar
            vm.avatarItemLiveData.observe(viewLifecycleOwner) {
                avatarAdapter.modifierItem(new = it)
            }

            // anim
            vm.animationItemLiveData.observe(viewLifecycleOwner) {
                animAdapter.modifierItem(newItem = it)
            }

            // purchase gold
            goldAdapter.purchaseGoldCallback { item ->
                purchaseGold = item
                MainActivity.userId?.let { userId ->
                    MainActivity.purchaseProduct(sku = item.id, payload = userId)
                }
            }


            // user gold
            vm.userGold.observe(viewLifecycleOwner) {
                val animator = ValueAnimator.ofInt(currentUserGold,it ?: 0)
                animator.duration = 1500
                animator.addUpdateListener {
                    txtGold.text = animator.animatedValue.toString()
                }
                animator.start()
                currentUserGold = it ?: 0
            }

            // on user purchase consumed successfully
            MainActivity.onUpdateMarketItemsCallback {
                // show gold anim
                playAnim()
                marketItems()
            }

            // purchase avatar
            avatarAdapter.onPurchaseAvatarCallback { item ->
                showConfirmDialog(item = item, type = "آواتار")
            }

            // purchase anim
            animAdapter.onPurchaseAnimItemCallback {item ->
                showConfirmDialog(item = item , type = "انیمیشن")
            }
        }
    }

    private fun showConfirmDialog(item: MarketEntity.MarketDataItemsDetail, type: String) {
        val layerDialogConfirmPurchase = LayerDialogConfirmPurchaseItemBinding.inflate(
            layoutInflater
        )
        val dialog = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(layerDialogConfirmPurchase.root)
        }
        dialogConfirmPurchase = dialog.create()
        dialogConfirmPurchase.bindToClosingDialog()
        dialogConfirmPurchase.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        layerDialogConfirmPurchase.apply {
            // clear data
            txtTitle.text = ""
            anim.clearAnimation()
            img.load(null)
            txtTitle.text = "خرید".plus(" ").plus(type)
            if (type == "آواتار") img.load(BASE_URL.plus(item.image))
            else anim.setAnimationFromUrl(BASE_URL.plus(item.anim))

            txtDecline.setOnClickListener {
                dialogConfirmPurchase.dismiss()
            }

            btnConfirm.setOnClickListener {
                btnConfirm.loading(progress = progress)
                requestPurchaseItem(item = item)
            }
        }

        dialogConfirmPurchase.show()

    }

    private fun requestPurchaseItem(item: MarketEntity.MarketDataItemsDetail) {
        MainActivity.userToken?.let {
            vm.purchaseItem(itemId = item.itemId, userToken = it) {
                lifecycleScope.launch {
                    if (it != null) {
                        if (it.status) {
                            msg("محصول خریداری شد")
                            marketItems()
                        } else {
                            msg(it.msg)
                        }
                    } else msg("عدم ارتباط")

                    dialogConfirmPurchase.dismiss()
                }
            }
        }
    }

    private fun playAnim() {
        binding?.apply {
            animPurchaseGold.repeatCount = 2
            animPurchaseGold.playAnimation()
            soundManager.coinSound()
        }
    }

    private fun marketItems() {
        vm.getMarketItems()
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