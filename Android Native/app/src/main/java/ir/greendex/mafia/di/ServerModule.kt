package ir.greendex.mafia.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import io.socket.client.Socket
import ir.greendex.mafia.repository.Api
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.BASE_URL_NAME
import ir.greendex.mafia.util.SOCKET_URL
import ir.greendex.mafia.util.SOCKET_URL_NAME
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerModule {

    @Provides
    @Singleton
    @Named(BASE_URL_NAME)
    fun provideBaseUrl() = BASE_URL


    @Provides
    @Singleton
    @Named(SOCKET_URL_NAME)
    fun provideSocketUrl() = SOCKET_URL


    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named(BASE_URL_NAME) baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder().client(okHttpClient).baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }

    @Provides
    @Singleton
    fun provideSocket(@Named(SOCKET_URL_NAME) url: String): Socket {
        return IO.socket(url)
    }

}