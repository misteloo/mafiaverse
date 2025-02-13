package ir.greendex.mafia.util.payment

import androidx.activity.result.ActivityResultRegistry
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.request.PurchaseRequest
import ir.greendex.mafia.util.payment.listeners.BazzarPaymentListener
import ir.greendex.mafia.util.payment.listeners.BazzarPurchaseRequestListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object BazzarPayment {
    private lateinit var paymentConnection: Connection
    private lateinit var purchaseRequestListener: BazzarPurchaseRequestListener
    private lateinit var paymentListener: BazzarPaymentListener
    private lateinit var payment: Payment
    private lateinit var activityResultRegistry: ActivityResultRegistry
    private var connectedToBazzar = false

    fun getPaymentConnection(): Boolean = connectedToBazzar
    private fun getActivityResultRegistry() = activityResultRegistry

    fun setActivityResultRegistry(activityResultRegistry: ActivityResultRegistry) {
        this.activityResultRegistry = activityResultRegistry
    }

    fun setPayment(payment: Payment) {
        this.payment = payment
    }

    fun setPaymentListeners(
        paymentListener: BazzarPaymentListener,
        purchaseRequestListener: BazzarPurchaseRequestListener
    ) {
        this.paymentListener = paymentListener
        this.purchaseRequestListener = purchaseRequestListener
    }


    fun connectToPayment() {
        paymentConnection = payment.connect {
            connectionSucceed {
                connectedToBazzar = true
                payment.getPurchasedProducts {
                    querySucceed {

                        CoroutineScope(Dispatchers.IO).launch {
                            it.forEach {
                                consumeProduct(token = it.purchaseToken)
                                delay(2000)
                            }

                        }

                    }
                }

            }

            connectionFailed {
                connectedToBazzar = false
                paymentListener.onFailureBazzar()
            }

            disconnected {
                connectedToBazzar = false
            }
        }
    }


    fun purchaseRequest(
        productId: String,
        payload: String
    ) {
        val purchaseRequest = PurchaseRequest(
            productId = productId,
            payload = payload
        )
        payment.purchaseProduct(registry = getActivityResultRegistry(), request = purchaseRequest) {
            failedToBeginFlow {
                purchaseRequestListener.onBazzarPurchaseError()
            }

            purchaseSucceed {
                purchaseRequestListener.onBazzarPurchaseSucceed(it = it)
            }

            purchaseFailed {
                purchaseRequestListener.onBazzarPurchaseFailed()
            }

            purchaseCanceled {
                purchaseRequestListener.onBazzarPurchaseCanceled()
            }

        }
    }

    suspend fun consumeProduct(
        token: String,
    ) = withContext(Dispatchers.IO) {
        payment.consumeProduct(purchaseToken = token) {
            consumeSucceed {
                paymentListener.onConsumeSucceed()
            }

            consumeFailed {
                paymentListener.onConsumeFailed()
            }

        }

    }

    fun disconnect() {
        if (::paymentConnection.isInitialized) {
            if (connectedToBazzar) paymentConnection.disconnect()
        }
    }

}