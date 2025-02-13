package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class InGameTurnSpeechEntity(
    @SerializedName("data")
    val data: InGameTurnSpeechQueue
) {
    data class InGameTurnSpeechQueue(
        @SerializedName("queue")
        val queue: List<InGameTurnSpeechQueueData>,
        @SerializedName("can_take_challenge")
        val canTakeChallenge: Boolean,
        @SerializedName("timer")
        val timer: Int
    )

    data class InGameTurnSpeechQueueData(
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("user_index")
        val userIndex: Int,
        @SerializedName("speech_status")
        val speechStatus: String,
        @SerializedName("challenge_used")
        val challengeUsed: Boolean,
        @SerializedName("pass")
        val pass: Boolean
    )
}
