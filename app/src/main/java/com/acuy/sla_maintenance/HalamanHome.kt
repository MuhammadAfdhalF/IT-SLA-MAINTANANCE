package com.acuy.sla_maintenance

import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.FragmentHalamanHomeBinding
import com.acuy.sla_maintenance.marker.CustomMarkerView
import com.acuy.sla_maintenance.model.DataItemGrafik
import com.acuy.sla_maintenance.model.GrafikJamKerjaResponse
import com.acuy.sla_maintenance.model.PostUsers
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HalamanHome : Fragment() {
    private lateinit var binding: FragmentHalamanHomeBinding
    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager

    val profitValues = ArrayList<BarEntry>()
    private var totalDuration: Int = 0
    private var selectedYear: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHalamanHomeBinding.inflate(inflater, container, false)
        val view = binding.root


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


        // Mengambil nilai USER_ID dari SharedPreferences
        val userId = sharedPrefManager.getInt(Constant.USER_ID.toString(), -1)

        // Mengambil nilai USER_TOKEN dari SharedPreferences
        val userToken = getTokenFromSharedPreferences()

        // Menampilkan nilai USER_ID dan USER_TOKEN dalam log
        Log.d("HalamanHome", "USER_ID: $userId")
        Log.d("HalamanHome", "USER_TOKEN: $userToken")

        val buttonZoomOut = binding.buttonZoomOut
        buttonZoomOut.setOnClickListener {
            // Mengatur grafik ke zoom level default
            binding.barChart.fitScreen()
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
        val adapterTahun = ArrayAdapter(requireContext(), R.layout.list_company, years)
        binding.panggilTahunFIlter.setAdapter(adapterTahun)

        // Atur tindakan yang diambil saat item tahun dipilih
        binding.panggilTahunFIlter.setOnItemClickListener { parent, view, position, id ->
            val selectedYearString = parent.getItemAtPosition(position).toString()
            selectedYear = selectedYearString.toIntOrNull() ?: 0
            println("Tahun yang dipilih: $selectedYear")
            // Di sini Anda dapat memanggil fungsi getGrafikJamKerja dengan tahun yang dipilih
            getGrafikJamKerja(userId, selectedYear)
        }

        //        btnJamKerja
        val btnListJamKerja = binding.btnListJamKerja
        btnListJamKerja.setOnClickListener {
            val intent = Intent(requireActivity(), HalamanJamKerja::class.java)
            startActivity(intent)
        }

        binding.cardFotoProfile.setOnClickListener {
            val profileFragment = HalamanProfile()
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, profileFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        getGrafikJamKerja(userId, currentYear)

        return view
    }

    private fun getGrafikJamKerja(userId: Int, year: Int) {
        val token = getTokenFromSharedPreferences()
        val apiServices = NetworkConfig().getServices()

        val tokenInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }

        // Tambahkan interceptor logging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConfig().BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val apiServicesWithToken = retrofit.create(ApiServices::class.java)

        apiServicesWithToken.getGrafikJamKerja(userId, year)
            .enqueue(object : Callback<GrafikJamKerjaResponse> {
                override fun onResponse(
                    call: Call<GrafikJamKerjaResponse>,
                    response: Response<GrafikJamKerjaResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body()
                        val grafikDataList = responseData?.data ?: emptyList()

                        profitValues.clear()
                        if (grafikDataList.isNotEmpty()) {
                            grafikDataList.forEach { grafikData ->
                                grafikData?.durations?.forEachIndexed { index, duration ->
                                    val hours = convertSecondsToHours(duration)
                                    profitValues.add(BarEntry(index.toFloat(), hours))
                                }
                            }
                            setChart()
                            grafikDataList[0]?.let { displayTotalDuration(it) }
                            binding.noDataTextView.visibility = View.GONE


                        } else {
                            // Jika tidak ada data, tampilkan total durasi 0
                            displayTotalDuration(DataItemGrafik(total = 0))
                            hideChart()
                            binding.noDataTextView.visibility = View.VISIBLE

                        }
                    } else {
                        Log.e("Retrofit onResponse", "Response not successful")
                        hideChart()
                    }
                }

                override fun onFailure(call: Call<GrafikJamKerjaResponse>, t: Throwable) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.message}", t)
                    hideChart()
                }
            })
    }

    private fun displayTotalDuration(total: DataItemGrafik) {
        // Mengambil total durasi dari objek DataItemGrafik
        val totalDuration = total.total ?: 0

        // Menghitung jumlah jam
        val hours = totalDuration / 3600

        // Menghitung jumlah menit
        val minutes = (totalDuration % 3600) / 60

        // Format pesan untuk menampilkan total durasi dalam format jam dan menit
        val message = String.format("%d jam %02d menit", hours, minutes)

        // Update UI dengan total durasi
        binding.panggilTotalJamKerjaUSer.text = message
    }


    private fun convertSecondsToHours(seconds: Int?): Float {
        return (seconds ?: 0) / 3600f
    }
