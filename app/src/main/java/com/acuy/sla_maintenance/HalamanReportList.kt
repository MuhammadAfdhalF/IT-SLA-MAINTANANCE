package com.acuy.sla_maintenance


import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acuy.sla_maintenance.adapter.ReportAdapter
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.FragmentHalamanReportListBinding
import com.acuy.sla_maintenance.model.DataReport2
import com.acuy.sla_maintenance.model.GetLokasiResponseItem
import com.acuy.sla_maintenance.model.PostUsers
import com.acuy.sla_maintenance.model.ResponseListReport2
import com.bumptech.glide.Glide
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HalamanReportList : Fragment() {
    private lateinit var binding: FragmentHalamanReportListBinding
    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager


    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHalamanReportListBinding.inflate(inflater, container, false)
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


        // Mendapatkan token dari SharedPreferences
        val sharedPrefManager = SharedPrafManager(requireContext())
        val token = sharedPrefManager.getString(Constant.USER_TOKEN)

        // Menampilkan token di logcat jika tersedia
        token?.let { Log.d("Token", "Token: $it") }


        // Panggil metode getReport() untuk mengambil data
        // Panggil metode getReport() untuk mengambil data
        getReport(currentPage)
        binding.rvReport.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        // Load more data here
                        // Panggil metode untuk memuat data tambahan
                        loadMoreData()
                    } else if (firstVisibleItemPosition == 0) {
                        // Jika pengguna telah mencapai posisi awal, panggil metode untuk memuat halaman sebelumnya
                        loadPreviousData()
                    }
                }
            }
        })


        //filter company
        val itemCompany = listOf("MMN", "MAN")
        val adapterCompany = ArrayAdapter(
            requireContext(),
            R.layout.list_company,
            itemCompany
        )
        binding.panggilCompany.setAdapter(adapterCompany)

        binding.panggilCompany.setOnItemClickListener { _, _, _, _ ->
            val selectedCompany = binding.panggilCompany.text.toString().toLowerCase()
            val selectedStatus = binding.panggilFilterStatus.text.toString().toLowerCase()
            val selectedLocation = binding.panggilFilterLokasi.text.toString()
            getReportByFilters(selectedCompany, selectedStatus, selectedLocation)
        }

        //filter status
        val itemStatus = listOf("Process", "Done", "Pending")
        val adapterStatus = ArrayAdapter(
            requireContext(),
            R.layout.list_company,
            itemStatus
        )
        binding.panggilFilterStatus.setAdapter(adapterStatus)

        binding.panggilFilterStatus.setOnItemClickListener { _, _, _, _ ->
            val selectedStatus = binding.panggilFilterStatus.text.toString().toLowerCase()
            val selectedCompany = binding.panggilCompany.text.toString().toLowerCase()
            val selectedLocation = binding.panggilFilterLokasi.text.toString()
            getReportByFilters(selectedCompany, selectedStatus, selectedLocation)
        }

//        //filter lokasi
//        data class LokasiItem(val nama: String, val id: Int)

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
        binding.panggilFilterLokasi.setAdapter(adapterLokasi)

        binding.panggilFilterLokasi.setOnItemClickListener { _, _, position, _ ->
            val selectedLokasi = itemLokasi[position]
            val selectedLocationId =
                selectedLokasi.second // Mengambil ID lokasi dari pasangan nilai
            val selectedCompany = binding.panggilCompany.text.toString().toLowerCase()
            val selectedStatus = binding.panggilFilterStatus.text.toString().toLowerCase()
            getReportByFilters(selectedCompany, selectedStatus, selectedLocationId.toString())
        }

        if (authToken != null) {
            getLokasiFromAPI(authToken)
        } else {
            Log.e("Token", "Token is null")
            // Handle null token here
        }



