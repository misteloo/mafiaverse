package ir.greendex.mafia.game.nato.listeners

import ir.greendex.mafia.entity.game.nato.NatoInGameNightUsers

interface NatoNightListener {
    fun onNightActTimeUp()
    fun onNightActResult(users:List<NatoInGameNightUsers>,mafiaShot:Boolean)
}