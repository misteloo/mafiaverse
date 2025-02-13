package ir.greendex.mafia.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.greendex.mafia.db.dao.Dao
import ir.greendex.mafia.db.database.MafiaDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(context: Context): MafiaDatabase {
        return Room.databaseBuilder(context, MafiaDatabase::class.java, "mafiia_db")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideDao(mafiaDatabase: MafiaDatabase): Dao {
        return mafiaDatabase.dao()
    }
}