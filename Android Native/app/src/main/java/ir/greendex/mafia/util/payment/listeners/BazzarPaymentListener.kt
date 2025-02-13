package ir.greendex.mafia.util.payment.listeners

interface BazzarPaymentListener {

    fun onFailureBazzar()
    fun onConsumeSucceed()
    fun onConsumeFailed()
}