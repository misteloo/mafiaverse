package ir.greendex.mafia.repository.local

import android.content.SharedPreferences
import dagger.hilt.android.scopes.ActivityRetainedScoped
import ir.greendex.mafia.db.dao.Dao
import ir.greendex.mafia.db.entity.MessageDbEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class LocalRepository @Inject constructor(
    private val editor: SharedPreferences.Editor,
    private val shared: SharedPreferences,
    private val dao: Dao,
) {
    suspend fun <T> storeData(key: String, value: T) = withContext(Dispatchers.IO) {
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
        }
        editor.apply()
    }

    suspend fun getStoreData(key: String, type: GetLocalRepositoryEnum) =
        withContext(Dispatchers.IO) {
            return@withContext when (type) {
                (GetLocalRepositoryEnum.String) -> shared.getString(key, "not_found")
                (GetLocalRepositoryEnum.Int) -> shared.getInt(key, -1)
                (GetLocalRepositoryEnum.Long) -> shared.getLong(key, -1L)
                (GetLocalRepositoryEnum.Float) -> shared.getFloat(key, -1f)
                (GetLocalRepositoryEnum.Boolean) -> shared.getBoolean(key, false)
                else -> null
            }
        }

    suspend fun contain(key: String) = withContext(Dispatchers.IO) {
        return@withContext shared.contains(key)
    }

    suspend fun remove(key : String) =  withContext(Dispatchers.IO) {
        editor.remove(key).apply()
    }


    fun insertAllMessage(messages: List<MessageDbEntity>) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertAllMessages(item = messages)
        }
    }

    fun insertMessage(message: MessageDbEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertMessage(message = message)
        }
    }

    suspend fun getAllMessage(channelId: String): Flow<List<MessageDbEntity>> {
        return flow {
            emit(dao.getAllMessages(channelId = channelId))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllMessageSize(channelId: String): Flow<Int> {
        return flow {
            val result = dao.getAllMessages(channelId = channelId)
            emit(result.size)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMessage(offset: Int, channelId: String): Flow<List<MessageDbEntity>> {
        return flow {
            emit(dao.getMessagePaging(offset = offset, channelId = channelId))
        }.flowOn(Dispatchers.IO)
    }
}


