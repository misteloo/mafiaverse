package ir.greendex.mafia.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.greendex.mafia.util.MESSAGE_ENTITY
import ir.greendex.mafia.util.NOT_FOUND

@Entity(MESSAGE_ENTITY)
data class MessageDbEntity(
    val channelId:String,
    val messageId:String,
    val userId:String,
    val userName:String,
    @ColumnInfo(defaultValue = NOT_FOUND)
    val userImage:String,
    val message:String,
    val messageType:String,
    val messageTime:Long,
    val userState:String,
    @ColumnInfo(defaultValue = NOT_FOUND)
    val responseMessage:String,
    @ColumnInfo(defaultValue = NOT_FOUND)
    val responseUserId:String
){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}
