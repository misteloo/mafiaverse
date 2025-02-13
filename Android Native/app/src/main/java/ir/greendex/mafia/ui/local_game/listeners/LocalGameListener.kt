package ir.greendex.mafia.ui.local_game.listeners

interface LocalGameListener {
    fun onGetDeck(it:String)
    fun onBase64(it:String)
    fun onError(it:String)
    fun onCheckPrvDeck(it:String)
    fun onUsersJoining(it:String)
    fun onUsersJoined(it: String)
    fun onUserCharacter(it:String)
}