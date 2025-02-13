package ir.greendex.mafia.game.general.listener

import ir.greendex.mafia.entity.game.general.UserQueueToPickEntity

interface SelectCharacterListener {
    fun onYourTurnToPick()
    fun onRandomCharacter(it: String)
    fun onUsersTurnToPick(it: List<UserQueueToPickEntity.UserQueueToPickData>)
    fun onAbandon()
}