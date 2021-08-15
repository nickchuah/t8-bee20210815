package com.tiltedeight.android.bee.ws

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class WS {

    companion object {
        var okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(1, TimeUnit.DAYS)
                .readTimeout(1, TimeUnit.DAYS)
                .writeTimeout(1, TimeUnit.DAYS)
                .build()

        val TiltedEight = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://te.linkpluscard.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TiltedEightService::class.java)
    }
}