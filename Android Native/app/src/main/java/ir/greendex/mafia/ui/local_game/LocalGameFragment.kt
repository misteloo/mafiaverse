package ir.greendex.mafia.ui.local_game

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.TransitionManager
import coil.load
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentLocalGameBinding
import ir.greendex.mafia.databinding.LayerDialogShowQrCodeToLocalBinding
import ir.greendex.mafia.databinding.LayerLocalGameUserNameBinding
import ir.greendex.mafia.databinding.LayerPrvDeckLocalBinding
import ir.greendex.mafia.entity.local.LocalBase64Entity
import ir.greendex.mafia.entity.local.LocalCharacterEntity
import ir.greendex.mafia.entity.local.LocalErrorEntity
import ir.greendex.mafia.entity.local.LocalUsersJoinedEntity
import ir.greendex.mafia.entity.local.LocalUsersJoiningEntity
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.ui.local_game.adapter.LocalPrvCharacterAdapter
import ir.greendex.mafia.ui.local_game.adapter.LocalShowRoleAdapter
import ir.greendex.mafia.ui.local_game.listeners.LocalGameVmListener
import ir.greendex.mafia.ui.local_game.vm.LocalGameVm
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.dialog.DialogManager
import ir.greendex.mafia.util.extension.bindToClosingDialog
import ir.greendex.mafia.util.extension.bindToClosingSheet
import ir.greendex.mafia.util.socket.SocketManager
import ir.greendex.mafia.util.sound.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LocalGameFragment : BaseFragment(), LocalGameVmListener {
    private var _binding: FragmentLocalGameBinding? = null
    private val binding get() = _binding
    private val dialogManager by lazy { DialogManager(context, soundManager) }
    private val createLocalGame by lazy { CreateLocalGameBsFragment() }
    private val vm: LocalGameVm by viewModels()
    private var playerCount = 0
    private var playerName: String? = null
    private var dialogQrCode: AlertDialog? = null
    private var socketStarted = false
    private lateinit var prvDeck: BottomSheetDialog

    // injection

    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var localUsersAdapter: LocalShowRoleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!socketStarted) {
            // start socket
            startSocket()

            // listener
            localGameListener()

            // disable twice
            socketStarted = true

        }

        // store routing
        storeRouting()
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.LOCAL)
    }

    private fun startSocket() {
        vm.setLocalGameSocketManager(localGameSocketManager = SocketManager.getLocalGameSocket)
    }

    private fun localGameListener() {
        vm.initLocalSocket(listener = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocalGameBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initView
        initViews()

    }


    private fun initViews() {
        binding?.apply {
            // back
            imgBack.setOnClickListener {
                vm.turnOffSocket()
                findNavController().popBackStack()
            }

            // rv
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rv.adapter = localUsersAdapter

            btnCreate.setOnClickListener {
                localGame()
            }

            btnJoin.setOnClickListener {
                requestPermission()
            }

            // get data from qr code scanner
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("game_id")
                ?.observe(viewLifecycleOwner) { gameId ->
                    // send game id to server
                    playerName?.let {
                        vm.joinLocalGame(gameId = gameId, name = it)
                    }
                }

            SelectDeckFragment.onSelectedDeckCallback {
                if (it.isNotEmpty()) vm.setDeck(it)
            }

            // user exit from local game
            onUserForceExitCallback {
                vm.leaveLocalGame()
            }

            // show create panel
            btnRemove.setOnClickListener {
                flip.visibility = View.GONE
                localUsersAdapter.modifierItem(newItem = emptyList())
                showCreatePanel()
            }
        }
    }

    private fun localGame() {
        createLocalGame.show(childFragmentManager, null)
        createLocalGame.bindToClosingSheet()
        createLocalGame.onCreateLocalCallback {
            playerCount = it.toInt()
            vm.createLocalGame(playerCount = it)
            // get deck from server
            vm.getDeck()
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    dialogManager.qrCodeScannerRational {
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), 10)
                    }
                }
            } else {
                // permission granted
                getUsernameSheet {
                    lifecycleScope.launch {
                        delay(200)
                        playerName = it
                        findNavController().navigate(R.id.action_localGameFragment_to_qrCodeScannerFragment)
                    }
                }
            }
        }
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onRequestPermissionsResult(requestCode, permissions, grantResults)",
            "ir.greendex.mafia.util.base.BaseFragment"
        )
    )
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted
            getUsernameSheet {
                lifecycleScope.launch {
                    delay(200)
                    playerName = it
                    findNavController().navigate(R.id.action_localGameFragment_to_qrCodeScannerFragment)
                }
            }
        } else {
            msg("دسترسی داده نشد")
        }
    }

    private fun getUsernameSheet(name: (String) -> Unit) {
        val view = LayerLocalGameUserNameBinding.inflate(LayoutInflater.from(context))
        val sheet = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogThemeNoFloating)
        sheet.setContentView(view.root)
        sheet.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sheet.behavior.state = STATE_EXPANDED
        sheet.setCancelable(false)
        sheet.show()

        view.apply {
            layerName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    layerName.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    layerName.hintTextColor = ColorStateList.valueOf(
                        Color.WHITE
                    )
                }
            }


            // click
            btnConfirm.setOnClickListener {
                if (edtName.text.toString().isEmpty()) {
                    msg("اسم را وارد کنید")
                    return@setOnClickListener
                }
                // callback
                name(edtName.text.toString())
                sheet.dismiss()
            }

            // close
            fabClose.setOnClickListener {
                sheet.dismiss()
            }
        }
    }

    override fun onGetDeck(data: LocalCharacterEntity) {
        lifecycleScope.launch {
            val bundle = bundleOf().apply {
                putParcelable("local_deck", data)
                putInt("player_count", playerCount)
            }
            findNavController().navigate(
                R.id.action_localGameFragment_to_selectDeckFragment,
                bundle
            )
        }
    }

    override fun onBase64(data: LocalBase64Entity.LocalBase64Data) {
        lifecycleScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                val qrCodeByteArray: Deferred<ByteArray> = async {
                    val trimIndex = (data.qr.indexOf("base64,", 0) + 7)
                    val base = data.qr.substring(trimIndex, data.qr.length)
                    Base64.decode(base, Base64.DEFAULT)
                }
                withContext(Dispatchers.Main) {
                    binding?.apply {
                        dialogQrCode(
                            byteArray = qrCodeByteArray.await()
                        ) {
                            // emit start to server
                            vm.startPick()
                            // disable create panel and show remove panel
                            showRemovePanel()
                        }
                    }
                }
            }
        }
    }

    override fun onError(msg: LocalErrorEntity.LocalErrorMsg) {
        lifecycleScope.launch {
            showSnack(msg = msg.msg)
        }
    }

    override fun onCheckPrvDeck(data: List<LocalCharacterEntity.LocalCharacterDeck>) {
        lifecycleScope.launch {
            showPrvDeck(deckList = filterMultiData(data = data).await())
        }
    }

    private suspend fun filterMultiData(data: List<LocalCharacterEntity.LocalCharacterDeck>): Deferred<List<LocalCharacterEntity.LocalCharacterDeck>> =
        withContext(Dispatchers.IO) {
            async {
                val pure = data.distinctBy {
                    it.id
                }.toMutableList()
                pure.forEach { item ->
                    data.count {
                        it.id == item.id
                    }.also { count ->
                        pure.indexOfFirst {
                            it.id == item.id
                        }.also { index ->
                            pure[index] = pure[index].apply {
                                this.count = count
                            }
                        }
                    }
                }
                return@async pure
            }
        }

    override fun onUsersJoining(data: LocalUsersJoiningEntity.UsersJoinedToLocalData) {
        lifecycleScope.launch {
            // set callback to ready button
            onReadyToStart?.let {
                it(data.canStarted)
            }
            if (data.canStarted) vibrate.longVibrate()
        }
    }

    override fun onUsersJoined(data: List<LocalUsersJoinedEntity.LocalJoinedUsersDetail>) {
        lifecycleScope.launch {
            // qr code dialog dismiss
            dialogQrCode?.dismiss()
            localUsersAdapter.modifierItem(newItem = data)
        }
    }

    override fun onUserCharacter(data: LocalCharacterEntity.LocalCharacterDeck) {
        lifecycleScope.launch {
            // close dialog
            showRemovePanel()
            // dismiss prv deck
            if (::prvDeck.isInitialized) {
                prvDeck.dismiss()
            }
            binding?.apply {
                flip.visibility = View.VISIBLE
                imgRole.load(BASE_URL.plus(data.icon))
                txtRole.text = data.name
            }
        }
    }

    private fun dialogQrCode(
        byteArray: ByteArray? = null,
        shuffle: () -> Unit
    ) {
        val layer = LayerDialogShowQrCodeToLocalBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(layer.root)
        }
        dialogQrCode = builder.create()
        dialogQrCode?.bindToClosingDialog()
        dialogQrCode?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogQrCode?.show()

        if (byteArray != null) {
            layer.apply {
                imgQr.load(byteArray) {
                    crossfade(true)
                    crossfade(500)
                }
            }
        }


        layer.apply {
            txtCancel.setOnClickListener {
                dialogQrCode?.dismiss()
            }
            // start shuffle cards
            btnShuffle.setOnClickListener {
                // callback
                shuffle()
                dialogQrCode?.dismiss()
            }

            onReadyToStartCallback {
                btnShuffle.isEnabled = it
            }
        }

    }

    private var onReadyToStart: ((Boolean) -> Unit)? = null
    private fun onReadyToStartCallback(it: (Boolean) -> Unit) {
        onReadyToStart = it
    }

    private fun showPrvDeck(
        deckList: List<LocalCharacterEntity.LocalCharacterDeck>
    ) {
        lifecycleScope.launch {
            val layer = LayerPrvDeckLocalBinding.inflate(LayoutInflater.from(context))
            prvDeck =
                BottomSheetDialog(
                    requireContext(),
                    R.style.BottomSheetDialogThemeNoFloating
                ).apply {
                    setCancelable(false)
                    setContentView(layer.root)
                }
            prvDeck.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            prvDeck.behavior.state = STATE_EXPANDED
            prvDeck.create()
            prvDeck.show()

            layer.apply {
                btnExit.setOnClickListener {
                    onUserForceExit?.let {
                        it()
                    }
                    prvDeck.dismiss()
                }

                // rv
                rv.layoutManager = FlexboxLayoutManager(context).apply {
                    flexDirection = FlexDirection.ROW
                    flexWrap = FlexWrap.WRAP
                    justifyContent = JustifyContent.SPACE_AROUND
                }

                rv.adapter = LocalPrvCharacterAdapter(deckList)
            }
        }
    }

    private fun showCreatePanel() = lifecycleScope.launch {
        binding?.apply {
            val animGone = Slide(Gravity.BOTTOM).apply {
                duration = 500
                addTarget(frameRemoveGame)
            }
            TransitionManager.beginDelayedTransition(root, animGone)
            frameRemoveGame.visibility = View.GONE

            delay(500)

            val animVisible = Slide(Gravity.BOTTOM).apply {
                duration = 500
                addTarget(linearCreateGame)
            }
            TransitionManager.beginDelayedTransition(root, animVisible)
            linearCreateGame.visibility = View.VISIBLE
        }
    }

    private fun showRemovePanel() = lifecycleScope.launch {
        binding?.apply {
            val animGone = Slide(Gravity.BOTTOM).apply {
                duration = 500
                addTarget(linearCreateGame)
            }
            TransitionManager.beginDelayedTransition(root, animGone)
            linearCreateGame.visibility = View.GONE

            delay(500)

            val animVisible = Slide(Gravity.BOTTOM).apply {
                duration = 500
                addTarget(frameRemoveGame)
            }
            TransitionManager.beginDelayedTransition(root, animVisible)
            frameRemoveGame.visibility = View.VISIBLE

        }
    }

    private var onUserForceExit: (() -> Unit)? = null
    private fun onUserForceExitCallback(it: () -> Unit) {
        onUserForceExit = it
    }


    override fun onDestroy() {
        vm.turnOffSocket()
        _binding = null
        super.onDestroy()
    }
}