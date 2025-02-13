package ir.greendex.mafia.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.greendex.mafia.R
import ir.greendex.mafia.util.ENC_KEY
import ir.greendex.mafia.util.ENC_KEY_NAME
import ir.greendex.mafia.util.Encryption
import ir.greendex.mafia.util.NOTIFICATION_CHANNEL_ID
import ir.greendex.mafia.util.NOTIFICATION_CHANNEL_ID_NAME
import org.json.JSONObject
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClsModule {

    @Provides
    @Singleton
    @Named(ENC_KEY_NAME)
    fun provideEncryptionKey(): String {
        return ENC_KEY
    }

    @Provides
    @Singleton
    @Named(NOTIFICATION_CHANNEL_ID_NAME)
    fun provideNotificationChannelIdName(): String = NOTIFICATION_CHANNEL_ID

    @Provides
    @Singleton
    fun provideEncryption(@Named(ENC_KEY_NAME) enc: String): Encryption = Encryption(enc)


    @Provides
    fun provideGson(): Gson = Gson()


    @Provides
    fun provideJsonObject(): JsonObject = JsonObject()

    @Provides
    fun provideJsonObjectForSocket(): JSONObject = JSONObject()


    @Provides
    @Singleton
    fun provideNotificationBuilder(
        context: Context,
        @Named(NOTIFICATION_CHANNEL_ID_NAME) channelId: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.mafia_icon)
            .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT).setSilent(true)

    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        context: Context,
        @Named(NOTIFICATION_CHANNEL_ID_NAME) channelId: String
    ): NotificationManagerCompat {
        val manager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "ready game", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(notificationChannel)
        }
        return manager
    }

    @Provides
    @Singleton
    fun provideMediaPlayer():MediaPlayer = MediaPlayer()

}