//clear filter
        binding.clearFilter.setOnClickListener {
            // Reset nilai teks untuk setiap filter
            binding.panggilCompany.setText("")
            binding.panggilFilterStatus.setText("")
            binding.panggilFilterLokasi.setText("")

            // Tambahkan log sebelum pemanggilan getReport(currentPage)
            Log.i("Clear Filter", "Current Page before getReport: $currentPage")

            // Panggil metode getReport() untuk mengambil data dengan currentPage saat ini
            getReport(currentPage)

            // Tambahkan log setelah pemanggilan getReport(currentPage)
            Log.i("Clear Filter", "Current Page after getReport: $currentPage")
        }



        binding.cardFotoProfile.setOnClickListener {
            val profileFragment = HalamanProfile()
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, profileFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


//        binding.rvReport.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val visibleItemCount = layoutManager.childCount
//                val totalItemCount = layoutManager.itemCount
//                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//
//                if (!isLoading && !isLastPage) {
//                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
//                        // Load more data here
//                        // Panggil metode untuk memuat data tambahan
//                        loadMoreData()
//                    }
//                }
//            }
//        })


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

    // Function to load more data
    private fun loadMoreData() {
        val nextPage = currentPage + 1 // Increment the page number
        getReport(nextPage) // Fetch the next page of data
    }


    // Function to load previous data (previous page)
    private fun loadPreviousData() {
        if (currentPage > 1) { // Pastikan currentPage tidak kurang dari 1
            val previousPage = currentPage - 1 // Kurangi nomor halaman untuk memuat halaman sebelumnya
            getReport(previousPage) // Fetch the previous page of data
        }
    }

    private fun getReport(page: Int) {
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
        // Menggunakan nomor halaman yang diberikan dalam permintaan
        apiServicesWithToken.getToll(page).enqueue(object : Callback<ResponseListReport2> {
            override fun onResponse(
                call: Call<ResponseListReport2>,
                response: Response<ResponseListReport2>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    val receiveDatas = responseData?.data?.data // Akses properti data dari objek Data

                    // Panggil method addDataToAdapter untuk menambahkan data baru ke adapter
                    receiveDatas?.let { addDataToAdapter(it) }

                    // Perbarui currentPage hanya jika ini adalah halaman pertama
                    if (page == 1) {
                        currentPage = page // currentPage diperbarui dengan nilai page
                    }

                    // Penanganan kasus khusus untuk identifikasi halaman terakhir
                    if (receiveDatas.isNullOrEmpty() || receiveDatas.size < PAGE_SIZE) {
                        // Jika jumlah data kurang dari PAGE_SIZE, ini adalah halaman terakhir
                        isLastPage = true
                    }

                    Log.i("ReportList", "berhasil page ke-$page")

                } else {
                    // Tangani respons tidak berhasil dengan menampilkan pesan kesalahan yang bermakna
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody ?: "Unknown error"
                    Log.e("Retrofit onResponse", "Response not successful: $errorMessage")
                    // Atau jika ingin menampilkan pesan default
                    // Log.e("Retrofit onResponse", "Response not successful")
                }
            }

            override fun onFailure(call: Call<ResponseListReport2>, t: Throwable) {
                // Tangani kesalahan saat gagal melakukan panggilan ke server
                Log.e("Retrofit onFailure", "Failed to get report data: ${t.message}", t)
            }
        })
    }

    // Method untuk menambahkan data baru ke adapter
    private fun addDataToAdapter(receiveDatas: List<DataReport2?>) {
        // Jika adapter sudah ada, tambahkan data baru ke adapter yang ada
        if (binding.rvReport.adapter != null && binding.rvReport.adapter is ReportAdapter) {
            val adapter = binding.rvReport.adapter as ReportAdapter
            adapter.addData(receiveDatas)
        } else {
            // Jika adapter belum ada, buat adapter baru dan atur ke RecyclerView
            val adapter = ReportAdapter(receiveDatas)
            binding.rvReport.layoutManager = LinearLayoutManager(requireContext())
            binding.rvReport.adapter = adapter
            binding.rvReport.itemAnimator = DefaultItemAnimator()

//            // Atur margin top untuk RecyclerView
//            val layoutParams = binding.rvReport.layoutParams as ConstraintLayout.LayoutParams
//            layoutParams.topMargin = 100
//            binding.rvReport.layoutParams = layoutParams
        }
    }


