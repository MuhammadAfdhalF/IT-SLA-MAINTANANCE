package com.acuy.sla_maintenance.config

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.acuy.sla_maintenance.config.Constant.Companion.USER_TOKEN
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


//layanan retrofit untuk permintaan ke API
object TokenManager {
    var token: String = ""
}

class NetworkConfig {

    // local
//    val BASE_URL: String = "http://10.0.2.2:8000/api/"

    //server
    val BASE_URL: String = "http://mksapi.margautamanusantara.com/it_sla_mnt/be/public/api/"


    //     Membuat interceptor untuk menambahkan token ke header permintaan
    val interceptor = Interceptor { chain ->

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${TokenManager.token}")
            .build()
        chain.proceed(request)
    }

    //    fungsi untuk permintaan jaringan oleh retrofit
    private fun setOkHttp(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BASIC
        ).setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .callTimeout(15L, TimeUnit.SECONDS)
            .build()
    }

    //    mengkonfigurasi objek retrofit
    private fun setRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(setOkHttp())
            .build()
    }

    fun getServices(): ApiServices {
        return setRetrofit().create(ApiServices::class.java)
    }
}


//class NetworkConfig {
//    val BASE_URL: String = "http://10.0.2.2:8000/api/"
//    private var authToken: String? = null
//
//
//    //    fungsi untuk permintaan jaringan oleh retrofit
//    private fun setOkHttp(): OkHttpClient {
//        val interceptor = HttpLoggingInterceptor().setLevel(
//            HttpLoggingInterceptor.Level.BASIC
//        ).setLevel(HttpLoggingInterceptor.Level.BODY)
//
//        return OkHttpClient.Builder()
//            .addInterceptor(interceptor)
//            .callTimeout(15L, TimeUnit.SECONDS)
//            .build()
//    }
//
//    //    mengkonfigurasi objek retrofit
//    private fun setRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(setOkHttp())
//            .build()
//    }
//
//    fun getServices(): ApiServices {
//        return setRetrofit().create(ApiServices::class.java)
//    }
//}


//class NetworkConfig {
//    private val BASE_URL: String = "http://10.0.2.2:8000/api/"
//    private var authToken: String? = null
//
//    private fun setOkHttp(): OkHttpClient {
//        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//
//        return OkHttpClient.Builder()
//            .addInterceptor(interceptor)
//            .addInterceptor(TokenInterceptor())
//            .callTimeout(15L, TimeUnit.SECONDS)
//            .build()
//    }
//
//    private fun setRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(setOkHttp())
//            .build()
//    }
//
//    fun getServices(): ApiServices {
//        return setRetrofit().create(ApiServices::class.java)
//    }
//
//    fun setToken(token: String?) {
//        this.authToken = token
//    }
//
//    fun getToken(): String? {
//        return authToken
//    }
//
//    inner class TokenInterceptor : Interceptor {
//        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
//            val originalRequest = chain.request()
//            val token = getToken()
//            val request = if (token != null) {
//                originalRequest.newBuilder()
//                    .header("Authorization", "Bearer $token")
//                    .build()
//            } else {
//                originalRequest
//            }
//            return chain.proceed(request)
//        }
//    }
//}
//











