package ir.greendex.mafia.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.config.PaymentConfiguration
import ir.cafebazaar.poolakey.config.SecurityCheck
import ir.greendex.mafia.util.BAZZAR_RSA
import ir.greendex.mafia.util.payment.BazzarPayment
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    @Singleton
    @Provides
    @Named("BAZZAR_RSA")
    fun provideBazzarRsa() = BAZZAR_RSA


    @Singleton
    @Provides
    fun provideSecurityCheck(@Named("BAZZAR_RSA") rsa: String): SecurityCheck.Enable {
        return SecurityCheck.Enable(rsaPublicKey = rsa)
    }

    @Singleton
    @Provides
    fun providePaymentConfiguration(securityCheck: SecurityCheck.Enable): PaymentConfiguration {
        return PaymentConfiguration(localSecurityCheck = securityCheck)
    }

    @Singleton
    @Provides
    fun providePayment(paymentConfiguration: PaymentConfiguration, context: Context): Payment {
        return Payment(context = context, config = paymentConfiguration)
    }


}