package com.acuy.sla_maintenance.config
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val sharedPrefManager: SharedPrafManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Ambil token dari header jika ada
        val token = response.header("Authorization")

        // Simpan token jika tidak null
        if (token != null) {
            sharedPrefManager.put(Constant.PREFS_TOKEN_FILE, token)
        }

        return response
    }
}
