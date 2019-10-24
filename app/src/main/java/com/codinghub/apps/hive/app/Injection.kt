package com.codinghub.apps.hive.app

import com.codinghub.apps.hive.BuildConfig
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.repository.HiveApi
import com.codinghub.apps.hive.repository.RemoteRepository
import com.codinghub.apps.hive.repository.Repository
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {

    fun provideRepository(): Repository = RemoteRepository

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppPrefs.getServiceURL().toString())
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient())
            .build()
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {

        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    private fun provideOkHttpClient(): OkHttpClient {

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(provideLoggingInterceptor())
        val username = AppPrefs.getHeaderUserName().toString()
        val password = AppPrefs.getHeaderPassword().toString()

        httpClient.addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", Credentials.basic(username, password))
                .build()

            chain.proceed(request)
        }
        return httpClient.build()
    }

    fun provideHiveApi(): HiveApi {
        return provideRetrofit().create(HiveApi::class.java)
    }

}