package com.acuy.sla_maintenance

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.ActivityMaintananceProccesBinding
import com.acuy.sla_maintenance.model.MaintananceResponse
import com.acuy.sla_maintenance.model.PendingResponse
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class HalamanMaintananceProcces : AppCompatActivity() {
    private lateinit var binding: ActivityMaintananceProccesBinding

    private val IMAGE_REQUEST_CODE = 100
    private var imagePath: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var imageView: ImageView
    private var reportId: Int = 0
    private var deskripsiPending : String? = null


    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaintananceProccesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi sharedPrefManager di sini
        sharedPrefManager = SharedPrafManager(this)
        // Panggil fungsi untuk mendapatkan token dari SharedPreferences
        val authToken = getTokenFromSharedPreferences()
        Log.i("Token", "TOken: $authToken")


         reportId = intent.getIntExtra("id", -1)
        Log.i("ReportID di Maintanance", "Report ID: $reportId")


        val btnSelesaiMaintanance = binding.btnSelesaiMaintanance
        // selesai
        btnSelesaiMaintanance.visibility = View.GONE
        binding.img2Prosses.visibility = View.GONE
        binding.txtMulaiTimer.visibility = View.GONE
        binding.teksTimer.visibility = View.GONE

        val btnPendingMaintanance = binding.btnPendingMaintanance

        // pending
        binding.btnPendingMaintanance.visibility = View.GONE
        binding.img4Prosses.visibility = View.GONE
        binding.txtPending.visibility = View.GONE
        binding.teksPending.visibility = View.GONE
        binding.cardPending.visibility = View.GONE
        binding.txtKonfirmasiPending.visibility = View.GONE
        binding.btnSubmitPending.visibility = View.GONE
        binding.garis1.visibility = View.GONE
        binding.garis2.visibility = View.GONE






        val btnSampai = binding.btnSampai
        btnSampai.setOnClickListener {
            // Ganti warna tombol btnSampai menjadi abu saat ditekan
            btnSampai.setCardBackgroundColor(getColor(R.color.abu))
            btnSelesaiMaintanance.setCardBackgroundColor(getColor(R.color.blue))
            btnPendingMaintanance.setCardBackgroundColor(getColor(R.color.red))

            // Tampilkan tombol btnSelesaiMaintanance
            btnSelesaiMaintanance.visibility = View.VISIBLE
            btnPendingMaintanance.visibility = View.VISIBLE

            binding.img2Prosses.visibility = View.VISIBLE
            binding.txtMulaiTimer.visibility = View.VISIBLE
            binding.teksTimer.visibility = View.VISIBLE

            //GAK BISA ditekan
            binding.btnSampai.isEnabled = false
            binding.btnSampai.isClickable = false

        }



        //done
        binding.img3Prosses.visibility = View.GONE
        binding.txtSelesai.visibility = View.GONE
        binding.teksSelesai.visibility = View.GONE
        binding.cardAddImage.visibility = View.GONE
        binding.txtLaporanMaintanance.visibility = View.GONE
        binding.cardContent.visibility = View.GONE
        binding.txtKonfirmasi.visibility = View.GONE
        binding.btnSubmitMaintanance.visibility = View.GONE
        binding.txtBiaya.visibility = View.GONE
        binding.inpBiaya.visibility = View.GONE





        binding.btnSelesaiMaintanance.setOnClickListener {
            binding.btnSelesaiMaintanance.setCardBackgroundColor(getColor(R.color.abu))
            binding.img3Prosses.visibility = View.VISIBLE
            binding.txtSelesai.visibility = View.VISIBLE
            binding.teksSelesai.visibility = View.VISIBLE
            binding.cardAddImage.visibility = View.VISIBLE
            binding.txtLaporanMaintanance.visibility = View.VISIBLE
            binding.cardContent.visibility = View.VISIBLE
            binding.txtKonfirmasi.visibility = View.VISIBLE
            binding.btnSubmitMaintanance.visibility = View.VISIBLE
            binding.txtBiaya.visibility = View.VISIBLE
            binding.inpBiaya.visibility = View.VISIBLE

            btnPendingMaintanance.setCardBackgroundColor(getColor(R.color.red))
            binding.img4Prosses.visibility = View.GONE
            binding.txtPending.visibility = View.GONE
            binding.teksPending.visibility = View.GONE
            binding.cardPending.visibility = View.GONE
            binding.txtKonfirmasiPending.visibility = View.GONE
            binding.btnSubmitPending.visibility = View.GONE

            binding.garis2.visibility = View.VISIBLE


        }

        binding.btnPendingMaintanance.setOnClickListener {
            binding.btnPendingMaintanance.setCardBackgroundColor(getColor(R.color.abu))
            binding.img4Prosses.visibility = View.VISIBLE
            binding.txtPending.visibility = View.VISIBLE
            binding.teksPending.visibility = View.VISIBLE
            binding.cardPending.visibility = View.VISIBLE
            binding.txtKonfirmasiPending.visibility = View.VISIBLE
            binding.btnSubmitPending.visibility = View.VISIBLE

            binding.img3Prosses.visibility = View.GONE
            binding.txtSelesai.visibility = View.GONE
            binding.teksSelesai.visibility = View.GONE
            binding.cardAddImage.visibility = View.GONE
            binding.txtLaporanMaintanance.visibility = View.GONE
            binding.cardContent.visibility = View.GONE
            binding.txtKonfirmasi.visibility = View.GONE
            binding.btnSubmitMaintanance.visibility = View.GONE
            binding.txtBiaya.visibility = View.GONE
            binding.inpBiaya.visibility = View.GONE
            btnSelesaiMaintanance.setCardBackgroundColor(getColor(R.color.blue))

            binding.garis1.visibility = View.VISIBLE

        }



            val btnSubmitMaintanance = binding.btnSubmitMaintanance
        btnSubmitMaintanance.setOnClickListener {
            // Ambil kondisi akhir dari komponen UI
            val kondisiAkhir = binding.panggilKondisiAKhir.text.toString().trim()
            var photoUri: Uri? = null // Inisialisasi dengan null
            val biaya = binding.panggilBiaya.text.toString()


            // Log informasi kondisi akhir dan foto sebelum mengirim data ke server
            Log.i("Maintanance Request", "Kondisi Akhir: $kondisiAkhir")
            Log.i("Maintanance Request", "Foto Akhir URI: $imagePath")

            // Periksa apakah kondisi akhir tidak kosong
            if (kondisiAkhir.isNotEmpty()) {
                // Panggil fungsi sendMaintananceData dengan parameter yang sesuai
                val photoFile = File(imagePath)
                photoUri = Uri.fromFile(photoFile)
                sendMaintananceData(reportId, photoUri, kondisiAkhir, biaya)

                startActivity(Intent(this@HalamanMaintananceProcces, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please fill in the maintenance condition", Toast.LENGTH_SHORT).show()
            }


        }



        //        foto
        imageView = binding.panggilFotoAkhir // Menginisialisasi imageView
        val inputPhoto = binding.cardAddImage
        inputPhoto.setOnClickListener {
            // Mulai proses pengunggahan
            openImageChooser()
        }


        binding.btnSubmitPending.setOnClickListener {
            deskripsiPending = binding.panggilDeskripsiPendingMaintanance.text.toString()


            if (deskripsiPending!!.isNotEmpty()) {
                // Lakukan tindakan jika deskripsiPending sudah terisi
                // Misalnya, lanjutkan ke tindakan berikutnya
                startActivity(Intent(this@HalamanMaintananceProcces, MainActivity::class.java))
                postPending(reportId, deskripsiPending!!)
            } else {
                // Tampilkan pesan kesalahan jika deskripsiPending kosong
                Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun postPending(id: Int, deskripsiPending: String) {
        // Ambil token dari SharedPreferences
        val authToken = getTokenFromSharedPreferences()

        // Cek apakah token tersedia
        if (authToken != null) {
            // Membuat interceptor untuk menyertakan token dalam header permintaan
            val tokenInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
                chain.proceed(request)
            }

            // Tambahkan interceptor ke OkHttpClient
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()

            // Membuat instance Retrofit dengan OkHttpClient yang telah dikonfigurasi
            val retrofit = Retrofit.Builder()
                .baseUrl(NetworkConfig().BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            // Buat instance layanan API dengan retrofit yang telah dikonfigurasi
            val apiService = retrofit.create(ApiServices::class.java)

            // Membuat request body untuk deskripsi pending
            val deskripsiPendingRequestBody = deskripsiPending.toRequestBody("text/plain".toMediaTypeOrNull())

            // Mengirim permintaan POST pending ke server
            apiService.postPending(id, deskripsiPendingRequestBody)
                .enqueue(object : Callback<PendingResponse> {
                    override fun onResponse(
                        call: Call<PendingResponse>,
                        response: Response<PendingResponse>
                    ) {
                        if (response.isSuccessful) {
                            // Tanggapi ketika permintaan berhasil
                            val pendingResponse = response.body()
                            pendingResponse?.let {
                                val message = it.message
                                val data = it.data

                                // Tampilkan pesan dan data dalam Toast atau di mana pun yang Anda inginkan
                                val toastMessage = "Message: $message"
                                Toast.makeText(this@HalamanMaintananceProcces, toastMessage, Toast.LENGTH_SHORT).show()

                                // Contoh penanganan data, misalnya menampilkan ID aktivitas dan deskripsi pending
                                Log.d("Pending Response", "Activity ID: ${data?.activityId}, Deskripsi Pending: ${data?.deskripsiPending}")
                            }
                        }
                        else {
                            // Tanggapi respon gagal di sini
                            Toast.makeText(
                                this@HalamanMaintananceProcces,
                                "Failed to send pending activity",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("Pending Activity", "Failed to send pending activity: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<PendingResponse>, t: Throwable) {
                        // Tanggapi kegagalan jaringan atau permintaan di sini
                        Toast.makeText(this@HalamanMaintananceProcces, "Network error", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("Pending Activity", "Network error: ${t.message}", t)
                    }
                })
        } else {
            // Tanggapi ketika token tidak tersedia
            Log.e("Token", "Token is null")
        }
    }


    // Fungsi untuk membuka pemilih gambar
    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                HalamanAddLaporan.IMAGE_REQUEST_CODE -> {
                    // Mengatur URI gambar yang dipilih ke ImageView
                    selectedImageUri = data?.data
                    imageView.setImageURI(selectedImageUri)

                    // Mendapatkan jalur file dari URI gambar
                    selectedImageUri?.let { uri ->
                        val contentResolver = contentResolver
                        imagePath = getPathFromUri(uri, contentResolver)
                        Log.d("ImageChooser", "Image Path: $imagePath")
                    }
                }
            }
        }
    }

    // Fungsi untuk mendapatkan jalur file dari URI gambar
    private fun getPathFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return null
    }

    private fun getTokenFromSharedPreferences(): String? {
        // Mengambil token dari SharedPreferences
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }


//    // Fungsi untuk mengirim data pemeliharaan ke server
//    private fun sendMaintananceData(id: Int, fotoAkhirUri: Uri?, kondisiAkhir: String) {
//        // Ambil token dari SharedPreferences
//        val authToken = getTokenFromSharedPreferences()
//
//        // Cek apakah token tersedia
//        if (authToken != null) {
//            // Membuat interceptor untuk menyertakan token dalam header permintaan
//            val tokenInterceptor = Interceptor { chain ->
//                val request = chain.request().newBuilder()
//                    .addHeader("Authorization", "Bearer $authToken")
//                    .build()
//                chain.proceed(request)
//            }
//
//            // Tambahkan interceptor ke OkHttpClient
//            val okHttpClient = OkHttpClient.Builder()
//                .addInterceptor(tokenInterceptor)
//                .build()
//
//            // Membuat instance Retrofit dengan OkHttpClient yang telah dikonfigurasi
//            val retrofit = Retrofit.Builder()
//                .baseUrl(NetworkConfig().BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
//                .build()
//
//            // Buat instance layanan API dengan retrofit yang telah dikonfigurasi
//            val apiService = retrofit.create(ApiServices::class.java)
//
//            // Membuat bagian dari body untuk foto akhir jika tersedia
//            val fotoAkhirRequestBody = fotoAkhirUri?.let { uri ->
//                val imagePath = uri.path // Mengambil jalur file gambar dari URI gambar
//                imagePath?.let { path ->
//                    val file = File(path)
//                    file.asRequestBody("image/*".toMediaTypeOrNull())
//                }
//            }
//
//// Membuat instance MultipartBody.Part untuk file foto
//            val photoPart = fotoAkhirRequestBody?.let {
//                MultipartBody.Part.createFormData("foto_akhir", "photo.jpg", it)
//            }
//
//
//            // Membuat bagian dari body untuk kondisi akhir
//            val kondisiAkhirRequestBody =
//                kondisiAkhir.toRequestBody("text/plain".toMediaTypeOrNull())
//
//
//            // Log informasi kondisi akhir dan foto sebelum mengirim data ke server
//            Log.i("Maintanance Request", "Kondisi Akhir: $kondisiAkhir")
//            Log.i("Maintanance Request", "Foto Akhir URI: $fotoAkhirUri")
//
//            // Mengirim data ke server dengan Retrofit
//            apiService.maintanance(id, photoPart, kondisiAkhirRequestBody)
//                .enqueue(object : Callback<MaintananceResponse> {
//                    override fun onResponse(
//                        call: Call<MaintananceResponse>,
//                        response: Response<MaintananceResponse>
//                    ) {
//                        if (response.isSuccessful) {
//                            // Tanggapi ketika permintaan berhasil
//                            val maintananceResponse = response.body()
//                            maintananceResponse?.let {
//                                Toast.makeText(this@HalamanMaintananceProcces, it.message, Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                        } else {
//                            // Tanggapi respon gagal di sini
//                            Toast.makeText(
//                                this@HalamanMaintananceProcces,
//                                "Failed to update profile",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            Log.e("Update Profile", "Failed to update profile: ${response.code()}")
//
//                        }
//                    }
//
//                    override fun onFailure(call: Call<MaintananceResponse>, t: Throwable) {
//                        // Tanggapi kegagalan jaringan atau permintaan di sini
//                        Toast.makeText(this@HalamanMaintananceProcces, "Network error", Toast.LENGTH_SHORT)
//                            .show()
//                        Log.e("Update Profile", "Network error: ${t.message}", t)
//                    }
//                })
//        } else {
//            // Tanggapi ketika token tidak tersedia
//            Log.e("Token", "Token is null")
//        }
//    }

    // Fungsi untuk mengirim data pemeliharaan ke server
    private fun sendMaintananceData(id: Int, fotoAkhirUri: Uri?, kondisiAkhir: String, biaya: String) {
        // Ambil token dari SharedPreferences
        val authToken = getTokenFromSharedPreferences()

        // Cek apakah token tersedia
        if (authToken != null) {
            // Membuat interceptor untuk menyertakan token dalam header permintaan
            val tokenInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
                chain.proceed(request)
            }

            // Tambahkan interceptor ke OkHttpClient
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()

            // Membuat instance Retrofit dengan OkHttpClient yang telah dikonfigurasi
            val retrofit = Retrofit.Builder()
                .baseUrl(NetworkConfig().BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            // Buat instance layanan API dengan retrofit yang telah dikonfigurasi
            val apiService = retrofit.create(ApiServices::class.java)

            // Membuat bagian dari body untuk foto akhir jika tersedia
            val fotoAkhirRequestBody = fotoAkhirUri?.let { uri ->
                val imagePath = uri.path // Mengambil jalur file gambar dari URI gambar
                imagePath?.let { path ->
                    val file = File(path)
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                }
            }

            // Membuat instance MultipartBody.Part untuk file foto
            val photoPart = fotoAkhirRequestBody?.let {
                MultipartBody.Part.createFormData("foto_akhir", "photo.jpg", it)
            }

            // Membuat bagian dari body untuk kondisi akhir
            val kondisiAkhirRequestBody =
                kondisiAkhir.toRequestBody("text/plain".toMediaTypeOrNull())

            // Membuat bagian dari body untuk biaya
            val biayaRequestBody =
                biaya.toRequestBody("text/plain".toMediaTypeOrNull())

            // Log informasi kondisi akhir, foto, dan biaya sebelum mengirim data ke server
            Log.i("Maintanance Request", "Kondisi Akhir: $kondisiAkhir")
            Log.i("Maintanance Request", "Foto Akhir URI: $fotoAkhirUri")
            Log.i("Maintanance Request", "Biaya: $biaya")

            // Mengirim data ke server dengan Retrofit
            apiService.postDone(id, photoPart, kondisiAkhirRequestBody, biayaRequestBody)
                .enqueue(object : Callback<MaintananceResponse> {
                    override fun onResponse(
                        call: Call<MaintananceResponse>,
                        response: Response<MaintananceResponse>
                    ) {
                        if (response.isSuccessful) {
                            // Tanggapi ketika permintaan berhasil
                            val maintananceResponse = response.body()
                            maintananceResponse?.let {
                                Toast.makeText(this@HalamanMaintananceProcces, it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            // Tanggapi respon gagal di sini
                            Toast.makeText(
                                this@HalamanMaintananceProcces,
                                "Failed to update profile",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("Update Profile", "Failed to update profile: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<MaintananceResponse>, t: Throwable) {
                        // Tanggapi kegagalan jaringan atau permintaan di sini
                        Toast.makeText(this@HalamanMaintananceProcces, "Network error", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("Update Profile", "Network error: ${t.message}", t)
                    }
                })
        } else {
            // Tanggapi ketika token tidak tersedia
            Log.e("Token", "Token is null")
        }
    }

}
