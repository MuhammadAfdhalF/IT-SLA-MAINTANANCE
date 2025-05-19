package com.acuy.sla_maintenance.UploadToll

import com.acuy.sla_maintenance.model.SubmitReport
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Interface yang mendefinisikan endpoint-endpoint untuk berinteraksi dengan API
interface MyApi2 {

    // Fungsi untuk mengunggah data tol ke server menggunakan metode POST multipart
    @Multipart
    @POST("api/toll") // Endpoint untuk mengunggah data tol
    fun postToll(
        @Part("company") company: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("jenis_hardware") jenis_hardware: RequestBody,
        @Part("uraian_hardware") uraian_hardware: RequestBody,
        @Part("standart_aplikasi") standart_aplikasi: RequestBody,
        @Part("uraian_aplikasi") uraian_aplikasi: RequestBody,
        @Part("aplikasi_it_tol") aplikasi_it_tol: RequestBody,
        @Part("uraian_it_tol") uraian_it_tol: RequestBody,
        @Part("catatan") catatan: RequestBody,
        @Part("shift") shift: RequestBody,
        @Part("lokasi_id") lokasi_id: RequestBody,
        @Part("kategori_id") kategori_id: RequestBody,
        @Part("biaya") biaya: RequestBody,
        @Part foto_awal: MultipartBody.Part,
        @Part("status") status: String
    ): Call<SubmitReport>

    // Companion object untuk membuat instance dari MyApi2 menggunakan Retrofit
    companion object {
        // Fungsi invoke untuk membuat instance MyApi2
        operator fun invoke(): MyApi2 {
            // Membangun Retrofit instance dengan konfigurasi baseUrl dan converter factory Gson
            return Retrofit.Builder()
                .baseUrl("http://127.0.0.1:8000/") // Base URL dari API
                .addConverterFactory(GsonConverterFactory.create()) // Converter factory untuk mengubah JSON menjadi objek Kotlin
                .build()
                .create(MyApi2::class.java) // Membuat instance MyApi2 dari Retrofit
        }
    }
}
