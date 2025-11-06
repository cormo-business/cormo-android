package com.cormo.neulbeot.core

import android.content.Context
import com.cormo.neulbeot.auth.AccessTokenInterceptor
import com.cormo.neulbeot.auth.TokenAuthenticator
import com.cormo.neulbeot.auth.TokenStorage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    @Volatile private var retrofitInst: Retrofit? = null

    fun retrofit(context: Context): Retrofit {
        return retrofitInst ?: synchronized(this) {
            retrofitInst ?: buildRetrofit(context).also { retrofitInst = it }
        }
    }

    private fun buildRetrofit(context: Context): Retrofit {
        val storage = TokenStorage(context.applicationContext)

        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())   // ✅ 이 줄 추가
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(AccessTokenInterceptor(storage))  // ③ 헤더 부착
            .addInterceptor(logger)
            .authenticator(TokenAuthenticator(retrofitRaw(moshi), storage)) // ④ 401 재발급/재시도
            .build()

        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi ))
            .build()
    }

    /** Authenticator 생성 시 Retrofit 인스턴스가 필요해서 임시로 클라이언트 없는 Retrofit을 만듦 */
    private fun retrofitRaw(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // ✅ moshi 주입
            .build()
    }
}
