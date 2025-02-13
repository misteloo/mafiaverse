package ir.greendex.mafia.game.general.listener

import ir.greendex.mafia.entity.game.general.EndGameFreeSpeechEntity

interface EndGameVmListener {
    fun onSpeech(data:List<EndGameFreeSpeechEntity.EndGameFreeSpeechData>)
}