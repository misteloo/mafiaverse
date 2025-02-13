package ir.greendex.mafia.util.vibrate

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import javax.inject.Inject

class Vibrate @Inject constructor(private val vibrator: Vibrator) {

    fun normalVibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    200,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(
                200
            )
        }
    }

    fun longVibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    600,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(
                600
            )
        }
    }
}