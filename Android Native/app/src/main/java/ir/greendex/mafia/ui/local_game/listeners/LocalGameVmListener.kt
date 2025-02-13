package ir.greendex.mafia.ui.local_game.listeners

import ir.greendex.mafia.entity.local.LocalBase64Entity
import ir.greendex.mafia.entity.local.LocalCharacterEntity
import ir.greendex.mafia.entity.local.LocalErrorEntity
import ir.greendex.mafia.entity.local.LocalUserCharacterEntity
import ir.greendex.mafia.entity.local.LocalUsersJoinedEntity
import ir.greendex.mafia.entity.local.LocalUsersJoiningEntity

interface LocalGameVmListener {
    fun onGetDeck(data:LocalCharacterEntity)
    fun onBase64(data:LocalBase64Entity.LocalBase64Data)
    fun onError(msg:LocalErrorEntity.LocalErrorMsg)
    fun onCheckPrvDeck(data:List<LocalCharacterEntity.LocalCharacterDeck>)
    fun onUsersJoining(data: LocalUsersJoiningEntity.UsersJoinedToLocalData)
    fun onUsersJoined(data:List<LocalUsersJoinedEntity.LocalJoinedUsersDetail>)
    fun onUserCharacter(data:LocalCharacterEntity.LocalCharacterDeck)
}