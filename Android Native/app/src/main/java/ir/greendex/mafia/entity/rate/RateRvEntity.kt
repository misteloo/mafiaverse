package ir.greendex.mafia.entity.rate

data class RateRvEntity(
    var index:Int,
    var userId:String,
    var image:String,
    var name:String,
    var gold:String,
    var rank:String,
    var place:Int = 0
)
