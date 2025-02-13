package ir.greendex.mafia.game.nato.listeners

interface NatoDayActionListener {
    fun onLike()
    fun onDislike()
    fun onChallengeReq()
    fun onChallengeAccepted(userId:String)
    /**
    * Return [Unit] current player wont talk anymore .
    * */
    fun onNextPlayer()
    fun onSubmitVote()

    // below callback for accepting on user who want talk about us
    /**
     * For accepting which user talk about my self in defencive state.
     * */
    fun onSubmitHandRiseToTargetCover(userId: String)
    /**
     * User telling to other players , i've gun .
     * */
    fun onDayUsingGun()
    fun onDayUsingGunTimUp()
}