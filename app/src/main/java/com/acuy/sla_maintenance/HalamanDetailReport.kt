package com.acuy.sla_maintenance

import android.content.Intent
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
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.ActivityDetailReportBinding
import com.acuy.sla_maintenance.model.DataItemIdWorker
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
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HalamanDetailReport : AppCompatActivity() {
    private lateinit var binding: ActivityDetailReportBinding
    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager
    private lateinit var pendingAdapter: PendingAdapter

    private var reportId: Int = 0
    private lateinit var lokasi: String
    private lateinit var catatanHandle: String
    private lateinit var status: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reportId = intent.getIntExtra("id", -1)
        Log.i("ReportID di detail", "Report ID: $reportId")

        lokasi = intent.getStringExtra("locationName").toString()
        Log.i("lokasi", "lokasi: $lokasi")

        status = intent.getStringExtra("status").toString()
        Log.i("status", "status: $status")

        val waktu_pengerjaan = intent.getStringExtra("waktu_pengerjaan").toString()
        Log.i("waktu_pengerjaan", "waktu_pengerjaan: $waktu_pengerjaan")


        catatanHandle = intent.getStringExtra("catatan") ?: "Tidak Ada Catatan Ditinggalkan Teknisi"
        Log.i("catatan", "catatan: $catatanHandle")

        //post user
        apiServices = NetworkConfig().getServices()
        sharedPrefManager = SharedPrafManager(this)
        // Memanggil fungsi untuk mendapatkan data pekerja kegiatan berdasarkan ID laporan
        getIdWorker(reportId)

        binding.panggilTotalKeseluruhan.visibility = View.GONE
        binding.txtTotalKeseluruhan.visibility = View.GONE


//        val idWorker = intent.getIntExtra("id_worker", -1)
//        Log.i("id_worker id", "id_worker id: $idWorker")
//
//        val activityId = intent.getIntExtra("activity_id", -1)
//        Log.i("activityId id", "activityId id: $activityId")
//
//        val deskripsiPending = intent.getStringExtra("deskripsiPending")
//        Log.i("deskripsiPending id", "deskripsiPending id: $deskripsiPending")


        //detail Report
        // Terima data dari intent
//        val reportId = intent.getIntExtra("id", -1)
        val location = intent.getStringExtra("locationName")
        val catatan = intent.getStringExtra("catatan")
        val dateString = intent.getStringExtra("date")
        val company = intent.getStringExtra("company")
        val pelapor = intent.getStringExtra("nama_user")
        val jenisHardware = intent.getStringExtra("jenisHardware")
        val uraianHardware = intent.getStringExtra("uraianHardware")
        val standartAplikasi = intent.getStringExtra("standartAplikasi")
        val uraianAplikasi = intent.getStringExtra("uraianAplikasi")


        val aplikasiItTol = intent.getStringExtra("aplikasiItTol")
        val uraianItTol = intent.getStringExtra("uraianItTol")
        val biaya = intent.getIntExtra("biaya", 0).toString()
        val shift = intent.getStringExtra("shift")
        val fotos = intent.getStringExtra("foto_awal")
        val status = intent.getStringExtra("status")
        val category_activity = intent.getStringExtra("kategori_activity")

        Log.i("status status", "status status: $status")


// Tambahkan logika untuk menonaktifkan tombol jika status adalah "done" atau "pending"
        if (status == "done") {
            binding.btnHandledReport.isEnabled = false
            binding.btnHandledReport.isClickable = false
            binding.btnHandledReport.alpha = 0.5f // Mengatur transparansi tombol menjadi 50%

            binding.panggilTotalKeseluruhan.visibility = View.VISIBLE
            binding.txtTotalKeseluruhan.visibility = View.VISIBLE

        } else if (status != "pending") {
            binding.rvWorker.visibility = View.GONE
            binding.garis2.visibility = View.GONE
        }


//        val imageUrl = "http://10.0.2.2:8000/images/1711326262.png"    // Ubah format tanggal
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        originalFormat.timeZone =
            TimeZone.getTimeZone("UTC") // Sesuaikan dengan zona waktu database

        val targetFormat = SimpleDateFormat("dd MMMM yyyy '||' HH:mm:ss", Locale.getDefault())
        targetFormat.timeZone = TimeZone.getDefault() // Gunakan zona waktu perangkat

        val date = originalFormat.parse(dateString)

        // Gunakan data untuk mengisi tampilan
//        binding.txtReportId.text = "Report ID: $reportId"
//        binding.panggilLokasiReport.text = "$location"
//        binding.panggilContentReport.text = "$catatan"
//        binding.panggilReportDate.text = targetFormat.format(date)
//        binding.panggilCompany.text = "$company"
//        binding.panggilJenisHardware.text = "$jenisHardware"
//        binding.panggilUraianHardware.text = "$uraianHardware"
//        binding.panggiJenisSoftware.text = "$standartAplikasi"
//        binding.panggilItTol.text = "$aplikasiItTol"
//        binding.panggilUraianItTol.text = "$uraianItTol"
//        binding.panggilBiaya.text = "$biaya"
//        binding.panggilShift.text = "$shift"
//        binding.panggilPelaporDetail.text = "$pelapor"

        binding.panggilLokasiReport.text = location ?: "-"
        binding.panggilContentReport.text = catatan ?: "-"
        binding.panggilReportDate.text = date?.let { targetFormat.format(it) } ?: "-"
        binding.panggilCompany.text = company ?: "-"
        binding.panggilJenisHardware.text = jenisHardware ?: "-"
        binding.panggilUraianHardware.text = uraianHardware ?: "-"
        binding.panggiJenisSoftware.text = standartAplikasi ?: "-"
        binding.panggilUraianSoftware.text = uraianAplikasi ?: "-"
        binding.panggilCatgory.text = category_activity ?: "-"


        binding.panggilItTol.text = aplikasiItTol ?: "-"
        binding.panggilUraianItTol.text = uraianItTol ?: "-"
        binding.panggilBiaya.text = biaya ?: "-"
        binding.panggilShift.text = shift ?: "-"
        binding.panggilPelaporDetail.text = pelapor ?: "-"


// Jika Anda memiliki path file gambar di dalam variabel 'fotos'
        Glide.with(this).load(fotos).into(binding.imgReports)


        ///Handle
        val tanggalReport = intent.getStringExtra("created_at")
        val tanggalSelesai = intent.getStringExtra("ended_at")
        val waktuHandle = intent.getStringExtra("waktu_pengerjaan")
        val kondisiAkhir = intent.getStringExtra("kondisi_akhir")
        val fotoAkhir = intent.getStringExtra("foto_akhir")

        val originalFormat3 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//        val targetFormat2 = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault())
        val originalFormat2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat2 = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault())


        //waktu handle
        // Mengonversi waktu ke format jam menit
        val waktuHandleText = waktuHandle?.let {
            val parts = it.split(":")
            if (parts.size == 3) {
                "${parts[0]} jam ${parts[1]} menit ${parts[2]} detik"
            } else {
                "Format waktu tidak valid"
            }
        } ?: "-"

