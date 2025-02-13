package ir.greendex.mafia.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.greendex.mafia.db.entity.MessageDbEntity

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMessages(item:List<MessageDbEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message:MessageDbEntity)

    @Query("SELECT * FROM MESSAGE_ENTITY WHERE channelId = :channelId")
    fun getAllMessages(channelId: String):List<MessageDbEntity>

    @Query("SELECT * FROM MESSAGE_ENTITY WHERE channelId = :channelId ORDER BY messageTime DESC LIMIT 15 OFFSET :offset")
    fun getMessagePaging(offset:Int , channelId:String):List<MessageDbEntity>

    @Query("SELECT * FROM MESSAGE_ENTITY")
    fun getAllMessages():List<MessageDbEntity>

}