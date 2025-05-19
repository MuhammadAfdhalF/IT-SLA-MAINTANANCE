package com.acuy.sla_maintenance

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.ActivityUpdateProfileBinding
import com.acuy.sla_maintenance.model.PostUsers
import com.acuy.sla_maintenance.model.UpdateResponse
import com.bumptech.glide.Glide
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
import java.io.FileOutputStream
import java.io.IOException

class UpdateProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager

    //ttd n foto
    private lateinit var imageView: ImageView
    private lateinit var imageView2: ImageView


    private var imagePath: String? = null
    private var ttdPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ambil data cek token
        apiServices = NetworkConfig().getServices()
        sharedPrefManager = SharedPrafManager(this)

        // Panggil fungsi untuk mendapatkan token dari SharedPreferences
        val authToken = getTokenFromSharedPreferences()

        if (authToken != null) {
            // Panggil fungsi untuk mendapatkan dan menampilkan data pengguna
            getUsers(authToken)
        } else {
            Log.e("Token", "Token is null")
            // Handle null token here
        }

        // Menginisialisasi imageView untuk ttd
        imageView = binding.panggilTtd
        // Menetapkan onClickListener untuk ttd
        imageView.setOnClickListener {
            pickImageGallery(IMAGE_REQUEST_CODE)
        }

        // Menginisialisasi imageView untuk foto
        imageView2 = binding.fotoUserUpdate
        // Menetapkan onClickListener untuk foto
        imageView2.setOnClickListener {
            pickImageGallery(IMAGE_REQUEST_CODE_2)
        }




        binding.btnUpdateProfile.setOnClickListener {
            val username = binding.panggilUpdateUsername.text.toString().trim()
            var photoUri: Uri? = null // Inisialisasi dengan null
            var ttdUri: Uri? = null // Inisialisasi dengan null

            // Mendapatkan URI gambar dari ImageView fotoUserUpdate
            if (imageView2.drawable != null && imagePath != null) {
                val photoFile = File(imagePath)
                photoUri = Uri.fromFile(photoFile)
                Log.d("UpdateProfile", "Photo Path: $imagePath")
            }

            // Mendapatkan URI gambar dari ImageView panggilTtd
            if (imageView.drawable != null && ttdPath != null) {
                val ttdFile = File(ttdPath)
                ttdUri = Uri.fromFile(ttdFile)
            }

            // Periksa apakah username tidak kosong
            if (username.isNotEmpty()) {
                // Panggil fungsi saveProfil dengan parameter yang sesuai
                saveProfil(username, photoUri, ttdUri)

                val intent = Intent(this@UpdateProfile, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill in the username", Toast.LENGTH_SHORT).show()
            }

        }
    }