// Waktu keseluruhan
        binding.panggilTotalKeseluruhan.text = waktuHandleText


        val date2: Date? = try {
            tanggalReport?.let { originalFormat3.parse(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }

        val date3: Date? = try {
            tanggalSelesai?.let { originalFormat2.parse(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }

//        val waktuHandleText = waktuHandle ?: "-"
        val kondisiAkhirText = kondisiAkhir ?: "-"

        Glide.with(this)
            .load(fotoAkhir)
            .placeholder(R.drawable.clear)
            .error(R.drawable.clear) // Menggunakan gambar yang sama untuk placeholder dan error
            .into(binding.fotoAkhirDetail)


//        binding.panggilTanggalReport.text = date2?.let { targetFormat2.format(it) } ?: "-"
//        binding.panggilTanggalSelesai.text = date3?.let { targetFormat2.format(it) } ?: "-"
//        binding.panggilWaktuHandle.text = waktuHandleText
        binding.panggilKondisiAKhir.text = kondisiAkhirText


        val btnHandle = binding.btnHandledReport
        btnHandle.setOnClickListener {
            val intent = Intent(this@HalamanDetailReport, HalamanHandle::class.java).apply {
                putExtra("id", reportId) // Memasukkan ID report ke intent
                putExtra("locationName", lokasi)
                putExtra("catatan", catatanHandle)
                putExtra("status", status)


            }
            startActivity(intent)
            finish()
        }

        val btnBack = binding.btnBackReport
        btnBack.setOnClickListener {
            startActivity(Intent(this@HalamanDetailReport, MainActivity::class.java))
            finish()
        }

        getRvPending(reportId)

        // Mengatur gambar dari drawable ke ImageView menggunakan binding
//        binding.fotoAkhirDetail.setImageResource(R.drawable.clear)
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
        originalFormat.timeZone =
            TimeZone.getTimeZone("UTC") // Sesuaikan dengan zona waktu database

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
        } else {
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


//    //22 nya
//    private fun bindDataHandle(dataItemIdWorker: DataItemIdWorker) {
//        if (dataItemIdWorker.status.equals("done", ignoreCase = true)) {
//            val username = findViewById<TextView>(R.id.panggilUsernameHandled)
//            username.text = dataItemIdWorker.username ?: "-"
//
//            // Tanggal Report Pending
//            val tanggalReportPendingTextView = findViewById<TextView>(R.id.panggilTanggalReport)
//            tanggalReportPendingTextView.text = dataItemIdWorker.startTime ?: "-"
//
//            // Tanggal Selesai Pending
//            val tanggalSelesaiPendingTextView = findViewById<TextView>(R.id.panggilTanggalSelesai)
//            tanggalSelesaiPendingTextView.text = dataItemIdWorker.endTime ?: "-"
//        } else if (dataItemIdWorker.status.equals("pending", ignoreCase = true)) {
//            val username = findViewById<TextView>(R.id.panggilUsernamPending)
//            username.text = dataItemIdWorker.username ?: "-"
//
//            // Tanggal Report Pending
//            val tanggalReportPendingTextView =
//                findViewById<TextView>(R.id.panggilTanggalReportPending)
//            tanggalReportPendingTextView.text = dataItemIdWorker.startTime ?: "-"
//
//            // Tanggal Selesai Pending
//            val tanggalSelesaiPendingTextView =
//                findViewById<TextView>(R.id.panggilTanggalSelesaiPending)
//            tanggalSelesaiPendingTextView.text = dataItemIdWorker.endTime ?: "-"
//
//            // Waktu Handle
//            val waktuHandlePendingTextView = findViewById<TextView>(R.id.panggilWaktuHandlePending)
//            waktuHandlePendingTextView.text = dataItemIdWorker.workDuration ?: "-"
//
//            // Deskripsi Pending
//            val deskripsiPendingTextView = findViewById<TextView>(R.id.panggilDeskripsiPending)
//            deskripsiPendingTextView.text = dataItemIdWorker.deskripsiPending ?: "-"
//        }
//    }

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

        binding.rvWorker.layoutManager = LinearLayoutManager(this@HalamanDetailReport)
        pendingAdapter = PendingAdapter(filteredList as List<DataItemIdWorker>)
        binding.rvWorker.adapter = pendingAdapter
    }


    private fun getTokenFromSharedPreferences(): String? {
        val sharedPrefManager = SharedPrafManager(this)
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }
}


//// Jika Anda memiliki URL gambar di dalam variabel 'fotos'
//        Glide.with(this).load(fotos).into(binding.imgReports)