//
//    private fun setChart() {
//        binding.barChart.visibility = View.VISIBLE // Pastikan grafik ditampilkan
//        binding.barChart.description.isEnabled = false
//        binding.barChart.setPinchZoom(true)
//        binding.barChart.setDrawBarShadow(false)
//        binding.barChart.setDrawGridBackground(false)
//
//        val xAxis = binding.barChart.xAxis
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setDrawAxisLine(false)
//        xAxis.setDrawGridLines(false)
//        xAxis.granularity = 1f
//        xAxis.isGranularityEnabled = true
//        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Jan", "Feb", "Mar", "Apr",
//            "Mei", "Juni", "Juli", "Agust", "Sept", "Okt", "Nov", "Des"))
//        binding.barChart.axisLeft.setDrawGridLines(false)
//        binding.barChart.legend.isEnabled = false
//
//        binding.barChart.axisLeft.setDrawGridLines(false)
//        binding.barChart.axisRight.setDrawGridLines(false)
//        binding.barChart.axisRight.setDrawLabels(false)
//        binding.barChart.axisRight.setDrawAxisLine(false)
//
//        val limitLine = LimitLine(0f, "")
//        limitLine.lineWidth = 1f
//        limitLine.lineColor = Color.GRAY
//        binding.barChart.axisLeft.addLimitLine(limitLine)
//
//        binding.barChart.legend.isEnabled = false
//        binding.barChart.axisLeft.axisMinimum = 0f
//
//        val barColors = listOf(
//            Color.parseColor("#BBDEFB"),
//            Color.parseColor("#90CAF9"),
//            Color.parseColor("#64B5F6"),
//            Color.parseColor("#42A5F5"),
//            Color.parseColor("#2196F3"),
//            Color.parseColor("#1E88E5"),
//            Color.parseColor("#1976D2"),
//            Color.parseColor("#1565C0"),
//            Color.parseColor("#0D47A1"),
//            Color.parseColor("#82B1FF"),
//            Color.parseColor("#448AFF"),
//            Color.parseColor("#2979FF")
//        )
//
//        val barDataSetter: BarDataSet
//        if (binding.barChart.data != null && binding.barChart.data.dataSetCount > 0) {
//            barDataSetter = binding.barChart.data.getDataSetByIndex(0) as BarDataSet
//            barDataSetter.values = profitValues
//            binding.barChart.data.notifyDataChanged()
//            binding.barChart.notifyDataSetChanged()
//        } else {
//            barDataSetter = BarDataSet(profitValues, "Data Set")
//            barDataSetter.colors = barColors
//            barDataSetter.setDrawValues(false)
//
//            val dataSet = ArrayList<IBarDataSet>()
//            dataSet.add(barDataSetter)
//
//            val data = BarData(dataSet)
//            binding.barChart.data = data
//            binding.barChart.setFitBars(true)
//        }
//
//        binding.barChart.invalidate()
//    }


    private fun setChart() {
        binding.barChart.visibility = View.VISIBLE // Pastikan grafik ditampilkan
        binding.barChart.description.isEnabled = false
        binding.barChart.setPinchZoom(true)
        binding.barChart.setDrawBarShadow(false)
        binding.barChart.setDrawGridBackground(false)

        val xAxis = binding.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Jan", "Feb", "Mar", "Apr",
            "Mei", "Juni", "Juli", "Agust", "Sept", "Okt", "Nov", "Des"))
        binding.barChart.axisLeft.setDrawGridLines(false)
        binding.barChart.legend.isEnabled = false

        binding.barChart.axisLeft.setDrawGridLines(false)
        binding.barChart.axisRight.setDrawGridLines(false)
        binding.barChart.axisRight.setDrawLabels(false)
        binding.barChart.axisRight.setDrawAxisLine(false)

        val limitLine = LimitLine(0f, "")
        limitLine.lineWidth = 1f
        limitLine.lineColor = Color.GRAY
        binding.barChart.axisLeft.addLimitLine(limitLine)

        binding.barChart.legend.isEnabled = false
        binding.barChart.axisLeft.axisMinimum = 0f

        val barColors = listOf(
            Color.parseColor("#BBDEFB"),
            Color.parseColor("#90CAF9"),
            Color.parseColor("#64B5F6"),
            Color.parseColor("#42A5F5"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#1E88E5"),
            Color.parseColor("#1976D2"),
            Color.parseColor("#1565C0"),
            Color.parseColor("#0D47A1"),
            Color.parseColor("#82B1FF"),
            Color.parseColor("#448AFF"),
            Color.parseColor("#2979FF")
        )

        val barDataSetter: BarDataSet
        if (binding.barChart.data != null && binding.barChart.data.dataSetCount > 0) {
            barDataSetter = binding.barChart.data.getDataSetByIndex(0) as BarDataSet
            barDataSetter.values = profitValues
            binding.barChart.data.notifyDataChanged()
            binding.barChart.notifyDataSetChanged()
        } else {
            barDataSetter = BarDataSet(profitValues, "Data Set")
            barDataSetter.colors = barColors
            barDataSetter.setDrawValues(false)

            val dataSet = ArrayList<IBarDataSet>()
            dataSet.add(barDataSetter)

            val data = BarData(dataSet)
            binding.barChart.data = data
            binding.barChart.setFitBars(true)
        }

        // Tambahkan CustomMarkerView
        val markerView = CustomMarkerView(requireContext(), R.layout.custom_marker_view)
        markerView.chartView = binding.barChart
        binding.barChart.marker = markerView

        binding.barChart.invalidate()
    }



    private fun hideChart() {
        binding.barChart.clear()
        binding.barChart.visibility = View.GONE
        binding.noDataTextView.visibility = View.VISIBLE
        binding.buttonZoomOut.visibility = View.GONE
    }

