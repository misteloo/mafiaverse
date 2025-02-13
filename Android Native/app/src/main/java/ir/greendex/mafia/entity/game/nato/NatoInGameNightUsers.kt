package ir.greendex.mafia.entity.game.nato

data class NatoInGameNightUsers(
    val userName: String,
    val userId: String,
    val userImage: String,
    var hasGun: Boolean = false,
    val gunKind: String? = null,
    val natoAct: Boolean = false,
    val guessCharacter: NatoCharacters? = null
)