//    private fun saveProfil() {
//        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
//    }

    // Metode untuk memilih gambar dari galeri
    private fun pickImageGallery(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    companion object {
        val IMAGE_REQUEST_CODE = 100
        val IMAGE_REQUEST_CODE_2 = 101
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let { uri ->
                try {
                    val imageFile = File(cacheDir, "selected_image")
                    val inputStream = contentResolver.openInputStream(uri)
                    inputStream?.let { input ->
                        FileOutputStream(imageFile).use { output ->
                            val buffer = ByteArray(4 * 1024)
                            var read: Int
                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                            }
                            output.flush()
                        }
                        if (requestCode == IMAGE_REQUEST_CODE) {
                            ttdPath = getPathFromUri(uri, contentResolver)
                            imageView.setImageURI(uri)
                            Log.d("ImageChooser", "TTD Path: $imagePath") // Log jalur gambar
                        } else if (requestCode == IMAGE_REQUEST_CODE_2) {
                            imagePath = getPathFromUri(uri, contentResolver) // Menyimpan path tanda tangan
                            imageView2.setImageURI(uri)
                            Log.d("ImageChooser", "Image Path: $ttdPath") // Log jalur tanda tangan
                        } else {

                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
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


    private fun getUsers(token: String) {
        val formattedToken = "Bearer $token"
        apiServices.getUsers(formattedToken).enqueue(object : Callback<PostUsers> {
            override fun onResponse(call: Call<PostUsers>, response: Response<PostUsers>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let { displayUser(it) }
                } else {
                    Log.e("Retrofit onResponse", "Response not successful: ${response.code()}")
                    // Handle error response here
                }
            }

            override fun onFailure(call: Call<PostUsers>, t: Throwable) {
                Log.e("Retrofit onFailure", "onFailure: ${t.message}")
                // Handle failure here
            }
        })
    }

    private fun displayUser(user: PostUsers) {
        // Di sini Anda dapat menampilkan detail pengguna sesuai kebutuhan aplikasi Anda
        binding.panggilUpdateUsername.text =
            Editable.Factory.getInstance().newEditable(user.username)

        val images = Images()
        val imageUrlAwal = "${images.BASE_URL}${user.foto}"
        val imageUrlAkhir = "${images.BASE_URL}${user.ttd}"

        if (!user.ttd.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrlAkhir) // URL gambar yang diberikan oleh respons API
                .placeholder(R.drawable.ic_launcher_foreground) // Gambar placeholder yang akan ditampilkan saat proses unduhan gambar berlangsung
                .error(R.drawable.clear) // Gambar yang akan ditampilkan jika terjadi kesalahan saat memuat gambar
                .into(binding.panggilTtd) // Tampilkan gambar di CircleImageView
        } else {
            // Tampilkan gambar placeholder atau gambar default jika URL gambar tidak ada
            binding.panggilTtd.setImageResource(R.drawable.ic_launcher_foreground)
        }

        if (!user.foto.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrlAwal) // URL gambar yang diberikan oleh respons API
                .placeholder(R.drawable.ic_launcher_foreground) // Gambar placeholder yang akan ditampilkan saat proses unduhan gambar berlangsung
                .error(R.drawable.clear) // Gambar yang akan ditampilkan jika terjadi kesalahan saat memuat gambar
                .into(binding.fotoUserUpdate) // Tampilkan gambar di CircleImageView
        } else {
            // Tampilkan gambar placeholder atau gambar default jika URL gambar tidak ada
            binding.fotoUserUpdate.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }


    private fun getTokenFromSharedPreferences(): String? {
        // Mengambil token dari SharedPreferences
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }

    private fun saveProfil(username: String, photoUri: Uri?, ttdUri: Uri?) {
        // Ambil token dari SharedPreferences
        val authToken = getTokenFromSharedPreferences()

        // Cek apakah token tersedia
        if (authToken != null) {
            // Mendapatkan ID pengguna dari SharedPreferences
            val userId = getUserId()

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
            val apiServicesWithToken = retrofit.create(ApiServices::class.java)

            // Membuat RequestBody untuk parameter username
            val usernameRequestBody = username.toRequestBody("text/plain".toMediaTypeOrNull())

            // Membuat RequestBody untuk file foto jika tersedia
            val photoRequestBody = photoUri?.let { uri ->
                val imagePath = uri.path // Mengambil jalur file gambar dari URI gambar
                imagePath?.let { path ->
                    val file = File(path)
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                }
            }

            // Membuat RequestBody untuk file ttd jika tersedia
            val ttdRequestBody = ttdUri?.let { uri ->
                val ttdPath = uri.path // Mengambil jalur file gambar dari URI gambar
                ttdPath?.let { path ->
                    val file = File(path)
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                }
            }

            // Membuat instance MultipartBody.Part untuk file foto
            val photoPart = photoRequestBody?.let {
                MultipartBody.Part.createFormData("foto", "photo.jpg", it)
            }

            // Membuat instance MultipartBody.Part untuk file ttd
            val ttdPart = ttdRequestBody?.let {
                MultipartBody.Part.createFormData("ttd", "ttd.jpg", it)
            }

            // Mengirim data ke server dengan Retrofit
            apiServicesWithToken.updateProfile(userId, usernameRequestBody, ttdPart, photoPart)
                .enqueue(object : Callback<UpdateResponse> {
                    override fun onResponse(
                        call: Call<UpdateResponse>,
                        response: Response<UpdateResponse>
                    ) {
                        if (response.isSuccessful) {
                            val updateResponse = response.body()
                            updateResponse?.let {
                                // Tanggapi respon berhasil di sini
                                Toast.makeText(this@UpdateProfile, it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            // Tanggapi respon gagal di sini
                            Toast.makeText(
                                this@UpdateProfile,
                                "Failed to update profile",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("Update Profile", "Failed to update profile: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                        // Tanggapi kegagalan jaringan atau permintaan di sini
                        Toast.makeText(this@UpdateProfile, "Network error", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("Update Profile", "Network error: ${t.message}", t)
                    }
                })
        } else {
            Log.e("Token", "Token is null")
            // Tanggapi ketika token tidak tersedia di sini
        }
    }




    private fun getUserId(): Int {
        val sharedPrefManager = SharedPrafManager(this)
        return sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
    }
}

//package com.acuy.sla_maintenance
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.text.Editable
//import android.util.Log
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.acuy.sla_maintenance.config.ApiServices
//import com.acuy.sla_maintenance.config.Constant
//import com.acuy.sla_maintenance.config.NetworkConfig
//import com.acuy.sla_maintenance.config.SharedPrafManager
//import com.acuy.sla_maintenance.databinding.ActivityUpdateProfileBinding
//import com.acuy.sla_maintenance.model.PostUsers
//import com.acuy.sla_maintenance.model.UpdateResponse
//import com.bumptech.glide.Glide
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//
//class UpdateProfile : AppCompatActivity() {
//
//    private lateinit var binding: ActivityUpdateProfileBinding
//    private lateinit var apiServices: ApiServices
//    private lateinit var sharedPrefManager: SharedPrafManager
//
//    //ttd n foto
//    private lateinit var imageView: ImageView
//    private lateinit var imageView2: ImageView
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // ambil data cek token
//        apiServices = NetworkConfig().getServices()
//        sharedPrefManager = SharedPrafManager(this)
//
//        // Panggil fungsi untuk mendapatkan token dari SharedPreferences
//        val authToken = getTokenFromSharedPreferences()
//
//        if (authToken != null) {
//            // Panggil fungsi untuk mendapatkan dan menampilkan data pengguna
//            getUsers(authToken)
//        } else {
//            Log.e("Token", "Token is null")
//            // Handle null token here
//        }
//
//
//        // Menginisialisasi imageView untuk ttd
//        imageView = binding.panggilTtd
//        // Menetapkan onClickListener untuk ttd
//        imageView.setOnClickListener {
//            pickImageGallery(IMAGE_REQUEST_CODE)
//        }
//
//        // Menginisialisasi imageView untuk foto
//        imageView2 = binding.fotoUserUpdate
//        // Menetapkan onClickListener untuk foto
//        imageView2.setOnClickListener {
//            pickImageGallery(IMAGE_REQUEST_CODE_2)
//        }
//
//        binding.btnUpdateProfile.setOnClickListener {
//            saveProfil()
//        }
//    }
//
//    private fun saveProfil() {
//        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
//    }
//
//    // Metode untuk memilih gambar dari galeri
//    private fun pickImageGallery(requestCode: Int) {
//        Intent(Intent.ACTION_PICK).also {
//            it.type = "image/*"
//            val mimeTypes = arrayOf("image/jpeg", "image/png")
//            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//            startActivityForResult(it, IMAGE_REQUEST_CODE)
//        }
//    }
//
//    companion object {
//        val IMAGE_REQUEST_CODE = 100
//        val IMAGE_REQUEST_CODE_2 = 101
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) { // Pastikan untuk memeriksa requestCode, bukan resultCode
//            val selectedImageUri: Uri? = data?.data
//            selectedImageUri?.let { uri ->
//                try {
//                    // Membuat file dari URI yang dipilih
//                    val imageFile = File(cacheDir, "selected_image")
//                    val inputStream = contentResolver.openInputStream(uri)
//                    inputStream?.let { input ->
//                        // Menyalin data gambar ke file yang dibuat
//                        FileOutputStream(imageFile).use { output ->
//                            val buffer = ByteArray(4 * 1024) // 4k buffer
//                            var read: Int
//                            while (input.read(buffer).also { read = it } != -1) {
//                                output.write(buffer, 0, read)
//                            }
//                            output.flush()
//                        }
//                        // Menyimpan path dari file gambar yang dibuat
//                        val imagePath = imageFile.absolutePath
//                        // Menampilkan gambar pada ImageView sesuai dengan requestCode
//                        if (requestCode == IMAGE_REQUEST_CODE) {
//                            imageView.setImageURI(uri)
//                        } else if (requestCode == IMAGE_REQUEST_CODE_2) {
//                            imageView2.setImageURI(uri)
//                        }
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private fun getUsers(token: String) {
//        val formattedToken = "Bearer $token"
//        apiServices.getUsers(formattedToken).enqueue(object : Callback<PostUsers> {
//            override fun onResponse(call: Call<PostUsers>, response: Response<PostUsers>) {
//                if (response.isSuccessful) {
//                    val user = response.body()
//                    user?.let { displayUser(it) }
//                } else {
//                    Log.e("Retrofit onResponse", "Response not successful: ${response.code()}")
//                    // Handle error response here
//                }
//            }
//
//            override fun onFailure(call: Call<PostUsers>, t: Throwable) {
//                Log.e("Retrofit onFailure", "onFailure: ${t.message}")
//                // Handle failure here
//            }
//        })
//    }
//
//    private fun displayUser(user: PostUsers) {
//        // Di sini Anda dapat menampilkan detail pengguna sesuai kebutuhan aplikasi Anda
//        binding.panggilUpdateUsername.text =
//            Editable.Factory.getInstance().newEditable(user.username)
//        Glide.with(this)
//            .load("http://10.0.2.2:8000/images/${user.foto}") // URL gambar yang diberikan oleh respons API
//            .placeholder(R.drawable.ic_launcher_foreground) // Gambar placeholder yang akan ditampilkan saat proses unduhan gambar berlangsung
//            .error(R.drawable.clear) // Gambar yang akan ditampilkan jika terjadi kesalahan saat memuat gambar
//            .into(binding.fotoUserUpdate) // Tampilkan gambar di CircleImageView
//
//    }
//
//    private fun getTokenFromSharedPreferences(): String? {
//        // Mengambil token dari SharedPreferences
//        return sharedPrefManager.getString(Constant.USER_TOKEN)
//    }
//
//
//    private fun sendDataToServer(username: String, photoUri: Uri?, ttdUri: Uri?) {
//        // Ambil token dari SharedPreferences
//        val authToken = getTokenFromSharedPreferences()
//
//        // Cek apakah token tersedia
//        if (authToken != null) {
//            // Mendapatkan ID pengguna dari SharedPreferences
//            val userId = getUserId()
//
//            // Membuat instance Retrofit
//            val retrofit = NetworkConfig().getServices()
//
//            // Membuat RequestBody untuk parameter username
//            val usernameRequestBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
//
//            // Membuat RequestBody untuk file foto jika tersedia
//            val photoRequestBody = photoUri?.let { uri ->
//                val file = File(uri.path)
//                file.asRequestBody("image/*".toMediaTypeOrNull())
//            }
//
//            // Membuat RequestBody untuk file ttd jika tersedia
//            val ttdRequestBody = ttdUri?.let { uri ->
//                val file = File(uri.path)
//                file.asRequestBody("image/*".toMediaTypeOrNull())
//            }
//
//            // Membuat instance MultipartBody.Part untuk file foto
//            val photoPart = photoRequestBody?.let {
//                MultipartBody.Part.createFormData("foto", "photo.jpg", it)
//            }
//
//            // Membuat instance MultipartBody.Part untuk file ttd
//            val ttdPart = ttdRequestBody?.let {
//                MultipartBody.Part.createFormData("ttd", "ttd.jpg", it)
//            }
//
//            // Mengirim data ke server dengan Retrofit
//            retrofit.updateProfile(userId, usernameRequestBody, ttdPart, photoPart)
//                .enqueue(object : Callback<UpdateResponse> {
//                    override fun onResponse(
//                        call: Call<UpdateResponse>,
//                        response: Response<UpdateResponse>
//                    ) {
//                        if (response.isSuccessful) {
//                            val updateResponse = response.body()
//                            updateResponse?.let {
//                                // Tanggapi respon berhasil di sini
//                                Toast.makeText(this@UpdateProfile, it.message, Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                        } else {
//                            // Tanggapi respon gagal di sini
//                            Toast.makeText(
//                                this@UpdateProfile,
//                                "Failed to update profile",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            Log.e("Update Profile", "Failed to update profile: ${response.code()}")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
//                        // Tanggapi kegagalan jaringan atau permintaan di sini
//                        Toast.makeText(this@UpdateProfile, "Network error", Toast.LENGTH_SHORT)
//                            .show()
//                        Log.e("Update Profile", "Network error: ${t.message}", t)
//                    }
//                })
//        } else {
//            Log.e("Token", "Token is null")
//            // Tanggapi ketika token tidak tersedia di sini
//        }
//    }
//
//    private fun getUserId(): Int {
//        val sharedPrefManager = SharedPrafManager(this)
//        return sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
//    }
//
//
//}