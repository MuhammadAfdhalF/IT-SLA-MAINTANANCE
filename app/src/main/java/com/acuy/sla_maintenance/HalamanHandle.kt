package com.acuy.sla_maintenance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.ActivityHandleBinding
import com.acuy.sla_maintenance.model.ProsesResponse
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HalamanHandle : AppCompatActivity() {
    private lateinit var binding: ActivityHandleBinding
    private var reportId: Int = 0
    private lateinit var lokasi: String
    private lateinit var catatanHandle: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHandleBinding.inflate(layoutInflater)
        setContentView(binding.root)

         reportId = intent.getIntExtra("id", -1)
        Log.i("ReportID di Handle", "Report ID: $reportId")

        lokasi = intent.getStringExtra("locationName").toString()
        catatanHandle = intent.getStringExtra("catatan").toString()

        status = intent.getStringExtra("status").toString()
        Log.i("status", "status: $status")
        binding.catatanMaintanance.text = catatanHandle ?: "Tidak Ada Catatan Ditinggalkan Teknisi"
        binding.lokasiMaintanance.text = lokasi ?: "-"



//        perbaikan

        val btnPerbaikan = binding.btnPerbaikan
        btnPerbaikan.setOnClickListener {
            if (status != "process") {
                postProses(reportId)
            }
            val intent = Intent(this@HalamanHandle, HalamanMaintananceProcces::class.java).apply {
                putExtra("id", reportId) // Memasukkan ID report ke intent
            }
            startActivity(intent)
            finish()
        }
//        val btnPerbaikan = binding.btnPerbaikan
//        btnPerbaikan.setOnClickListener{
//            postProses(reportId)
//            val intent = Intent(this@HalamanHandle, HalamanMaintananceProcces::class.java).apply {
//                putExtra("id", reportId) // Memasukkan ID report ke intent
//            }
//            startActivity(intent)
//            finish()
//        }


//        cancel
        val btnCancel = binding.btnCancel
        btnCancel.setOnClickListener{
            startActivity(Intent(this@HalamanHandle,HalamanDetailReport::class.java))
            finish()
        }

    }

    private fun postProses(reportId: Int) {
        val authToken = getTokenFromSharedPreferences()

        authToken?.let { token ->
            val tokenInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }

            // Tambahkan interceptor ke OkHttpClient
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()

            // Buat retrofit baru dengan OkHttpClient yang telah dikonfigurasi
            val retrofit = Retrofit.Builder()
                .baseUrl(NetworkConfig().BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            // Buat instance layanan API dengan retrofit yang telah dikonfigurasi
            val apiServicesWithToken = retrofit.create(ApiServices::class.java)

            // Membuat request body untuk activityId
            val activityIdRequestBody = reportId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Mengirim permintaan POST
            apiServicesWithToken.postProses(activityIdRequestBody).enqueue(object : Callback<ProsesResponse> {
                override fun onResponse(call: Call<ProsesResponse>, response: Response<ProsesResponse>) {
                    if (response.isSuccessful) {
                        val prosesResponse = response.body()
                        val message = "Permintaan berhasil: ${prosesResponse?.message}\n" +
                                "Start Time: ${prosesResponse?.data?.startTime}\n" +
                                "User ID: ${prosesResponse?.data?.userId}\n" +
                                "Activity ID: ${prosesResponse?.data?.activityId}\n" +
                                "Created At: ${prosesResponse?.data?.createdAt}\n" +
                                "ID: ${prosesResponse?.data?.id}"
                        Log.i("HalamanHandle", message)
                    } else {
                        // Handle unsuccessful response
                        Log.e("HalamanHandle", "Gagal mengirim permintaan: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ProsesResponse>, t: Throwable) {
                    // Handle failure here
                    Log.e("HalamanHandle", "Error: ${t.message}", t)
                }
            })
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPrefManager = SharedPrafManager(this)
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }
}