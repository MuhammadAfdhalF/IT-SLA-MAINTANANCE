package com.acuy.sla_maintenance

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.acuy.sla_maintenance.adapter.PendingAdapter
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.ActivityDetailReportBinding
import com.acuy.sla_maintenance.databinding.ActivityDetailReportJamBinding
import com.acuy.sla_maintenance.model.DataId
import com.acuy.sla_maintenance.model.DataItemIdWorker
import com.acuy.sla_maintenance.model.GetTollById
import com.acuy.sla_maintenance.model.IdWorkerResponse
import com.bumptech.glide.Glide
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HalamanDetailReportJam : AppCompatActivity() {
    private lateinit var binding: ActivityDetailReportJamBinding
    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager
    private lateinit var pendingAdapter: PendingAdapter

    private var reportId: Int = 0
    private lateinit var lokasi: String
    private lateinit var catatanHandle: String

    private lateinit var status: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReportJamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reportId = intent.getIntExtra("EXTRA_ACTIVITY_ID", -1)
        Log.i("ReportID di detail", "Report ID: $reportId")

        lokasi = intent.getStringExtra("locationName").toString()
        Log.i("lokasi", "lokasi: $lokasi")

        catatanHandle = intent.getStringExtra("catatan") ?: "-"
        Log.i("catatan", "catatan: $catatanHandle")

        //post user
        apiServices = NetworkConfig().getServices()
        sharedPrefManager = SharedPrafManager(this)
        // Memanggil fungsi untuk mendapatkan data pekerja kegiatan berdasarkan ID laporan
        getIdWorker(reportId)

        binding.panggilTotalKeseluruhan.visibility = View.GONE
        binding.txtTotalKeseluruhan.visibility = View.GONE

        getRvPending(reportId)


        // Memanggil fungsi untuk mendapatkan data tol berdasarkan ID
        getTollById(reportId)

        //hilanngin button back and handle
        binding.btnBackReport.visibility = View.GONE
        binding.btnHandledReport.visibility = View.GONE


    }


    private fun getTollById(id: Int) {
        val token = getTokenFromSharedPreferences()
        if (token != null) {
            val tokenInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(NetworkConfig().BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            val apiServicesWithToken = retrofit.create(ApiServices::class.java)

            apiServicesWithToken.getTollById(id).enqueue(object : Callback<GetTollById> {
                override fun onResponse(call: Call<GetTollById>, response: Response<GetTollById>) {
                    if (response.isSuccessful) {
                        val tollData = response.body()?.data
                        if (tollData != null) {
                            displayTollData(tollData)
                            Log.i("getTollById", "Done Boss Q")

                        } else {
                            Log.e("getTollById", "No data found")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = errorBody ?: "Unknown error"
                        Log.e("getTollById", "Response not successful: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<GetTollById>, t: Throwable) {
                    Log.e("getTollById", "Failed to get toll data: ${t.message}", t)
                }
            })
        } else {
            Log.e("Token", "Token is null")
        }
    }


    private fun displayTollData(dataId: DataId) {
        // Mendapatkan data dari dataId
        val tollData =
            dataId.data?.firstOrNull() // Asumsikan kita hanya mengambil item pertama dalam daftar
        tollData?.let {


            // Mendapatkan status dari data
            status = it.status ?: ""

            // Menetapkan status dan menerapkan logika sesuai
            if (status == "done") {
                binding.btnHandledReport.isEnabled = false
                binding.btnHandledReport.isClickable = false
                binding.btnHandledReport.alpha = 0.5f

                binding.panggilTotalKeseluruhan.visibility = View.VISIBLE
                binding.txtTotalKeseluruhan.visibility = View.VISIBLE
            } else if (status != "pending") {
                binding.rvWorker.visibility = View.GONE
                binding.garis2.visibility = View.GONE

            }


// Definisikan format asli dan format target
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            originalFormat.timeZone = TimeZone.getTimeZone("UTC") // Sesuaikan dengan zona waktu database

            val targetFormat = SimpleDateFormat("dd MMMM yyyy '||' HH:mm:ss", Locale.getDefault())
            targetFormat.timeZone = TimeZone.getDefault() // Gunakan zona waktu perangkat

// Konversi tanggal dari format asli ke format target
            val createdAt = it.createdAt
            val formattedDate = createdAt?.let {
                try {
                    val date = originalFormat.parse(createdAt)
                    date?.let { targetFormat.format(date) }
                } catch (e: ParseException) {
                    e.printStackTrace()
                    "-"
                }
            } ?: "-"

            binding.panggilReportDate.text = formattedDate

            binding.panggilLokasiReport.text = it.locationName ?: "-"
            binding.panggilContentReport.text = it.catatan?.toString() ?: "-"
//            binding.panggilReportDate.text = it.createdAt ?: "-"
            binding.panggilCompany.text = it.company ?: "-"
            binding.panggilJenisHardware.text = it.jenisHardware ?: "-"
            binding.panggilUraianHardware.text = it.uraianHardware ?: "-"
            binding.panggiJenisSoftware.text = it.standartAplikasi?.toString() ?: "-"
            binding.panggilUraianSoftware.text = it.uraianAplikasi?.toString() ?: "-"
            binding.panggilCatgory.text = it.kategoriActivity ?: "-"
            binding.panggilItTol.text = it.aplikasiItTol?.toString() ?: "-"
            binding.panggilUraianItTol.text = it.uraianItTol?.toString() ?: "-"
            binding.panggilBiaya.text = it.biaya?.toString() ?: "0"
            binding.panggilShift.text = it.shift ?: "-"
            binding.panggilPelaporDetail.text = it.namaUser ?: "-"
            binding.panggilKondisiAKhir.text = it.kondisiAkhir ?: "-"
            status

            val images = Images()
            val imageUrlAwal = "${images.BASE_URL}${it.fotoAwal}"
            // Memuat gambar dengan Glide
            Glide.with(this)
                .load(imageUrlAwal)
                .placeholder(R.drawable.clear)
                .error(R.drawable.clear) // Menggunakan gambar yang sama untuk placeholder dan error
                .into(binding.imgReports)

            val imageUrlAkhir = "${images.BASE_URL}${it.fotoAkhir}"
            Glide.with(this)
                .load(imageUrlAkhir)
                .placeholder(R.drawable.clear)
                .error(R.drawable.clear)
                .into(binding.fotoAkhirDetail)

            // Mengonversi durasi kerja menjadi format jam dan menit
            val workDuration = it.waktuPengerjaan
            val waktuHandleText = workDuration?.let {
                val parts = it.split(":")
                if (parts.size == 3) {
                    "${parts[0]} jam ${parts[1]} menit ${parts[2]} detik"
                } else {
                    "Format waktu tidak valid"
                }
            } ?: "-"
            binding.panggilTotalKeseluruhan.text = waktuHandleText


        }
    }


    //pending
    private fun getRvPending(reportId: Int) {
        val token = getTokenFromSharedPreferences()
        val apiServices = NetworkConfig().getServices()

        val tokenInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConfig().BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val apiServicesWithToken = retrofit.create(ApiServices::class.java)

        // Panggil endpoint yang sesuai dengan layanan API yang baru
        // Gunakan reportId sebagai parameter dalam pemanggilan API
        apiServicesWithToken.getIdWorker(reportId).enqueue(object : Callback<IdWorkerResponse> {
            override fun onResponse(
                call: Call<IdWorkerResponse>,
                response: Response<IdWorkerResponse>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    // Di sini Anda dapat mengakses data yang diperlukan dari respons
                    val dataList = responseData?.data
                        ?: emptyList() // Gunakan operator elvis untuk mengatasi kemungkinan null
                    // Panggil fungsi showRvPending dengan dataList yang diterima
                    showRvPending(dataList)
                } else {
                    // Handle unsuccessful response here
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody ?: "Unknown error"
                    Log.e("Retrofit onResponse", "Response not successful: $errorMessage")
                }
            }

            override fun onFailure(call: Call<IdWorkerResponse>, t: Throwable) {
                // Handle failure here
                Log.e("Retrofit onFailure", "Failed to get worker data: ${t.message}", t)
            }
        })
    }

    private fun showRvPending(dataList: List<DataItemIdWorker?>) {
        // Filter item dengan status "pending" dan buang yang null
        val filteredList = dataList.filter { it?.status == "pending" }

        binding.rvWorker.layoutManager = LinearLayoutManager(this@HalamanDetailReportJam)
        pendingAdapter = PendingAdapter(filteredList as List<DataItemIdWorker>)
        binding.rvWorker.adapter = pendingAdapter
    }

    //handle
    private fun getIdWorker(reportId: Int) {
        val token = getTokenFromSharedPreferences() // Mendapatkan token dari SharedPreferences
        if (token != null) {
            val apiServices = NetworkConfig().getServices()

            // Buat interceptor untuk menyertakan token dalam header permintaan
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

            // Panggil endpoint yang sesuai dengan layanan API yang baru
            apiServicesWithToken.getIdWorker(reportId)
                .enqueue(object : Callback<IdWorkerResponse> {
                    override fun onResponse(
                        call: Call<IdWorkerResponse>,
                        response: Response<IdWorkerResponse>
                    ) {
                        if (response.isSuccessful) {
                            val idWorkerResponse = response.body()
                            val dataItems = idWorkerResponse?.data
                            if (dataItems != null) {
                                // Filter out null items
                                val nonNullDataItems = dataItems.filterNotNull()
                                displayDoneData(nonNullDataItems)
                                Log.i("Retrofit onResponse", "Response successful API Worker")
                                Log.i(
                                    "Retrofit onResponse",
                                    "Response successful API Worker: $nonNullDataItems"
                                )
                            }
                        } else {
                            Log.e("Retrofit onResponse", "Response not successful")
                            // Handle error response here
                            // Misalnya, menampilkan pesan kesalahan kepada pengguna
                            Toast.makeText(
                                applicationContext,
                                "Failed to get data: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<IdWorkerResponse>, t: Throwable) {
                        Log.e("Retrofit onFailure", "onFailure: ${t.stackTrace}")
                        // Handle failure here
                        // Misalnya, menampilkan pesan kesalahan kepada pengguna
                        Toast.makeText(
                            applicationContext,
                            "Error getting data: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            // Token null, tindakan yang sesuai di sini
            Log.e("Token", "Token is null")
            // Handle null token here
        }
    }


    //    done
    private fun bindDataHandle(dataItemIdWorker: DataItemIdWorker) {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        originalFormat.timeZone = TimeZone.getTimeZone("UTC") // Sesuaikan dengan zona waktu database

        val targetFormat = SimpleDateFormat("dd MMMM yyyy '||' HH:mm:ss", Locale.getDefault())
        targetFormat.timeZone = TimeZone.getDefault() // Gunakan zona waktu perangkat

        if (dataItemIdWorker.status.equals("done", ignoreCase = true)) {
            val username = findViewById<TextView>(R.id.panggilUsernameHandled)
            username.text = dataItemIdWorker.username ?: "-"

            // Tanggal Report Pending
            val tanggalReportPendingTextView = findViewById<TextView>(R.id.panggilTanggalReport)
            tanggalReportPendingTextView.text = dataItemIdWorker.startTime?.let {
                try {
                    val date = originalFormat.parse(it)
                    date?.let { targetFormat.format(date) } ?: "-"
                } catch (e: Exception) {
                    "-"
                }
            } ?: "-"

            // Tanggal Selesai Pending
            val tanggalSelesaiPendingTextView = findViewById<TextView>(R.id.panggilTanggalSelesai)
            tanggalSelesaiPendingTextView.text = dataItemIdWorker.endTime?.let {
                try {
                    val date = originalFormat.parse(it)
                    date?.let { targetFormat.format(date) } ?: "-"
                } catch (e: Exception) {
                    "-"
                }
            } ?: "-"

            // Waktu Handle
            val waktuHandlePendingTextView = findViewById<TextView>(R.id.panggilWaktuHandle)
            val workDuration = dataItemIdWorker.workDuration
            val waktuHandleText = workDuration?.let {
                val parts = it.split(":")
                if (parts.size == 3) {
                    "${parts[0]} jam ${parts[1]} menit ${parts[2]} detik"
                } else {
                    "Format waktu tidak valid"
                }
            } ?: "-"
            waktuHandlePendingTextView.text = waktuHandleText
        }
        else {
            // Jika status bukan "done", kosongkan semua teks dengan "-"
            val username = findViewById<TextView>(R.id.panggilUsernameHandled)
            username.text = "-"

            val tanggalReportPendingTextView = findViewById<TextView>(R.id.panggilTanggalReport)
            tanggalReportPendingTextView.text = "-"

            val tanggalSelesaiPendingTextView = findViewById<TextView>(R.id.panggilTanggalSelesai)
            tanggalSelesaiPendingTextView.text = "-"

            val waktuHandlePendingTextView = findViewById<TextView>(R.id.panggilWaktuHandle)
            waktuHandlePendingTextView.text = "-"
        }
    }

    // Filter dan bind data hanya untuk item dengan status "done"
    private fun displayDoneData(dataItems: List<DataItemIdWorker>) {
        dataItems.filter { it.status.equals("done", ignoreCase = true) }
            .forEach { dataItem ->
                bindDataHandle(dataItem)
            }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPrefManager = SharedPrafManager(this)
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }
}