//    private fun getReport() {
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
//                    // Tangani respons tidak berhasil dengan menampilkan pesan kesalahan yang bermakna
//                    val errorBody = response.errorBody()?.string()
//                    val errorMessage = errorBody ?: "Unknown error"
//                    Log.e("Retrofit onResponse", "Response not successful: $errorMessage")
//                    // Atau jika ingin menampilkan pesan default
//                    // Log.e("Retrofit onResponse", "Response not successful")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseListReport2>, t: Throwable) {
//                // Tangani kesalahan saat gagal melakukan panggilan ke server
//                Log.e("Retrofit onFailure", "Failed to get report data: ${t.message}", t)
//            }
//        })
//    }

    private fun setToAdapter(receiveDatas: List<DataReport2?>) {
        val adapter = ReportAdapter(receiveDatas)
        binding.rvReport.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReport.adapter = adapter
        binding.rvReport.itemAnimator = DefaultItemAnimator()

//
//        val layoutParams = binding.rvReport.layoutParams as ConstraintLayout.LayoutParams
//        layoutParams.topMargin = 100
//        binding.rvReport.layoutParams = layoutParams
    }


    private fun getReportByFilters(company: String, status: String, selectedLocationId: String) {
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
        apiServicesWithToken.getTollAllFilter(company, status, selectedLocationId.toIntOrNull())
            .enqueue(object : Callback<ResponseListReport2> {
                override fun onResponse(
                    call: Call<ResponseListReport2>,
                    response: Response<ResponseListReport2>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body()
                        val receiveDatas =
                            responseData?.data?.data // Akses properti data dari objek Data
                        receiveDatas?.let { setToAdapter(it) }

                        Log.i("ReportList filter", "berhasil filter")

                    } else {
                        Log.e("Retrofit onResponse", "Response not successful")
                    }
                }

                override fun onFailure(call: Call<ResponseListReport2>, t: Throwable) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.stackTrace}")
                }
            })
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
                            binding.panggilFilterLokasi.setAdapter(adapterLokasi)

                            // Handle aksi ketika lokasi dipilih
                            binding.panggilFilterLokasi.setOnItemClickListener { _, _, position, _ ->
                                val selectedLokasi = itemLokasi[position]
                                val selectedLocationId = selectedLokasi.second // Mengambil ID lokasi dari pasangan nilai
                                val selectedCompany = binding.panggilCompany.text.toString().toLowerCase()
                                val selectedStatus = binding.panggilFilterStatus.text.toString().toLowerCase()
                                getReportByFilters(selectedCompany, selectedStatus, selectedLocationId.toString())
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


    private fun getTokenFromSharedPreferences(): String? {
        val sharedPrefManager = SharedPrafManager(requireContext())
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }

}