//    private fun setChart() {
//        binding.barChart.description.isEnabled = false
//        binding.barChart.setPinchZoom(true)
//        binding.barChart.setDrawBarShadow(false)
//        binding.barChart.setDrawGridBackground(false)
//
//        val xAxis = binding.barChart.xAxis
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setDrawAxisLine(false)
//        xAxis.setDrawGridLines(false) // Hapus garis-garis vertikal di belakang label X
//        xAxis.granularity = 1f
//        xAxis.isGranularityEnabled = true
//        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Jan", "Feb", "Mar", "Apr",
//            "Mei", "Juni", "Juli", "Agust", "Sept", "Okt", "Nov", "Des"))
//        binding.barChart.axisLeft.setDrawGridLines(false)
//        binding.barChart.legend.isEnabled = false
//
//
//
//        // Nonaktifkan gridlines pada axis kiri
//        binding.barChart.axisLeft.setDrawGridLines(false)
//        binding.barChart.axisRight.setDrawGridLines(false)
//        binding.barChart.axisRight.setDrawLabels(false)
//        binding.barChart.axisRight.setDrawAxisLine(false)
//
//        // Tambahkan garis khusus di posisi 0
//        val limitLine = LimitLine(0f, "")
//        limitLine.lineWidth = 1f
//        limitLine.lineColor = Color.GRAY
//        binding.barChart.axisLeft.addLimitLine(limitLine)
//
//
//        //kode ini untuk grilines
////        binding.barChart.axisLeft.setDrawGridLines(false)
//
//        // Nonaktifkan legenda
//        binding.barChart.legend.isEnabled = false
//
//        // Mengatur batas bawah sumbu Y menjadi 0
//        binding.barChart.axisLeft.axisMinimum = 0f
//
//        // Menentukan warna batang yang berdekatan
//        val barColors = listOf(
//            Color.parseColor("#BBDEFB"), // Light Blue 100
//            Color.parseColor("#90CAF9"), // Light Blue 300
//            Color.parseColor("#64B5F6"), // Light Blue 400
//            Color.parseColor("#42A5F5"), // Light Blue 500
//            Color.parseColor("#2196F3"), // Blue 600
//            Color.parseColor("#1E88E5"), // Blue 700
//            Color.parseColor("#1976D2"), // Blue 800
//            Color.parseColor("#1565C0"), // Blue 900
//            Color.parseColor("#0D47A1"), // Blue A700
//            Color.parseColor("#82B1FF"), // Blue A100
//            Color.parseColor("#448AFF"), // Blue A200
//            Color.parseColor("#2979FF")  // Blue A400
//        )
//
//        val barDataSetter: BarDataSet
//        if (binding.barChart.data != null && binding.barChart.data.dataSetCount > 0) {
//            barDataSetter = binding.barChart.data.getDataSetByIndex(0) as BarDataSet
//            barDataSetter.values = profitValues
//            binding.barChart.data.notifyDataChanged()
//        } else {
//            barDataSetter = BarDataSet(profitValues, "Data Set")
//            barDataSetter.colors = barColors
//            barDataSetter.setDrawValues(false)
//
//            val dataSet = ArrayList<IBarDataSet>()
//            dataSet.add(barDataSetter)
//
//            val data = BarData(dataSet)
//            binding.barChart.data = data
//            binding.barChart.setFitBars(true)
//        }
//
//        binding.barChart.invalidate()
//    }


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
//        binding.namaUser.text = user.id.toString()
        // Tambahkan kode untuk menampilkan informasi pengguna lainnya sesuai kebutuhan

        val images = Images()
        val imageUrlAwal = "${images.BASE_URL}${user.foto}"
        Glide.with(requireContext())
            .load(imageUrlAwal) // URL gambar yang diberikan oleh respons API
            .placeholder(R.drawable.ic_launcher_foreground) // Gambar placeholder yang akan ditampilkan saat proses unduhan gambar berlangsung
            .error(R.drawable.clear) // Gambar yang akan ditampilkan jika terjadi kesalahan saat memuat gambar
            .into(binding.fotoUser) // Tampilkan gambar di CircleImageView

    }


    private fun displayUsers(users: List<PostUsers>) {
        // Di sini Anda dapat menampilkan detail pengguna sesuai kebutuhan aplikasi Anda
        // Misalnya, menampilkan informasi pengguna dalam TextView atau komponen UI lainnya
        val usernames = users.mapNotNull { it.username }
        val formattedUsernames = usernames.joinToString(separator = "\n")
        binding.namaUser.text = formattedUsernames
        // Tambahkan kode untuk menampilkan informasi pengguna lainnya sesuai kebutuhan


    }


    private fun saveUserId(userId: Int) {
        sharedPrefManager.putInt(Constant.USER_ID, userId)
    }

    private fun getTokenFromSharedPreferences(): String? {
        // Mengambil token dari SharedPreferences
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }
}


