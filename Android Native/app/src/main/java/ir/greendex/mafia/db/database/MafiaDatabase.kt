package ir.greendex.mafia.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.greendex.mafia.db.dao.Dao
import ir.greendex.mafia.db.entity.MessageDbEntity

@Database(
    entities = [MessageDbEntity::class],
    version = 4,
    exportSchema = false,
)
abstract class MafiaDatabase : RoomDatabase() {
    abstract fun dao(): Dao
}