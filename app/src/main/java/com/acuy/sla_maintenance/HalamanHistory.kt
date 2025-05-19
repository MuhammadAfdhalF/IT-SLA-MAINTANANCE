package com.acuy.sla_maintenance


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.acuy.sla_maintenance.adapter.HistoryAdapter
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.FragmentHalamanHistoryBinding
import com.acuy.sla_maintenance.model.DataItem
import com.acuy.sla_maintenance.model.GetLokasiResponseItem
import com.acuy.sla_maintenance.model.GetTollByUser
import com.acuy.sla_maintenance.model.PostUsers
import com.bumptech.glide.Glide
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HalamanHistory : Fragment() {
    private lateinit var binding: FragmentHalamanHistoryBinding
    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager

    private var selectedLokasiId: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHalamanHistoryBinding.inflate(inflater, container, false)
        val view = binding.root!!

        //post user
        apiServices = NetworkConfig().getServices()
        sharedPrefManager = SharedPrafManager(requireContext())

        // Panggil fungsi untuk mendapatkan token dari SharedPreferences
        val authToken = getTokenFromSharedPreferences()

        if (authToken != null) {
            // Panggil fungsi untuk mendapatkan dan menampilkan data pengguna
            getUsers(authToken)
        } else {
            Log.e("Token", "Token is null")
            // Handle null token here
        }


        val itemLokasi = listOf(
            "Bira Barat" to 1,
            "GTO 1 Bira Barat" to 2,
            "Bira Barat GRD 02" to 3,
            "Bira Barat Plaza" to 4,
            "Bira Barat RTM" to 5,
            "Gardu dan Plaza BRK" to 6,
            "GTO 1 Biringkanaya" to 7,
            "GTO 2 Biringkanaya" to 8,
            "GTO 3 Biringkanaya" to 9,
            "GRD 4 Biringkanaya" to 10,
            "GRD 5 Biringkanaya" to 11,
            "PCS Biringkanaya" to 12,
            "RTM Biringkanaya" to 13,
            "Bira Timur" to 14,
            "GTO 1 Bira Timur" to 15,
            "GTO 02 Bira Timur" to 16,
            "Gardu 03 Bira Timur" to 17,
            "PCS Bira Timur" to 18,
            "RTM Bira Timur" to 19,
            "Cambaya" to 20,
            "GTO 1 Cambaya" to 21,
            "GTO 2 Cambaya" to 22,
            "GRD3 CAMBAYA" to 23,
            "GRD4 CAMBAYA" to 24,
            "GTO 5 Cambaya" to 25
        )

        val adapterLokasi = ArrayAdapter(
            requireContext(),
            R.layout.list_company,
            itemLokasi.map { it.first } // Mengambil hanya nama lokasi dari setiap pasangan nilai
        )
        binding.panggilLokasiHistory.setAdapter(adapterLokasi)

        binding.panggilLokasiHistory.setOnItemClickListener { _, _, position, _ ->
            val selectedLokasi = itemLokasi[position]
            val selectedLocationId = selectedLokasi.second // Mengambil ID lokasi dari pasangan nilai
            val userId = sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
            getHistoryByLocation(userId, selectedLocationId.toString())
        }


        binding.clearFilter.setOnClickListener {
            binding.panggilLokasiHistory.setText("")
            val userId = sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
            val selectedLocation = "" // Atau dapatkan lokasi yang dipilih sebelumnya dan kirimkan kembali
            getHistoryByLocation(userId, selectedLocation)
        }



        val sharedPrefManager = SharedPrafManager(requireContext())
        val userId = sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
        getHistory(userId) // Memanggil fungsi getHistory dengan parameter userId


        if (authToken != null) {
            getLokasiFromAPI(authToken)
        } else {
            Log.e("Token", "Token is null")
            // Handle null token here
        }

        binding.cardFotoProfile.setOnClickListener {
            val profileFragment = HalamanProfile()
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, profileFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return view
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
        Glide.with(requireContext())
            .load(imageUrlAwal) // URL gambar yang diberikan oleh respons API
            .placeholder(R.drawable.ic_launcher_foreground) // Gambar placeholder yang akan ditampilkan saat proses unduhan gambar berlangsung
            .error(R.drawable.clear) // Gambar yang akan ditampilkan jika terjadi kesalahan saat memuat gambar
            .into(binding.fotoUser) // Tampilkan gambar di CircleImageView

    }

    // function get toll
//    private fun getHistory() {
//        val token = getTokenFromSharedPreferences() // Mendapatkan token dari SharedPreferences
//        val apiServices = NetworkConfig().getServices()
//
//        // Buat interceptor untuk menyertakan token dalam header permintaan
//        val tokenInterceptor = Interceptor { chain ->
//            val request = chain.request().newBuilder()
//                .addHeader("Authorization", "Bearer $token")
//                .build()
//            chain.proceed(request)
//        }
//
//        // Tambahkan interceptor ke OkHttpClient
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(tokenInterceptor)
//            .build()
//
//        // Buat retrofit baru dengan OkHttpClient yang telah dikonfigurasi
//        val retrofit = Retrofit.Builder()
//            .baseUrl(NetworkConfig().BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//
//        // Buat instance layanan API dengan retrofit yang telah dikonfigurasi
//        val apiServicesWithToken = retrofit.create(ApiServices::class.java)
//
//        // Panggil endpoint yang sesuai dengan layanan API yang baru
//        apiServicesWithToken.getToll().enqueue(object : Callback<ResponseListReport2> {
//            override fun onResponse(
//                call: Call<ResponseListReport2>,
//                response: Response<ResponseListReport2>
//            ) {
//                if (response.isSuccessful) {
//                    val responseData = response.body()
//                    val receiveDatas =
//                        responseData?.data?.data // Akses properti data dari objek Data
//                    receiveDatas?.let { setToAdapter(it) }
//                } else {
//                    Log.e("Retrofit onResponse", "Response not successful")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseListReport2>, t: Throwable) {
//                Log.e("Retrofit onFailure", "onFailure: ${t.stackTrace}")
//            }
//        })
//    }


    // function get history by user id
    private fun getHistory(userId: Int) {
        val token = getTokenFromSharedPreferences() // Mendapatkan token dari SharedPreferences
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
        apiServicesWithToken.getTollByUser(userId)
            .enqueue(object : Callback<GetTollByUser> {
                override fun onResponse(
                    call: Call<GetTollByUser>,
                    response: Response<GetTollByUser>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body()
                        val receiveDatas =
                            responseData?.data?.data  // Akses properti data dari objek GetTollByUser
                        receiveDatas?.let { setToAdapter(it) }
                    } else {
                        Log.e("Retrofit onResponse", "Response not successful")
                    }
                }

                override fun onFailure(call: Call<GetTollByUser>, t: Throwable) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.stackTrace}")
                }
            })
    }



    private fun getHistoryByLocation(userId: Int, selectedLocationId: String) {
        val token = getTokenFromSharedPreferences() // Retrieve token from SharedPreferences
        if (token != null ) {
            Log.d("Token", "Token: $token")
            val apiServices = NetworkConfig().getServices()

            // Create an interceptor to include the token in the request header
            val tokenInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }

            // Build OkHttpClient with the token interceptor
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()

            // Create a new Retrofit instance with the configured OkHttpClient
            val retrofit = Retrofit.Builder()
                .baseUrl(NetworkConfig().BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            // Create an instance of the API service with the configured retrofit
            val apiServicesWithToken = retrofit.create(ApiServices::class.java)

            // Call the appropriate endpoint with the new API service
            apiServicesWithToken.getTollFIlterUser(userId, selectedLocationId.toIntOrNull())
                .enqueue(object : Callback<GetTollByUser> {
                    override fun onResponse(call: Call<GetTollByUser>, response: Response<GetTollByUser>) {
                        if (response.isSuccessful) {
                            val responseData = response.body()
                            val receiveDatas = responseData?.data?.data // Access the 'data' property of the Data object
                            receiveDatas?.let { setToAdapter(it) }
                        } else {
                            Log.e("Retrofit onResponse", "Response not successful")
                        }
                    }

                    override fun onFailure(call: Call<GetTollByUser>, t: Throwable) {
                        Log.e("Retrofit onFailure", "onFailure: ${t.message}")
                    }
                })
        } else {
            Log.e("Token", "Token is empty or null")
        }
    }


    // Mengatur adapter untuk RecyclerView
    private fun setToAdapter(receiveDatas: List<DataItem?>) {

        // Filter data berdasarkan status "done"
        val doneReports = receiveDatas.filter { it?.status == "done" }


        val adapter = HistoryAdapter(doneReports)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
        binding.rvHistory.itemAnimator = DefaultItemAnimator()

        val layoutParams = binding.rvHistory.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = 0
        binding.rvHistory.layoutParams = layoutParams
    }

    // Fungsi untuk mengambil data lokasi dari API
    private fun getLokasiFromAPI(token: String) {
        val formattedToken = "Bearer $token"
        apiServices.getLokasi(formattedToken)
            .enqueue(object : Callback<List<GetLokasiResponseItem>> {
                override fun onResponse(
                    call: Call<List<GetLokasiResponseItem>>,
                    response: Response<List<GetLokasiResponseItem>>
                ) {
                    if (response.isSuccessful) {
                        val lokasiList = response.body()
                        if (!lokasiList.isNullOrEmpty()) {
                            // Ambil ID lokasi dari setiap item dalam respons API
                            val itemLokasi = lokasiList.map { it.namaLokasi to it.id }

                            // Buat adapter untuk AutoCompleteTextView menggunakan data lokasi dari API
                            val adapterLokasi = ArrayAdapter(
                                requireContext(),
                                R.layout.list_company,
                                itemLokasi.map { it.first } // Mengambil hanya nama lokasi dari setiap pasangan nilai
                            )
                            binding.panggilLokasiHistory.setAdapter(adapterLokasi)

                            // Handle aksi ketika lokasi dipilih
                            binding.panggilLokasiHistory.setOnItemClickListener { _, _, position, _ ->
                                val selectedLokasi = itemLokasi[position]
                                val selectedLocationId = selectedLokasi.second // Mengambil ID lokasi dari pasangan nilai
                                val userId = sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)
                                getHistoryByLocation(userId, selectedLocationId.toString())
                            }
                        } else {
                            Log.e("Retrofit onResponse", "Response body is null or empty")
                        }
                    } else {
                        Log.e("Retrofit onResponse", "Response not successful: ${response.code()}")
                        Toast.makeText(
                            requireContext(),
                            "Terjadi kesalahan (${response.code()}) saat mengambil data lokasi. Silakan coba lagi nanti.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<GetLokasiResponseItem>>,
                    t: Throwable
                ) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.message}")
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan saat mengambil data lokasi. Silakan coba lagi nanti.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    fun createLokasiAdapter(context: Context, data: List<LokasiItem>): ArrayAdapter<LokasiItem> {
        return object : ArrayAdapter<LokasiItem>(context, R.layout.list_company, data) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_company, parent, false)

                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = data[position].namaLokasi

                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_company, parent, false)

                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = data[position].namaLokasi

                return view
            }
        }
    }

    data class LokasiItem(val id: Int, val namaLokasi: String)

    private fun displayLokasi(lokasiList: List<GetLokasiResponseItem>?) {
        val autoCompleteTextView = binding.panggilLokasiHistory

        if (lokasiList.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Tidak ada data lokasi yang ditemukan.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val lokasiItems = lokasiList.map { LokasiItem(it.id ?: 0, it.namaLokasi ?: "") }
        val adapter = createLokasiAdapter(requireContext(), lokasiItems)

        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)
            if (selectedItem != null) {
                selectedLokasiId = selectedItem.id
                val selectedNamaLokasi = selectedItem.namaLokasi
                Log.i("Lokasi", "Anda memilih lokasi: $selectedNamaLokasi")
                Log.i("Lokasi", "ID lokasi: $selectedLokasiId")
            } else {
                Log.e("Lokasi", "Item yang dipilih null")
            }
        }
    }


    private fun getTokenFromSharedPreferences(): String? {
        val sharedPrefManager = SharedPrafManager(requireContext())
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }

}