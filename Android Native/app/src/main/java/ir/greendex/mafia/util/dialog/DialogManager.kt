package ir.greendex.mafia.util.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.LayerDialogAudioOutputSettingBinding
import ir.greendex.mafia.databinding.LayerDialogCameraPermissionBinding
import ir.greendex.mafia.databinding.LayerDialogCheckReadyBinding
import ir.greendex.mafia.databinding.LayerDialogDayUsingGunBinding
import ir.greendex.mafia.databinding.LayerDialogDetectiveInquiryBinding
import ir.greendex.mafia.databinding.LayerDialogExitBinding
import ir.greendex.mafia.databinding.LayerDialogGameResultBinding
import ir.greendex.mafia.databinding.LayerDialogIdentificationBinding
import ir.greendex.mafia.databinding.LayerDialogModeratorForceKickPermissionBinding
import ir.greendex.mafia.databinding.LayerDialogNatoMafiaDecisionBinding
import ir.greendex.mafia.databinding.LayerDialogNightMsgBinding
import ir.greendex.mafia.databinding.LayerDialogOnRequestSpeechOptionsBinding
import ir.greendex.mafia.databinding.LayerDialogReconnectNotificationBinding
import ir.greendex.mafia.databinding.LayerDialogReportBinding
import ir.greendex.mafia.databinding.LayerDialogSimpleMessageConfirmableBinding
import ir.greendex.mafia.databinding.LayerDialogSimpleMsgBinding
import ir.greendex.mafia.databinding.LayerDialogSpeechOptionMsgBinding
import ir.greendex.mafia.databinding.LayerDialogSpeechOptionsBinding
import ir.greendex.mafia.databinding.LayerDialogUpdateNewVersionBinding
import ir.greendex.mafia.databinding.LayerDialogUsedGunBinding
import ir.greendex.mafia.databinding.LayerDialogWaitOtherJoinBinding
import ir.greendex.mafia.databinding.LayerDialogWhichUserRequestSpeechOptionsBinding
import ir.greendex.mafia.entity.audio_switch.AudioSwitchEntity
import ir.greendex.mafia.entity.game.general.EndGameResultEntity
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.audio_device.AudioSwitch
import ir.greendex.mafia.util.audio_device.AudioSwitchAdapter
import ir.greendex.mafia.util.extension.bindToClosingDialog
import ir.greendex.mafia.util.sound.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DialogManager(
    private val context: Context?, private val soundManager: SoundManager
) {

    fun waitingToJoin(): AlertDialog {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.apply {
            setCancelable(false)
            setView(LayerDialogWaitOtherJoinBinding.inflate(LayoutInflater.from(context)).root)
        }
        return alertDialog.create()
    }

    fun nightMessage(msg: String) {
        val alertDialog = AlertDialog.Builder(context)
        val view = LayerDialogNightMsgBinding.inflate(LayoutInflater.from(context))
        alertDialog.apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = alertDialog.create()
        dialog.bindToClosingDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        view.apply {
            txtMsg.text = msg
            btnConfirm.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    fun simpleMessage(msg: String, timer: Int, resImage: Int = 0) {
        val alertDialog = AlertDialog.Builder(context)
        val view = LayerDialogSimpleMsgBinding.inflate(LayoutInflater.from(context))
        alertDialog.apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = alertDialog.create()
        dialog.bindToClosingDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        view.apply {
            txtMsg.text = msg
            if (resImage != 0) imgMsg.load(resImage)
        }

        object : CountDownTimer(timer * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                dialog.dismiss()
            }

        }.start()
    }

    fun simpleMessageConfirmable(msg: String, confirm: (Boolean) -> Unit) {
        val alertDialog = AlertDialog.Builder(context)
        val view = LayerDialogSimpleMessageConfirmableBinding.inflate(LayoutInflater.from(context))
        alertDialog.apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = alertDialog.create()
        dialog.bindToClosingDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        view.apply {
            txtMsg.text = msg

            // confirm
            btnConfirm.setOnClickListener {
                confirm(true)
                dialog.dismiss()
            }

            // cancel
            txtCancel.setOnClickListener {
                confirm(false)
                dialog.dismiss()
            }
        }
    }

    fun mafiaDecision(timer: Int, selectedShot: (Boolean?) -> Unit) {
        var shot: Boolean? = null
        val alertDialog = AlertDialog.Builder(context)
        val view = LayerDialogNatoMafiaDecisionBinding.inflate(LayoutInflater.from(context))
        alertDialog.apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = alertDialog.create()
        dialog.bindToClosingDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        view.apply {
            progress.progressMax = timer.toFloat()
            val countDownTimer = object : CountDownTimer(timer * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val value = (millisUntilFinished / 1000L)
                    progress.progress = value.toFloat()
                    txtCounter.text = value.toString()
                }

                override fun onFinish() {
                    selectedShot(null)
                    dialog.dismiss()
                }
            }

            countDownTimer.start()

            cardNato.setOnClickListener {
                shot = false
                cardNato.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.green800))
                cardShot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey500))
                btnConfirm.apply {
                    isEnabled = true
                    visibility = View.VISIBLE
                }
            }

            cardShot.setOnClickListener {
                shot = true
                cardNato.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.grey500))
                cardShot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green800))
                btnConfirm.apply {
                    isEnabled = true
                    visibility = View.VISIBLE
                }
            }

            btnConfirm.setOnClickListener {
                selectedShot(shot)
                countDownTimer.cancel()
                dialog.dismiss()
            }
        }
    }

    fun detectiveInquiry(user: InGameUsersDataEntity.InGameUserData, inquiry: Boolean) {
        val view = LayerDialogDetectiveInquiryBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        view.apply {
            imgUser.load(BASE_URL.plus(user.userImage))
            txtUsername.text = user.userName
            if (inquiry) animLike.visibility = View.VISIBLE else animDis.visibility = View.VISIBLE
            btnConfirm.setOnClickListener {
                animDis.visibility = View.GONE
                animLike.visibility = View.GONE
                dialog.dismiss()
            }
        }

    }

    fun usingSpeechOption(msg: String, timer: Int, option: (Boolean) -> Unit) {
        val view = LayerDialogSpeechOptionsBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        view.apply {
            progress.progressMax = timer.toFloat()
            txtMsg.text = msg
            val countDown = object : CountDownTimer(timer * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progress.progress = (millisUntilFinished / 1000L).toFloat()
                }

                override fun onFinish() {
                    dialog.dismiss()
                }

            }.start()


            btnCancel.setOnClickListener {
                countDown.cancel()
                option(false)
                dialog.dismiss()
            }

            btnConfirm.setOnClickListener {
                option(true)
                dialog.dismiss()
            }
        }
    }

    fun whichUserRequestToSpeechOptions(user: InGameUsersDataEntity.InGameUserData, timer: Int) {
        val view =
            LayerDialogWhichUserRequestSpeechOptionsBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setView(view.root)
            setCancelable(false)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        view.apply {
            progress.progressMax = timer.toFloat()
            imgUser.load(BASE_URL.plus(user.userImage))
            txtUsername.text = user.userName
            object : CountDownTimer(timer * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progress.progress = (millisUntilFinished / 1000L).toFloat()
                }

                override fun onFinish() {
                    dialog.dismiss()
                }
            }.start()
        }
    }

    fun speechOptionMsg(timer: Int, msg: String) {
        val view = LayerDialogSpeechOptionMsgBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setView(view.root)
            setCancelable(false)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        view.apply {
            progress.progressMax = timer.toFloat()
            txtMsg.text = msg
            object : CountDownTimer(timer * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progress.progress = (millisUntilFinished / 1000L).toFloat()
                }

                override fun onFinish() {
                    dialog.dismiss()
                }
            }.start()
        }
    }

    fun requestSpeechOption(
        user: InGameUsersDataEntity.InGameUserData,
        timer: Int,
        option: String,
        startToAnim: () -> Unit
    ) {
        val view = LayerDialogOnRequestSpeechOptionsBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.apply {
            progress.progressMax = timer.toFloat()
            txtOption.text = when (option) {
                "target" -> "درخواست تارگت دارد"
                "cover" -> "درخواست کاور دارد"
                "about" -> "درخواست درباره دارد"
                else -> ""
            }
            imgUser.load(BASE_URL.plus(user.userImage))
            object : CountDownTimer(timer * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progress.progress = (millisUntilFinished / 1000L).toFloat()
                }

                override fun onFinish() {
                    progress.progress = 0f
                    dialog.dismiss()
                    // callback function to start vote anim
                    startToAnim()
                }
            }.start()
        }
        dialog.show()
    }

    fun dayUsingGun(user: InGameUsersDataEntity.InGameUserData) {
        val view = LayerDialogDayUsingGunBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.apply {
            imgUser.load(BASE_URL.plus(user.userImage))
            txtUserName.text = user.userName
            btnConfirm.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun dayUsedGun(
        fromUser: InGameUsersDataEntity.InGameUserData,
        toUser: InGameUsersDataEntity.InGameUserData,
        gunKind: String
    ) {
        val view = LayerDialogUsedGunBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setView(view.root)
            setCancelable(false)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        view.apply {
            imgFromUser.load(BASE_URL.plus(fromUser.userImage))
            imgToUser.load(BASE_URL.plus(toUser.userImage))
            txtFromUser.text = fromUser.userName
            txtToUser.text = toUser.userName
            if (gunKind == "fighter") {
                imgGunKind.load(R.drawable.real_bullet)
                txtGunKind.text = "تیر جنگی"
            } else {
                imgGunKind.load(R.drawable.fake_bullet)
                txtGunKind.text = "تیر مشقی"
            }
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                rootMotion.transitionToEnd {
                    object : CountDownTimer(5000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            dialog.dismiss()
                        }
                    }.start()
                }
            }
        }
    }

    fun reportMessage(msg: String, timer: Int, user: InGameUsersDataEntity.InGameUserData?) {
        val alertDialog = AlertDialog.Builder(context)
        val view = LayerDialogReportBinding.inflate(LayoutInflater.from(context))
        alertDialog.apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = alertDialog.create()
        dialog.bindToClosingDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.apply {
            txtMsg.text = msg
            if (user != null) {
                txtUsername.also {
                    it.visibility = View.VISIBLE
                    it.text = user.userName
                }
                // user image
                imgMsg.load(BASE_URL.plus(user.userImage))
            }
        }
        dialog.show()
        object : CountDownTimer(timer * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                try {
                    dialog.dismiss()
                }catch (_:Exception){}
            }

        }.start()
    }

    fun dialogGameResult(
        gameResult: EndGameResultEntity.EndGameResultData,
        userId: String,
        showResult: (Boolean) -> Unit
    ) {
        val view = LayerDialogGameResultBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setView(view.root)
            setCancelable(false)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        view.apply {
            btnShowResult.setOnClickListener {
                showResult(true)
                dialog.dismiss()
            }

            txtExit.setOnClickListener {
                showResult(false)
                dialog.dismiss()
            }

            // winner
            if (gameResult.winner == "mafia") {
                imgWinnerHat.load(R.drawable.mafia_hat)
                txtWinner.text = "تیم مافیا"
            } else {
                imgWinnerHat.load(R.drawable.citizen_hat)
                txtWinner.text = "تیم شهروند"
            }
        }

        gameResult.users.find {
            userId == it.userId
        }?.let {
            if (it.winner) {
                soundManager.winnerSound()
            } else {
                soundManager.loseSound()
            }
        }
    }

    fun dialogReconnectNotification(connectToGame: (Boolean) -> Unit) {
        val view = LayerDialogReconnectNotificationBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.bindToClosingDialog()
        dialog.show()

        view.apply {
            btnContinue.setOnClickListener {
                connectToGame(true)
                dialog.dismiss()
            }

            txtAbandon.setOnClickListener {
                connectToGame(false)
                dialog.dismiss()
            }
        }
    }

    fun dialogExitGame(exit: (Boolean) -> Unit) {
        val view = LayerDialogExitBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        view.apply {
            btnExit.setOnClickListener {
                exit(true)
                dialog.dismiss()
            }

            txtCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    fun dialogExitChannelGame(exit: (Boolean) -> Unit) {
        val view = LayerDialogExitBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(view.root)
        }
        val dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        view.apply {
            btnExit.setOnClickListener {
                exit(true)
                dialog.dismiss()
            }

            txtCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    fun dialogCheckReady(confirm: (Boolean) -> Unit) {
        val layer = LayerDialogCheckReadyBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(layer.root)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        layer.apply {
            btnConfirm.setOnClickListener {
                confirm(true)
                dialog.dismiss()
            }

            btnCancel.setOnClickListener {
                confirm(false)
                dialog.dismiss()
            }

            object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progress.progress = (millisUntilFinished / 1000).toFloat()
                }

                override fun onFinish() {
                    dialog.dismiss()
                }
            }.start()
        }
    }

    fun identificationDialog(introCode: (String?) -> Unit) {
        val layer = LayerDialogIdentificationBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setView(layer.root)
            setCancelable(false)
        }
        val dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        layer.apply {
            txtCancel.setOnClickListener {
                dialog.dismiss()
                introCode(null)
            }

            btnConfirm.setOnClickListener {
                if (edt.text.toString().isEmpty()) return@setOnClickListener
                introCode(edt.text.toString())
                dialog.dismiss()
            }
        }
    }

    fun moderatorForceKickPermission(

        user: InGameUsersDataEntity.InGameUserData,
        callback: (Boolean) -> Unit,
    ) {
        val layer =
            LayerDialogModeratorForceKickPermissionBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(layer.root)
        }
        val dialog = builder.create()
        dialog.bindToClosingDialog()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        layer.apply {
            imgUser.load(BASE_URL.plus(user.userImage))
            txtMsg.text = "بازیکن ${user.userName} توسط تیر شما کشته خواهد شد"
            btnConfirm.setOnClickListener {
                callback(true)
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                callback(false)
                dialog.dismiss()
            }
        }
    }

    fun qrCodeScannerRational(confirm: () -> Unit) {
        val layer = LayerDialogCameraPermissionBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(layer.root)
        }
        val dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        layer.apply {
            btnConfirm.setOnClickListener {
                confirm()
                dialog.dismiss()
            }
        }
    }

    fun downloadNewAppVersion(openLink:()->Unit){
        val layer = LayerDialogUpdateNewVersionBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).apply {
            setView(layer.root)
            setCancelable(false)
        }
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        layer.apply {
            txtDismiss.setOnClickListener {
                dialog.dismiss()
            }

            btnGoToLink.setOnClickListener {
                openLink()
                dialog.dismiss()
            }
        }

        dialog.show()
    }
    fun reportPlayer() {

    }

}