//package com.acuy.sla_maintenance
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.acuy.sla_maintenance.config.Constant
//import com.acuy.sla_maintenance.config.SharedPrafManager
//import com.acuy.sla_maintenance.databinding.ActivityJamKerjaBinding
//import com.acuy.sla_maintenance.databinding.FragmentHalamanAddLaporanBinding
//import com.acuy.sla_maintenance.databinding.FragmentHalamanHomeBinding
//
//
//class HalamanHome : Fragment() {
//    private lateinit var binding: FragmentHalamanHomeBinding
//    lateinit var SharedPrafManager: SharedPrafManager
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentHalamanHomeBinding.inflate(inflater, container, false)
//        val view = binding.root!!
//
//        SharedPrafManager = SharedPrafManager(requireContext())
//        binding.namaUser.text = SharedPrafManager.getString(Constant.PREF_EMAIL)
//
//        return view
//    }
//
//
//}


//
//class HalamanHome : Fragment() {
//    private lateinit var binding: FragmentHalamanHomeBinding
//    private lateinit var apiServices: ApiServices
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentHalamanHomeBinding.inflate(inflater, container, false)
//        val view = binding.root
//
//        apiServices = NetworkConfig().getServices()
//
//        // Panggil metode untuk mengambil data pengguna dari API
//        getUserData()
//
//        return view
//    }
//
//    private fun getUserData() {
//        val token = getTokenFromSharedPreferences() ?: return // Periksa apakah token null
//
//        val tokenInterceptor = Interceptor { chain ->
//            val request = chain.request().newBuilder()
//                .addHeader("Authorization", "Bearer $token")
//                .build()
//            chain.proceed(request)
//        }
//
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(tokenInterceptor)
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl(NetworkConfig().BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//
//        val apiServicesWithToken = retrofit.create(ApiServices::class.java)
//
//        apiServicesWithToken.getUsers().enqueue(object : Callback<Users> {
//            override fun onResponse(call: Call<Users>, response: Response<Users>) {
//                if (response.isSuccessful) {
//                    val userResponse = response.body()
//                    userResponse?.let { users ->
//                        val loggedInUser = users.users?.find { user -> user?.email == getTokenFromSharedPreferences() }
//                        loggedInUser?.let { loggedIn ->
//                            binding.namaUser.text = loggedIn.username
//                        }
//                    }
//                } else {
//                    // Tampilkan pesan kesalahan kepada pengguna
//                    // Misalnya: Toast.makeText(context, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
//                    Log.e("Retrofit onResponse", "Response not successful")
//                }
//            }
//
//            override fun onFailure(call: Call<Users>, t: Throwable) {
//                // Tampilkan pesan kesalahan kepada pengguna
//                // Misalnya: Toast.makeText(context, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
//                Log.e("Retrofit onFailure", "onFailure: ${t.stackTrace}")
//            }
//        })
//    }
//
//    private fun getTokenFromSharedPreferences(): String? {
//        val sharedPrefManager = SharedPrafManager(requireContext())
//        return sharedPrefManager.getString(Constant.USER_TOKEN)
//    }
//}
