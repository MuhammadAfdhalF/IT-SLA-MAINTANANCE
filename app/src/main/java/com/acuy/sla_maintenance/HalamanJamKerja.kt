package com.acuy.sla_maintenance


import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.acuy.sla_maintenance.adapter.JamKerjaAdapter
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.ActivityJamKerjaBinding
import com.acuy.sla_maintenance.model.DataItemJamKerja
import com.acuy.sla_maintenance.model.JamKerjaResponse
import com.acuy.sla_maintenance.model.PostUsers
import com.bumptech.glide.Glide
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HalamanJamKerja : AppCompatActivity() {
    private lateinit var binding: ActivityJamKerjaBinding
    private lateinit var halamanProfile: HalamanProfile
    private lateinit var halamanHome: HalamanHome
    private lateinit var fragmentManager: FragmentManager
    private lateinit var jamKerjaAdapter: JamKerjaAdapter

    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager

//    private lateinit var jamKerjaAdapter: MultiDataAdapter // Pastikan menggunakan MultiDataAdapter di sini


    private val bulanMap = mapOf(
        "Januari" to 1,
        "Februari" to 2,
        "Maret" to 3,
        "April" to 4,
        "Mei" to 5,
        "Juni" to 6,
        "Juli" to 7,
        "Agustus" to 8,
        "September" to 9,
        "Oktober" to 10,
        "November" to 11,
        "Desember" to 12
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJamKerjaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        halamanProfile = HalamanProfile()
        halamanHome = HalamanHome()

        fragmentManager = supportFragmentManager

        //post user
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

        // filter  bulan
        val itemBulan = bulanMap.keys.toList()

        // Adapter untuk AutoCompleteTextView bulan
        val adapterBulan = ArrayAdapter(this, R.layout.list_company, itemBulan)
        binding.panggilFilterBulan.setAdapter(adapterBulan)

// Atur tindakan yang diambil saat item bulan dipilih
        binding.panggilFilterBulan.setOnItemClickListener { parent, view, position, id ->
            val selectedMonth = parent.getItemAtPosition(position).toString()
            val selectedMonthValue = bulanMap[selectedMonth] ?: -1

            // Ambil nilai tahun dari AutoCompleteTextView
            val selectedYear = binding.panggilTahunFIlter.text.toString()
            val selectedYearValue = selectedYear.toIntOrNull() ?: -1 // Konversi ke Int, jika gagal, beri nilai default -1

            println("Bulan yang dipilih: $selectedMonthValue")
            println("Tahun yang dipilih: $selectedYearValue")

            val sharedPrefManager = SharedPrafManager(this)
            val userId = sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
            // Panggil fungsi getFilterJamKerja untuk mengambil data jam kerja berdasarkan bulan dan tahun yang dipilih
            getFilterJamKerja(userId, selectedMonthValue, selectedYearValue)
        }



        // Filter tahun
        // Mendapatkan tahun saat ini
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // Daftar tahun dari tahun sebelumnya hingga 10 tahun ke depan
        val years = mutableListOf<String>()
        for (i in currentYear - 1..currentYear + 10) {
            years.add(i.toString())
        }

        // Adapter untuk AutoCompleteTextView tahun
        val adapterTahun = ArrayAdapter(this, R.layout.list_company, years)
        binding.panggilTahunFIlter.setAdapter(adapterTahun)

        // Atur tindakan yang diambil saat item tahun dipilih
        binding.panggilTahunFIlter.setOnItemClickListener { parent, view, position, id ->
            val selectedYear = parent.getItemAtPosition(position).toString()
            println("Tahun yang dipilih: $selectedYear")
            val selectedYearValue = selectedYear.toIntOrNull() ?: -1
            val selectedMonth = binding.panggilFilterBulan.text.toString()
            val selectedMonthValue = bulanMap[selectedMonth] ?: -1

            println("Bulan yang dipilih: $selectedMonthValue")
            println("Tahun yang dipilih: $selectedYearValue")

            val sharedPrefManager = SharedPrafManager(this)
            val userId = sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
            // Panggil fungsi getFilterJamKerja untuk mengambil data jam kerja berdasarkan tahun yang dipilih
            getFilterJamKerja(userId, selectedMonthValue, selectedYearValue)
        }


        //clear filter
        binding.clearFilter.setOnClickListener {
            binding.panggilFilterBulan.setText("")
            binding.panggilTahunFIlter.setText("")

            val sharedPrefManager = SharedPrafManager(this)
            val userId = sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
            getJamKerja(userId)

        }



        val btnBack = binding.btnBack
        btnBack.setOnClickListener {
            val intent = Intent(this@HalamanJamKerja, MainActivity::class.java)
            startActivity(intent)

        }

        val sharedPrefManager = SharedPrafManager(this)
        val userId = sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
        getJamKerja(userId)

    }

    // function post user
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
        // Misalnya, menampilkan informasi pengguna dalam TextView atau komponen UI lainnya
        binding.namaUser.text = user.username
        // Tambahkan kode untuk menampilkan informasi pengguna lainnya sesuai kebutuhan

        val images = Images()
        val imageUrlAwal = "${images.BASE_URL}${user.foto}"
        Glide.with(this)
            .load(imageUrlAwal) // URL gambar yang diberikan oleh respons API
            .placeholder(R.drawable.ic_launcher_foreground) // Gambar placeholder yang akan ditampilkan saat proses unduhan gambar berlangsung
            .error(R.drawable.clear) // Gambar yang akan ditampilkan jika terjadi kesalahan saat memuat gambar
            .into(binding.fotoUser) // Tampilkan gambar di CircleImageView


    }


    private fun setToAdapter(jamKerjaList: List<DataItemJamKerja>) {
        // Filter list to only include items with status "pending" or "done"
        val filteredList = jamKerjaList.filter { it.status == "pending" || it.status == "done" }

        jamKerjaAdapter = JamKerjaAdapter(filteredList)
        binding.rvJamKerja.layoutManager = LinearLayoutManager(this)
        binding.rvJamKerja.adapter = jamKerjaAdapter
        binding.rvJamKerja.itemAnimator = DefaultItemAnimator()


    }


    private fun getJamKerja(userId: Int) {
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

        apiServicesWithToken.getJamKerja(userId)
            .enqueue(object : Callback<JamKerjaResponse> {
                override fun onResponse(
                    call: Call<JamKerjaResponse>,
                    response: Response<JamKerjaResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body()
                        val jamKerjaList = responseData?.data ?: emptyList()
                        setToAdapter(jamKerjaList as List<DataItemJamKerja>)
                        // Log each item in the jamKerjaList
                        for (item in jamKerjaList) {
                            Log.i("JamKerjaData", "ID: ${item.id}, UserID: ${item.userId}, ActivityID: ${item.activityId}, Status: ${item.status}, StartTime: ${item.startTime}, EndTime: ${item.endTime}, WorkDuration: ${item.workDuration}, DeskripsiPending: ${item.deskripsiPending}")
                        }

                        Log.i("JamKerjaList", "Berhasil mendapatkan data jam kerja")

                        Log.i("JamKerjaList", "Berhasil mendapatkan data jam kerja")
                    } else {
                        Log.e("Retrofit onResponse", "Response not successful")
                    }
                }

                override fun onFailure(call: Call<JamKerjaResponse>, t: Throwable) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.message}", t)
                }
            })
    }



    private fun getFilterJamKerja(userId: Int, month: Int, year: Int) {
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

        apiServicesWithToken.getJamKerjaFilter(userId, month, year)
            .enqueue(object : Callback<JamKerjaResponse> {
                override fun onResponse(
                    call: Call<JamKerjaResponse>,
                    response: Response<JamKerjaResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body()
                        val jamKerjaList = responseData?.data ?: emptyList()
                        setToAdapter(jamKerjaList as List<DataItemJamKerja>)
                        // Log each item in the jamKerjaList


                        Log.i("JamKerjaList", "Berhasil mendapatkan data jam kerja")

                        Log.i("JamKerjaList", "Berhasil mendapatkan data jam kerja")
                    } else {
                        Log.e("Retrofit onResponse", "Response not successful")
                    }
                }

                override fun onFailure(call: Call<JamKerjaResponse>, t: Throwable) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.message}", t)
                }
            })
    }


    private fun getTokenFromSharedPreferences(): String? {
        val sharedPrefManager = SharedPrafManager(this)
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }

}
