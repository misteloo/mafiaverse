package ir.greendex.mafia.util.extension

import android.app.AlertDialog
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.CheckResult
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ir.greendex.mafia.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

fun Button.loading(progress: ProgressBar, enable: Boolean = false) {
    this.visibility = View.INVISIBLE
    this.isEnabled = enable
    progress.visibility = View.VISIBLE
}

fun Button.hideLoading(progress: ProgressBar, enable: Boolean = true) {
    progress.visibility = View.INVISIBLE
    this.visibility = View.VISIBLE
    this.isEnabled = enable
}

fun BottomSheetDialogFragment.bindToClosingSheet() {
    MainActivity.activeBottomSheet = this
}

fun AlertDialog.bindToClosingDialog() {
    MainActivity.activeDialog = this
}

infix fun ExtendedFloatingActionButton.disableDoubleClick(timer: Int) {
    CoroutineScope(Dispatchers.Main).launch {
        isEnabled = false
        delay(timer * 1000L)
        isEnabled = true
    }
}

infix fun FloatingActionButton.disableDoubleClick(timer: Int) {
    CoroutineScope(Dispatchers.Main).launch {
        isEnabled = false
        delay(timer * 1000L)
        isEnabled = true
    }
}

infix fun View.disableDoubleClick(timer:Int){
    CoroutineScope(Dispatchers.Main).launch {
        isEnabled = false
        delay(timer * 1000L)
        isEnabled = true
    }
}

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChangeFlow(): Flow<CharSequence?> {
    return callbackFlow {

        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.flowOn(Dispatchers.IO)
}


fun EditText.textChangeCallback(timeout: Long, callback: (String) -> Unit) {
    var job: Job? = null
    this.doAfterTextChanged {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(timeout)
            callback(it.toString())
        }
    }
}

/**
 * Returns a [String] number divided by ',' .
 */
fun CharArray.numberSeparator(): String {
    val builder = StringBuilder()
    var counter = 0
    var index: Int
    for (i in this.size - 1 downTo 0) {
        builder.append(this[i].toString())
        index = (i - (this.size - 1)) * -1
        counter++
        if (counter == 3 && index < (this.size - 1)) {
            builder.append(",")
            counter = 0
        }
    }
    return builder.reverse().toString()
}

/**
 * Returns a [String] containing span.
 */
fun String.span(): SpannableStringBuilder {
    val sb = SpannableStringBuilder(this)
    sb.setSpan(
        StrikethroughSpan(),
        0,
        this.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return sb
}

/**
 * show view with animation from bottom
 */
fun ViewGroup.showAnim(child: View, direction: Int = Gravity.BOTTOM) {
    val slide = Slide(direction).apply {
        duration = 200
        this.addTarget(child)
    }
    TransitionManager.beginDelayedTransition(this, slide)
    child.visibility = View.VISIBLE
}

/**
 * hide view with animation from bottom
 */
fun ViewGroup.hideAnim(child: View, direction: Int = Gravity.BOTTOM) {
    val slide = Slide(direction).apply {
        duration = 200
        this.addTarget(child)
    }
    TransitionManager.beginDelayedTransition(this, slide)
    child.visibility = View.GONE
}

fun String.badWords(): Boolean {
    val list = listOf(
        "kir",
        "kiri",
        "kos",
        "koskesh",
        "jakesh",
        "koslis",
        "kon",
        "koni",
        "jende",
        "jendeh",
        "sag",
        "madarjende",
        "madarjendeh",
        "madarjendeh",
        "binamos"
    )
    return list.contains(this)
}
