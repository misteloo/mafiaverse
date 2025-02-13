package ir.greendex.mafia.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.livekit.android.AudioOptions
import io.livekit.android.AudioType
import io.livekit.android.LiveKit
import io.livekit.android.LiveKitOverrides
import io.livekit.android.room.Room
import ir.greendex.mafia.util.voice.VoiceManager
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KitModule {
    @Provides
    @Singleton
    fun provideAudioOutput(): AudioType {
        return AudioType.MediaAudioType()
    }
    @Provides
    @Singleton
    fun provideLiveKitAudioOptions(audioType: AudioType): AudioOptions {
        return AudioOptions(audioOutputType = audioType)
    }
    @Provides
    @Singleton
    fun provideLiveKitOverRides(audioOutputOption: AudioOptions): LiveKitOverrides {
        return LiveKitOverrides(audioOptions = audioOutputOption)
    }
    @Provides
    @Singleton
    @Named("kit_room")
    fun provideKit(context: Context, kitOverrides: LiveKitOverrides): Room {
        return LiveKit.create(appContext = context, overrides = kitOverrides)
    }

    @Provides
    @Singleton
    fun provideVoiceManager(@Named("kit_room") room: Room): VoiceManager {
        return VoiceManager(room)
    }
}