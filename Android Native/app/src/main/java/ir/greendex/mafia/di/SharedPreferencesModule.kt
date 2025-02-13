package ir.greendex.mafia.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.greendex.mafia.util.PREFERENCES_NAME
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Singleton
    @Named(PREFERENCES_NAME)
    fun providePreferenceName(): String = PREFERENCES_NAME

    @Provides
    @Singleton
    fun providePreference(
        context: Context,
        @Named(PREFERENCES_NAME) sharedName: String,
    ): SharedPreferences {
        return context.getSharedPreferences(sharedName, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePreferencesEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }
}