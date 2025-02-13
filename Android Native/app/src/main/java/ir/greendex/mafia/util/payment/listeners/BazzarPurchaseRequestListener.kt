package ir.greendex.mafia.util.payment.listeners

import ir.cafebazaar.poolakey.entity.PurchaseInfo

interface BazzarPurchaseRequestListener {
    fun onBazzarPurchaseFailed()
    fun onBazzarPurchaseSucceed(it: PurchaseInfo)
    fun onBazzarPurchaseCanceled()
    fun onBazzarPurchaseError()
}