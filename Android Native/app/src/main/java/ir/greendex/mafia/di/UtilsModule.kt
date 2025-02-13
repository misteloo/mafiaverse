package ir.greendex.mafia.di

import android.app.AlarmManager
import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.Vibrator
import android.telephony.TelephonyManager
import android.view.ContextThemeWrapper
import android.widget.PopupMenu
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.greendex.mafia.R
import kotlinx.coroutines.*
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    @Suppress("DEPRECATION")
    fun provideVibrator(context: Context): Vibrator {
        return context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    @Provides
    @Singleton
    fun provideSupervisor(): CompletableJob = SupervisorJob()

    @Provides
    @Singleton
    fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideTelephonyManager(context: Context):TelephonyManager{
        return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }


    @Provides
    @Singleton
    fun provideContextWrapper(context: Context): ContextThemeWrapper {
        return ContextThemeWrapper(context, R.style.popupMenuRtl)
    }

    @Provides
    @Named("main_context")
    fun provideMainContext(completableJob: CompletableJob): CoroutineContext {
        return completableJob + Dispatchers.Main
    }

    @Provides
    @Named("io_context")
    fun provideIoContext(completableJob: CompletableJob): CoroutineContext {
        return completableJob + Dispatchers.IO
    }

    @Provides
    @Named("main_scope")
    fun provideMainScope(@Named("main_context") context: CoroutineContext): CoroutineScope {
        return CoroutineScope(context)
    }

    @Provides
    @Singleton
    @Named("io_scope")
    fun provideIoScope(@Named("io_context") context: CoroutineContext): CoroutineScope {
        return CoroutineScope(context)
    }

    @Provides
    @Singleton
    fun provideAudioService(context: Context): AudioManager {
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @Singleton
    fun providePopupMenu(context: Context):PopupMenu{
        return PopupMenu(context,null).apply {
            inflate(R.menu.bottom_nav_menu)
        }
    }

}