//class HalamanReportList : Fragment() {
//    private lateinit var binding: FragmentHalamanReportListBinding
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentHalamanReportListBinding.inflate(inflater, container, false)
//        val view = binding.root!!
//
//
//        // Mendapatkan token dari SharedPreferences
//        val sharedPrefManager =
//            SharedPrafManager(requireContext()) // Gunakan requireContext() untuk mendapatkan Context
//        val token = sharedPrefManager.getString(Constant.USER_TOKEN)
//
//        // Menampilkan token di logcat jika tersedia
//        token?.let { Log.d("Token", "Token: $it") }
//
//        val filterLokasi = binding.filterLokasi
//
//        filterLokasi.setOnClickListener {
//            val intent = Intent(requireActivity(), HalamanDetailReport::class.java)
//            startActivity(intent)
//        }
//
//
////                List Company
//        val itemCompany = listOf("mmn", "jtse")
//        val adapterCompany = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            itemCompany
//        )
//        binding.panggilCompany.setAdapter(adapterCompany)
//
//        binding.panggilCompany.setOnItemClickListener { parent, view, position, id ->
//            val selectedCompany = parent.getItemAtPosition(position) as String
//            val selectedStatus = binding.panggilFilterStatus.text.toString()
//            val selectedLocation = binding.panggilFilterLokasi.text.toString()
//            getReportByFilters(selectedCompany, selectedStatus, selectedLocation)
//        }
////        binding.panggilCompany.setOnItemClickListener { parent, view, position, id ->
////            val selectedCompany = parent.getItemAtPosition(position) as String
////            getReportByCompany(selectedCompany) // Ambil data laporan berdasarkan perusahaan yang dipilih
////        }
//
//        //                List Status
//        val itemStatus = listOf("process", "done")
//        val adapterStatus = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            itemStatus
//        )
//        binding.panggilFilterStatus.setAdapter(adapterStatus)
//
//        binding.panggilFilterStatus.setOnItemClickListener { parent, view, position, id ->
//            val selectedStatus = parent.getItemAtPosition(position) as String
//            val selectedCompany = binding.panggilCompany.text.toString()
//            val selectedLocation = binding.panggilFilterLokasi.text.toString()
//            getReportByFilters(selectedCompany, selectedStatus, selectedLocation)
//        }
//
//        //  list lokasi
//        val itemLokasi = listOf(
//            "Bira Barat",
//            "GTO 1 Bira Barat",
//            "Bira Barat GRD 02",
//            "Bira Barat Plaza",
//            "Bira Barat RTM",
//            "Gardu dan Plaza BRK",
//            "GTO 1 Biringkanaya",
//            "GTO 2 Biringkanaya",
//            "GTO 3 Biringkanaya",
//            "GRD 4 Biringkanaya",
//            "GRD 5 Biringkanaya",
//            "PCS Biringkanaya",
//            "RTM Biringkanaya",
//            "Bira Timur",
//            "GTO 1 Bira Timur",
//            "GTO 02 Bira Timur",
//            "Gardu 03 Bira Timur",
//            "PCS Bira Timur",
//            "RTM Bira Timur",
//            "Cambaya",
//            "GTO 1 Cambaya",
//            "GTO 2 Cambaya",
//            "GRD3 CAMBAYA",
//            "GRD4 CAMBAYA",
//            "GTO 5 Cambaya"
//        )
//        val adapterLokasi = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            itemLokasi
//        )
//        binding.panggilFilterLokasi.setAdapter(adapterLokasi)
//
//        binding.panggilFilterLokasi.setOnItemClickListener { parent, view, position, id ->
//            val selectedLocation = parent.getItemAtPosition(position) as String
//            val selectedCompany = binding.panggilCompany.text.toString()
//            val selectedStatus = binding.panggilFilterStatus.text.toString()
//            getReportByFilters(selectedCompany, selectedStatus, selectedLocation)
//        }
//
//
//        // Panggil metode getReport() untuk mengambil data
//        getReport()
//        return view
//    }
//
//    //    private fun getReport() {
////        NetworkConfig().getServices()
////            .getToll()
////            .enqueue(object : Callback<ResponseListReport2> {
////                override fun onResponse(
////                    call: Call<ResponseListReport2>,
////                    response: Response<ResponseListReport2>
////                ) {
////                    if (response.isSuccessful) {
////                        val receiveDatas = response.body()?.data
////                        receiveDatas?.let { setToAdapter(it) }
////                    } else {
////                        Log.e("Retrofit onResponse", "Response not successful")
////                    }
////                }
////
////                override fun onFailure(call: Call<ResponseListReport2>, t: Throwable) {
////                    Log.e("Retrofit onFailure", "onFailure: ${t.stackTrace}")
////                }
////            })
////    }
//    private fun getReport() {
//        NetworkConfig().getServices()
//            .getToll()
//            .enqueue(object : Callback<ResponseListReport2> {
//                override fun onResponse(
//                    call: Call<ResponseListReport2>,
//                    response: Response<ResponseListReport2>
//                ) {
//                    if (response.isSuccessful) {
//                        val receiveDatas = response.body()?.data
//                        receiveDatas?.let { setToAdapter(it) }
//                    } else {
//                        Log.e("Retrofit onResponse", "Response not successful")
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseListReport2>, t: Throwable) {
//                    Log.e("Retrofit onFailure", "onFailure: ${t.stackTrace}")
//                }
//            })
//    }
//
//    private fun setToAdapter(receiveDatas: List<DataReport2?>) {
//        val adapter = ReportAdapter(receiveDatas)
//        binding.rvReport.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvReport.adapter = adapter
//        binding.rvReport.itemAnimator = DefaultItemAnimator()
//    }
//
//    private fun getReportByFilters(company: String, status: String, locationName: String) {
//        NetworkConfig().getServices()
//            .getTollAllFilter(company, status, locationName)
//            .enqueue(object : Callback<ResponseListReport2> {
//                override fun onResponse(
//                    call: Call<ResponseListReport2>,
//                    response: Response<ResponseListReport2>
//                ) {
//                    if (response.isSuccessful) {
//                        val receiveDatas = response.body()?.data
//                        receiveDatas?.let { setToAdapter(it) }
//                    } else {
//                        Log.e("Retrofit onResponse", "Response not successful")
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseListReport2>, t: Throwable) {
//                    Log.e("Retrofit onFailure", "onFailure: ${t.stackTrace}")
//                }
//            })
//    }
//
//}


//    private fun getReportByCompany(company: String) {
//        NetworkConfig().getServices()
//            .getTollFilter(company)
//            .enqueue(object : Callback<ResponseListReport2> {
//                override fun onResponse(
//                    call: Call<ResponseListReport2>,
//                    response: Response<ResponseListReport2>
//                ) {
//                    if (response.isSuccessful) {
//                        val receiveDatas = response.body()?.data
//                        receiveDatas?.let { setToAdapter(it) }
//                    } else {
//                        Log.e("Retrofit onResponse", "Response not successful")
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseListReport2>, t: Throwable) {
//                    Log.e("Retrofit onFailure", "onFailure: ${t.stackTrace}")
//                }
//            })
//    }


//filter company
// Inisialisasi variabel TextView dan ImageView
//        filterCompanyTextView = view.findViewById(R.id.filterCompany)
//        filterCompanyImageView = view.findViewById(R.id.imgFilterCompany)
//
//        val filterCompanyCardView = view.findViewById<CardView>(R.id.filterCompany)
//        filterCompanyCardView.setOnClickListener {
//            // Tampilkan dialog pilihan perusahaan menggunakan AlertDialog
//            val companies = arrayOf("jtse", "mmn")
//            val builder = AlertDialog.Builder(requireContext())
//            builder.setTitle("Pilih Perusahaan")
//                .setItems(companies) { _, which ->
//                    // Ubah teks pada TextView sesuai dengan perusahaan yang dipilih
//                    filterCompanyTextView.text = companies[which]
//                }
//            builder.create().show()
//        }


//package com.acuy.sla_maintenance
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DefaultItemAnimator
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.acuy.sla_maintenance.adapter.ReportAdapter
//import com.acuy.sla_maintenance.config.NetworkConfig
//import com.acuy.sla_maintenance.databinding.FragmentHalamanReportListBinding
//import com.acuy.sla_maintenance.model.DataReport
//import com.acuy.sla_maintenance.model.ResponseListReport
//import retrofit2.Call
//import retrofit2.Response
//
//class HalamanReportList : Fragment() {
//    private lateinit var binding: FragmentHalamanReportListBinding
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentHalamanReportListBinding.inflate(inflater, container, false)
//        val view = binding.root!!
//
//        val filterLokasi = binding.filterLokasi
//
//        filterLokasi.setOnClickListener {
//            val intent = Intent(requireActivity(), HalamanDetailReport::class.java)
//            startActivity(intent)
//        }
//
//        getReport()
//        return view
//    }
//
//    private fun getReport() {
//        NetworkConfig().getServices()
//            .getToll()
//            .enqueue(object : retrofit2.Callback<ResponseListReport> {
//                override fun onResponse(
//                    call: Call<ResponseListReport>,
//                    response: Response<ResponseListReport>
//                ) {
////                   this@HalamanReportList.binding.progressIndicator.visibility = view.Gone
//                    if (response.isSuccessful) {
//                        val receiveDatas = response.body()?.data
//                        setToAdapter(receiveDatas)
//                    }
//
//                }
//
//                override fun onFailure(call: Call<ResponseListReport>, t: Throwable) {
//                    Log.d("Retrofit onFailure: ", "onFailure: ${t.stackTrace}")
//                }
//
//            }
//            )
//    }
//
//    private fun setToAdapter(receiveDatas: List<DataReport?>?) {
//        val adapter = ReportAdapter(receiveDatas)
//        val lm = LinearLayoutManager(requireContext())
//        this.binding.rvReport.layoutManager=lm
//        this.binding.rvReport.itemAnimator=DefaultItemAnimator()
//        this.binding.rvReport.adapter = adapter
//
//    }
//}