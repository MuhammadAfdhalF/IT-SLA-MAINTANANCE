package com.acuy.sla_maintenance

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.FragmentHalamanAddLaporanBinding
import com.acuy.sla_maintenance.model.GetIJenisKategoriResponseItem
import com.acuy.sla_maintenance.model.GetITdanTolResponseItem
import com.acuy.sla_maintenance.model.GetJenisHardwareResponseItem
import com.acuy.sla_maintenance.model.GetJenisSoftwareResponseItem
import com.acuy.sla_maintenance.model.GetLokasiResponseItem
import com.acuy.sla_maintenance.model.PostUsers
import com.acuy.sla_maintenance.model.SubmitReport
import com.bumptech.glide.Glide
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class HalamanAddLaporan : Fragment() {

    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager

    private lateinit var binding: FragmentHalamanAddLaporanBinding
    private lateinit var imageView: ImageView

    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

    private val STORAGE_PERMISSION_CODE = 101

    //foto

    private val PICK_IMAGE_REQUEST = 1
    private val IMAGE_REQUEST_CODE = 100

    private var imagePath: String? = null
    private var selectedImageUri: Uri? = null

    private val REQUEST_STORAGE_PERMISSION = 1002 // You can choose any unique value here

    private val REQUEST_PERMISSION_CODE = 123


    //checkbox //hardware
    private lateinit var checkboxContainer: LinearLayout
    private val selectedHardwareList = mutableListOf<String>()


    //checkbox software
    private lateinit var checkboxContainerSoftware: LinearLayout

    private val selectedSoftwareList = mutableListOf<String>()


    //checkbox it tol
    private lateinit var checkboxContainerITdanTol: LinearLayout
    private val selectedITdanTolList = mutableListOf<String>()

    //kategori
    private var selectedKategoriId: Int? = null

    //lokasi
    private var selectedLokasiId: Int? = null


    //date
    lateinit var inpTanggal: TextView
    lateinit var btnShowDatePicker: ImageButton
    private val calendar = Calendar.getInstance()


    private val lokasiMap = mapOf(
        "Bira Barat" to 1,
        "GTO 1 Bira Barat" to 2,
        "Bira Barat GRD 02" to 3,
        "Bira Barat Plaza" to 4,
        "Bira Barat RTM" to 5,
        "Gardu dan Plaza BRK" to 6,
        "GTO 1 Biringkanaya" to 7,
        "GTO 2 Biringkanaya" to 8,
        "GTO 3 Biringkanaya" to 10,
        "GRD 4 Biringkanaya" to 11,
        "GRD 5 Biringkanaya" to 12,
        "PCS Biringkanaya" to 13,
        "RTM Biringkanaya" to 15,
        "Bira Timur" to 16,
        "GTO 1 Bira Timur" to 17,
        "GTO 02 Bira Timur" to 18,
        "Gardu 03 Bira Timur" to 19,
        "PCS Bira Timur" to 20,
        "RTM Bira Timur" to 21,
        "Cambaya" to 22,
        "GTO 1 Cambaya" to 23,
        "GTO 2 Cambaya" to 24,
        "GRD3 CAMBAYA" to 25,
        "GRD4 CAMBAYA" to 26,
        "GTO 5 Cambaya" to 27
    )

    val kategoriMap = mapOf(
        "kerusakan minor" to 1,
        "kerusakan mayor" to 2,
        "Perbaikan CCTV" to 3,
        "Kerusakan/Pergantian" to 4,
        "Pemasangan Baru" to 5,
        "Penambahan Tinta" to 6,
        "Perawatan Rutin" to 7,
        "Perbaikan" to 8,
        "Remote Desktop/LC" to 9,
        "Install Aplikasi" to 10
    )


    companion object {
        val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHalamanAddLaporanBinding.inflate(inflater, container, false)
        val view = binding.root!!


        checkboxContainer = binding.checkboxContainer
        checkboxContainerSoftware = binding.checkBoxSoftware
        checkboxContainerITdanTol = binding.checkITdanTol

//        binding.boxPC.visibility = View.GONE
//        binding.boxPrinter.visibility = View.GONE
//        binding.boxServer.visibility = View.GONE
//        binding.boxInternet.visibility = View.GONE
//        binding.boxDll.visibility = View.GONE


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


        //get Hardware
        if (authToken != null) {
            // Panggil fungsi untuk mengambil data jenis hardware dari API
            getJenisHardwareFromAPI(authToken)
        } else {
            Log.e("Auth Token", "Token is null")
        }
        binding.inpUraianHardware.isEnabled = false


        //Checkbox Standart Aplikasi

        //get Software
        if (authToken != null) {
            // Panggil fungsi untuk mengambil data jenis hardware dari API
            getJenisSoftwareFromAPI(authToken)
        } else {
            Log.e("Auth Token", "Token is null")
        }
        binding.inpUraianStandartAplikasi.isEnabled = false


        //Checkbox Standart IT dan Peralatan TOl


        if (authToken != null) {
            // Panggil fungsi untuk mengambil data jenis hardware dari API
            getJenisITdanTolFromAPI(authToken)
        } else {
            Log.e("Auth Token", "Token is null")
        }
        binding.inpUraianItTol.isEnabled = false


        if (authToken != null) {
            getJenisKategoriFromAPI(authToken)
        } else {
            Log.e("Token", "Token is null")
            // Handle null token here
        }


        if (authToken != null) {
            getLokasiFromAPI(authToken)
        } else {
            Log.e("Token", "Token is null")
            // Handle null token here
        }


        // List Company
        val itemCompany = listOf("MMN", "MAN")
        val adapterCompany = ArrayAdapter(requireContext(), R.layout.list_company, itemCompany)
        binding.panggilCompany.setAdapter(adapterCompany)


        // List Kategori Activity
        val itemKategoriActivity = listOf("TOLL", "NonToll")
        val adapterKategoriActivity =
            ArrayAdapter(requireContext(), R.layout.list_company, itemKategoriActivity)
        binding.panggilKategorActivity.setAdapter(adapterKategoriActivity)

        //  list Shift
        val itemShift = listOf("1", "2", "3")
        val adapterShift = ArrayAdapter(requireContext(), R.layout.list_shift, itemShift)
        binding.panggilShift.setAdapter(adapterShift)

        //  list lokasi
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

        val adapterLokasi =
            ArrayAdapter(requireContext(), R.layout.list_company, itemLokasi.map { it.first })
        binding.panggilLokasi.setAdapter(adapterLokasi)


        // Saat pengguna memilih lokasi dari daftar
        binding.panggilLokasi.setOnItemClickListener { adapterView, view, position, id ->
            val selectedLocationName = adapterView.getItemAtPosition(position).toString()
            val selectedLocationId = kategoriMap[selectedLocationName]
        }

        // list Kategori
        val itemKategori = listOf(
            "kerusakan minor",
            "kerusakan mayor",
            "Perbaikan CCTV",
            "Kerusakan/Pergantian",
            "Pemasangan Baru",
            "Penambahan Tinta",
            "Perawatan Rutin",
            "Perbaikan",
            "Remote Desktop/LC",
            "Install Aplikasi"
        )

        val adapterKategori = ArrayAdapter(requireContext(), R.layout.list_company, itemKategori)
        binding.panggilKategori.setAdapter(adapterKategori)

        // Saat pengguna memilih kategori dari daftar
        binding.panggilKategori.setOnItemClickListener { adapterView, view, position, id ->
            val selectedCategoryName = adapterView.getItemAtPosition(position).toString()
            val selectedCategoryId = kategoriMap[selectedCategoryName]
        }

        //        btnAdd
        val btnAdd = binding.btnSubmitReport
        btnAdd.setOnClickListener {
            saveData()
        }

        //        foto
        imageView = binding.panggilImg // Menginisialisasi imageView
        val inputPhoto = binding.inpPhoto
        inputPhoto.setOnClickListener {
            // Mulai proses pengunggahan
            openImageChooser()
            Log.i("ImagePath", "ImagePath: $imagePath")
        }
        imagePath = savedInstanceState?.getString("imagePath")

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


    private fun getJenisHardwareFromAPI(token: String) {
        val formattedToken = "Bearer $token"
        apiServices.getJenisHardware(formattedToken)
            .enqueue(object : Callback<List<GetJenisHardwareResponseItem>> {
                override fun onResponse(
                    call: Call<List<GetJenisHardwareResponseItem>>,
                    response: Response<List<GetJenisHardwareResponseItem>>
                ) {
                    if (response.isSuccessful) {
                        val jenisHardwareList = response.body()
                        if (!jenisHardwareList.isNullOrEmpty()) {
                            displayJenisHardware(jenisHardwareList)
                            Log.d(
                                "Retrofit onResponse",
                                "Jenis hardware berhasil diambil dari API"
                            )
                        } else {
                            // Tangani kasus ketika respons body null atau kosong
                            Log.e("Retrofit onResponse", "Response body is null or empty")
                        }
                    } else {
                        Log.e("Retrofit onResponse", "Response not successful: ${response.code()}")
                        // Tangani kasus ketika respons tidak berhasil
                        // Anda dapat menambahkan umpan balik yang sesuai kepada pengguna di sini
                        Toast.makeText(
                            requireContext(),
                            "Terjadi kesalahan (${response.code()}) saat mengambil data jenis hardware. Silakan coba lagi nanti.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<GetJenisHardwareResponseItem>>,
                    t: Throwable
                ) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.message}")
                    // Tangani kasus ketika terjadi kegagalan saat mengambil data dari API
                    // Anda dapat menambahkan umpan balik yang sesuai kepada pengguna di sini
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan saat mengambil data jenis hardware. Silakan coba lagi nanti.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun displayJenisHardware(jenisHardwareList: List<GetJenisHardwareResponseItem>?) {
        // Bersihkan container sebelum menambahkan checkbox baru
        checkboxContainer.removeAllViews()

        if (jenisHardwareList.isNullOrEmpty()) {
            // Tampilkan pesan bahwa tidak ada jenis hardware yang ditemukan
            Toast.makeText(
                requireContext(),
                "Tidak ada jenis hardware yang ditemukan.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        jenisHardwareList.forEach { jenisHardware ->
            val checkBox = CheckBox(requireContext())
            checkBox.text = jenisHardware.namaHardware ?: ""
            checkBox.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                // Ketika status checkbox diubah, tambahkan atau hapus nilai checkbox dari daftar yang dipilih
                if (isChecked) {
                    // Checkbox dicentang
                    Log.d("Checkbox", "${jenisHardware.namaHardware} dicentang")
                    // Tambahkan nilai checkbox ke dalam daftar yang dipilih
                    selectedHardwareList.add(jenisHardware.namaHardware ?: "")
                } else {
                    // Checkbox tidak dicentang
                    Log.d("Checkbox", "${jenisHardware.namaHardware} tidak dicentang")
                    // Hapus nilai checkbox dari daftar yang dipilih
                    selectedHardwareList.remove(jenisHardware.namaHardware ?: "")
                }
                // Aktifkan inputan uraian hardware jika ada checkbox yang dipilih
                binding.inpUraianHardware.isEnabled = selectedHardwareList.isNotEmpty()
                // Output nilai selectedHardwareList ke log
                Log.i("Jenis Hardware", "Selected Hardware : $selectedHardwareList")
            }

            checkboxContainer.addView(checkBox)
        }
    }


    private fun getJenisSoftwareFromAPI(token: String) {
        val formattedToken = "Bearer $token"
        apiServices.getJenisSoftware(formattedToken)
            .enqueue(object : Callback<List<GetJenisSoftwareResponseItem>> {
                override fun onResponse(
                    call: Call<List<GetJenisSoftwareResponseItem>>,
                    response: Response<List<GetJenisSoftwareResponseItem>>
                ) {
                    if (response.isSuccessful) {
                        val jenisSoftwareList = response.body()
                        if (!jenisSoftwareList.isNullOrEmpty()) {
                            displayJenisSoftware(jenisSoftwareList)
                            Log.d(
                                "Retrofit onResponse",
                                "Jenis software berhasil diambil dari API"
                            )
                        } else {
                            // Tangani kasus ketika respons body null atau kosong
                            Log.e("Retrofit onResponse", "Response body is null or empty")
                        }
                    } else {
                        Log.e("Retrofit onResponse", "Response not successful: ${response.code()}")
                        // Tangani kasus ketika respons tidak berhasil
                        // Anda dapat menambahkan umpan balik yang sesuai kepada pengguna di sini
                        Toast.makeText(
                            requireContext(),
                            "Terjadi kesalahan (${response.code()}) saat mengambil data jenis software. Silakan coba lagi nanti.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<GetJenisSoftwareResponseItem>>,
                    t: Throwable
                ) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.message}")
                    // Tangani kasus ketika terjadi kegagalan saat mengambil data dari API
                    // Anda dapat menambahkan umpan balik yang sesuai kepada pengguna di sini
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan saat mengambil data jenis software. Silakan coba lagi nanti.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun displayJenisSoftware(jenisSoftwareList: List<GetJenisSoftwareResponseItem>?) {
        // Bersihkan container sebelum menambahkan checkbox baru
        checkboxContainerSoftware.removeAllViews()

        if (jenisSoftwareList.isNullOrEmpty()) {
            // Tampilkan pesan bahwa tidak ada jenis software yang ditemukan
            Toast.makeText(
                requireContext(),
                "Tidak ada jenis software yang ditemukan.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        jenisSoftwareList.forEach { jenisSoftware ->
            val checkBox = CheckBox(requireContext())
            checkBox.text = jenisSoftware.namaSoftware ?: ""
            checkBox.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                // Ketika status checkbox diubah, tambahkan atau hapus nilai checkbox dari daftar yang dipilih
                if (isChecked) {
                    // Checkbox dicentang
                    Log.d("Checkbox", "${jenisSoftware.namaSoftware} dicentang")
                    // Tambahkan nilai checkbox ke dalam daftar yang dipilih
                    selectedSoftwareList.add(jenisSoftware.namaSoftware ?: "")
                } else {
                    // Checkbox tidak dicentang
                    Log.d("Checkbox", "${jenisSoftware.namaSoftware} tidak dicentang")
                    // Hapus nilai checkbox dari daftar yang dipilih
                    selectedSoftwareList.remove(jenisSoftware.namaSoftware ?: "")
                }
                // Aktifkan inputan uraian software jika ada checkbox yang dipilih
                binding.inpUraianStandartAplikasi.isEnabled = selectedSoftwareList.isNotEmpty()
                // Output nilai selectedSoftwareList ke log
                Log.i("Jenis Software", "Selected Software : $selectedSoftwareList")
            }

            checkboxContainerSoftware.addView(checkBox)
        }
    }


    private fun getJenisITdanTolFromAPI(token: String) {
        val formattedToken = "Bearer $token"
        apiServices.getITdanTol(formattedToken)
            .enqueue(object : Callback<List<GetITdanTolResponseItem>> {
                override fun onResponse(
                    call: Call<List<GetITdanTolResponseItem>>,
                    response: Response<List<GetITdanTolResponseItem>>
                ) {
                    if (response.isSuccessful) {
                        val jenisITdanTolList = response.body()
                        if (!jenisITdanTolList.isNullOrEmpty()) {
                            displayJenisITdanTol(jenisITdanTolList)
                            Log.d(
                                "Retrofit onResponse",
                                "Jenis IT dan Tol berhasil diambil dari API"
                            )
                        } else {
                            // Tangani kasus ketika respons body null atau kosong
                            Log.e("Retrofit onResponse", "Response body is null or empty")
                        }
                    } else {
                        Log.e("Retrofit onResponse", "Response not successful: ${response.code()}")
                        // Tangani kasus ketika respons tidak berhasil
                        // Anda dapat menambahkan umpan balik yang sesuai kepada pengguna di sini
                        Toast.makeText(
                            requireContext(),
                            "Terjadi kesalahan (${response.code()}) saat mengambil data jenis IT dan Tol. Silakan coba lagi nanti.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<GetITdanTolResponseItem>>,
                    t: Throwable
                ) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.message}")
                    // Tangani kasus ketika terjadi kegagalan saat mengambil data dari API
                    // Anda dapat menambahkan umpan balik yang sesuai kepada pengguna di sini
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan saat mengambil data jenis IT dan Tol. Silakan coba lagi nanti.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun displayJenisITdanTol(jenisITdanTolList: List<GetITdanTolResponseItem>?) {
        // Bersihkan container sebelum menambahkan checkbox baru
        checkboxContainerITdanTol.removeAllViews()

        if (jenisITdanTolList.isNullOrEmpty()) {
            // Tampilkan pesan bahwa tidak ada jenis IT dan Tol yang ditemukan
            Toast.makeText(
                requireContext(),
                "Tidak ada jenis IT dan Tol yang ditemukan.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        jenisITdanTolList.forEach { jenisITdanTol ->
            val checkBox = CheckBox(requireContext())
            checkBox.text = jenisITdanTol.namaAplikasiTol ?: ""
            checkBox.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                // Ketika status checkbox diubah, tambahkan atau hapus nilai checkbox dari daftar yang dipilih
                if (isChecked) {
                    // Checkbox dicentang
                    Log.d("Checkbox", "${jenisITdanTol.namaAplikasiTol} dicentang")
                    // Tambahkan nilai checkbox ke dalam daftar yang dipilih
                    selectedITdanTolList.add(jenisITdanTol.namaAplikasiTol ?: "")
                } else {
                    // Checkbox tidak dicentang
                    Log.d("Checkbox", "${jenisITdanTol.namaAplikasiTol} tidak dicentang")
                    // Hapus nilai checkbox dari daftar yang dipilih
                    selectedITdanTolList.remove(jenisITdanTol.namaAplikasiTol ?: "")
                }
                // Aktifkan inputan uraian IT dan Tol jika ada checkbox yang dipilih
                binding.inpUraianItTol.isEnabled = selectedITdanTolList.isNotEmpty()
                // Output nilai selectedITdanTolList ke log
                Log.i("Jenis IT dan Tol", "Selected IT dan Tol : $selectedITdanTolList")
            }

            checkboxContainerITdanTol.addView(checkBox)
        }
    }

    private fun getJenisKategoriFromAPI(token: String) {
        val formattedToken = "Bearer $token"
        apiServices.getJenisKategori(formattedToken)
            .enqueue(object : Callback<List<GetIJenisKategoriResponseItem>> {
                override fun onResponse(
                    call: Call<List<GetIJenisKategoriResponseItem>>,
                    response: Response<List<GetIJenisKategoriResponseItem>>
                ) {
                    if (response.isSuccessful) {
                        val jenisKategoriList = response.body()
                        if (!jenisKategoriList.isNullOrEmpty()) {
                            displayJenisKategori(jenisKategoriList)
                            Log.d(
                                "Retrofit onResponse",
                                "Jenis kategori berhasil diambil dari API"
                            )
                        } else {
                            // Tangani kasus ketika respons body null atau kosong
                            Log.e("Retrofit onResponse", "Response body is null or empty")
                        }
                    } else {
                        Log.e("Retrofit onResponse", "Response not successful: ${response.code()}")
                        // Tangani kasus ketika respons tidak berhasil
                        // Anda dapat menambahkan umpan balik yang sesuai kepada pengguna di sini
                        Toast.makeText(
                            requireContext(),
                            "Terjadi kesalahan (${response.code()}) saat mengambil data jenis kategori. Silakan coba lagi nanti.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<GetIJenisKategoriResponseItem>>,
                    t: Throwable
                ) {
                    Log.e("Retrofit onFailure", "onFailure: ${t.message}")
                    // Tangani kasus ketika terjadi kegagalan saat mengambil data dari API
                    // Anda dapat menambahkan umpan balik yang sesuai kepada pengguna di sini
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan saat mengambil data jenis kategori. Silakan coba lagi nanti.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun createKategoriAdapter(
        context: Context,
        data: List<KategoriItem>
    ): ArrayAdapter<KategoriItem> {
        return object :
            ArrayAdapter<KategoriItem>(context, android.R.layout.simple_spinner_item, data) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_company, parent, false)

                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = data[position].namaKategori

                return view
            }


            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_company, parent, false)

                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = data[position].namaKategori

                return view
            }
        }
    }

    // Data class untuk menyimpan kategori
    data class KategoriItem(val id: Int, val namaKategori: String)

    private fun displayJenisKategori(jenisKategoriList: List<GetIJenisKategoriResponseItem>?) {
        val autoCompleteTextView = binding.panggilKategori

        if (jenisKategoriList.isNullOrEmpty()) {
            // Tampilkan pesan bahwa tidak ada jenis kategori yang ditemukan
            Toast.makeText(
                requireContext(),
                "Tidak ada jenis kategori yang ditemukan.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val kategoriItems =
            jenisKategoriList.map { KategoriItem(it.id ?: 0, it.namaKategori ?: "") }
        val adapter = createKategoriAdapter(requireContext(), kategoriItems)

        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1 // Atur ambang batas pencarian ke 1 karakter

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            // Tangani pemilihan item
            val selectedItem = adapter.getItem(position)
            if (selectedItem != null) {
                selectedKategoriId = selectedItem.id
                val selectedNamaKategori = selectedItem.namaKategori
                Log.i("Jenis Kategori", "Anda memilih kategori: $selectedNamaKategori")
                Log.i("Jenis Kategori", "Anda memilih kategori: $selectedKategoriId")

                // Sekarang Anda dapat menggunakan selectedId untuk mengirim ke server
            } else {
                Log.e("Jenis Kategori", "Item yang dipilih null")
            }
        }
    }


//    // Data class untuk menyimpan kategori
//    data class KategoriItem(val id: Int, val namaKategori: String)
//
//// ...
//
//    private fun displayJenisKategori(jenisKategoriList: List<GetIJenisKategoriResponseItem>?) {
//        val autoCompleteTextView = binding.panggilKategori
//
//        if (jenisKategoriList.isNullOrEmpty()) {
//            // Tampilkan pesan bahwa tidak ada jenis kategori yang ditemukan
//            Toast.makeText(
//                requireContext(),
//                "Tidak ada jenis kategori yang ditemukan.",
//                Toast.LENGTH_SHORT
//            ).show()
//            return
//        }
//
//        val adapter = ArrayAdapter<KategoriItem>(
//            requireContext(),
//            R.layout.list_company // Menggunakan layout kustom Anda sendiri untuk item dropdown
//        )
//
//        // Tambahkan objek KategoriItem ke dalam adapter
//        jenisKategoriList.forEach { jenisKategori ->
//            val id = jenisKategori.id ?: 0 // default value jika id null
//            val namaKategori =
//                jenisKategori.namaKategori ?: "" // default value jika namaKategori null
//            val kategoriItem = KategoriItem(id, namaKategori)
//            adapter.add(kategoriItem)
//        }
//
//        autoCompleteTextView.setAdapter(adapter)
//        autoCompleteTextView.threshold = 1 // Atur ambang batas pencarian ke 1 karakter
//
//        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
//            // Tangani pemilihan item
//            val selectedItem = adapter.getItem(position)
//            if (selectedItem != null) {
//                selectedKategoriId = selectedItem.id
//                val selectedNamaKategori = selectedItem.namaKategori
//                Log.i("Jenis Kategori", "Anda memilih kategori: $selectedNamaKategori")
//                Log.i("Jenis Kategori", "Anda memilih kategori: $selectedKategoriId")
//
//
//                // Sekarang Anda dapat menggunakan selectedId untuk mengirim ke server
//            } else {
//                Log.e("Jenis Kategori", "Item yang dipilih null")
//            }
//        }
//    }
//


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

                            Log.d(
                                "API Response",
                                "Nama Lokasi: ${lokasiList.map { it.namaLokasi }}"
                            )

                            displayLokasi(lokasiList)
                            Log.d(
                                "Retrofit onResponse",
                                "Data lokasi berhasil diambil dari API"
                            )
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

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
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
        val autoCompleteTextView = binding.panggilLokasi

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


    private fun clearFormData() {
        binding.panggilCompany.text?.clear()
//        binding.inpTanggal.text = ""
        binding.panggilUraianHardware.text?.clear()
        binding.panggilUraianStandartAplikasi.text?.clear()
        binding.panggilUraianItdanTol.text?.clear()
        binding.panggilCatatan.text?.clear()
        binding.panggilShift.text?.clear()
        binding.panggilLokasi.text?.clear()
        binding.panggilKategori.text?.clear()
//        binding.panggilBiaya.text?.clear()

        imageView.setImageResource(R.drawable.img_add)


//        imageView.setImageURI(null)

        // Bersihkan status checkbox sesuai kebutuhan
        binding.boxPC.isChecked = false
        binding.boxPrinter.isChecked = false
        binding.boxServer.isChecked = false
        binding.boxInternet.isChecked = false
        binding.boxDll.isChecked = false
        binding.boxSistemOperasi.isChecked = false
        binding.boxMicrosoft.isChecked = false
        binding.boxProgramPCS.isChecked = false
        binding.boxLTCS.isChecked = false
        binding.boxRTM.isChecked = false
        binding.boxProgramCCTV.isChecked = false

        // Membersihkan status checkbox
        checkboxContainer.forEach { view ->
            if (view is CheckBox) {
                view.isChecked = false
            }
        }
        // Membersihkan status checkbox
        checkboxContainerSoftware.forEach { view ->
            if (view is CheckBox) {
                view.isChecked = false
            }
        }
        // Membersihkan status checkbox
        checkboxContainerITdanTol.forEach { view ->
            if (view is CheckBox) {
                view.isChecked = false
            }
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
                        val contentResolver = requireContext().contentResolver
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

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Izin disetujui, buka galeri
                openImageChooser()
                Log.i("Izin disetujui", "Izin DIsetujui")
            } else {
                // Izin ditolak, tampilkan pesan
                AlertDialog.Builder(requireContext())
                    .setTitle("Izin Diperlukan")
                    .setMessage("Untuk mengakses galeri dan memilih gambar, aplikasi memerlukan izin untuk mengakses penyimpanan.")
                    .setPositiveButton("Buka Pengaturan") { dialog, _ ->
                        dialog.dismiss()
                        openAppSettings()
                    }
                    .create()
                    .show()
                Log.e("Izin BELUM diberikan", "Anda bELUM KASIH AKSES")
            }
        }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika izin belum diberikan, minta izin secara dinamis
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            // Izin sudah diberikan, log pesan saja
            Log.i("Izin sudah diberikan", "Anda dapat melanjutkan operasi membaca file di sini")
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    private fun saveData() {
        val authToken = getTokenFromSharedPreferences()

        if (authToken != null) {
            // Mendapatkan nilai dari inputan pengguna
            val company = binding.panggilCompany.text.toString().toLowerCase()

            val kategoriActivity =
                binding.panggilKategorActivity.text.toString().toLowerCase().replace(" ", "")

            // tanggal
//            val tanggal = binding.inpTanggal.text.toString()

            // Mengambil nilai dari selectedHardwareList
            val selectedHardwareListValues = selectedHardwareList.joinToString(", ")
            Log.i("Jenis Hardware", "Selected Hardware : $selectedHardwareListValues")


            val selectedSoftwareListValues = selectedSoftwareList.joinToString(", ")
            Log.i("Jenis Software", "Selected Software : $selectedSoftwareListValues")

            val selectedITdanTolListValues = selectedITdanTolList.joinToString(", ")
            Log.i("Jenis IT TOL", "Selected IT TOL : $selectedITdanTolListValues")


            val selectedIdKategori = selectedKategoriId
            if (selectedIdKategori != null) {
                // Lakukan sesuatu dengan selectedId, seperti mengirimnya ke server
                Log.i("Jenis Kategori id", "  kategori ID: $selectedIdKategori")
            } else {
                Log.e("Jenis Kategori id", "Tidak ada kategori yang dipilih")
            }
            val selectedLokasiId = selectedLokasiId
            Log.i("Jenis Lokasi id", "  Lokasi ID: $selectedLokasiId")


            //jenis hardware dan uraian hardware
            val jenisHardware = when {
                binding.boxPC.isChecked -> binding.boxPC.text.toString()
                binding.boxPrinter.isChecked -> binding.boxPrinter.text.toString()
                binding.boxServer.isChecked -> binding.boxServer.text.toString()
                binding.boxInternet.isChecked -> binding.boxInternet.text.toString()
                else -> binding.boxDll.text.toString()
            }
            val uraianJenisHardware = binding.panggilUraianHardware.text.toString()

            //standart aplikasi dan uraian standar aplikasi
            val standartAplikasi = if (binding.boxSistemOperasi.isChecked) {
                binding.boxSistemOperasi.text.toString()
            } else {
                binding.boxMicrosoft.text.toString()
            }
            val uraianStandartAplikasi = binding.panggilUraianStandartAplikasi.text.toString()

//        Aplikasi ItTol dan Uraian
            val aplikasiItTol = when {
                binding.boxProgramPCS.isChecked -> binding.boxProgramPCS.text.toString()
                binding.boxLTCS.isChecked -> binding.boxLTCS.text.toString()
                binding.boxRTM.isChecked -> binding.boxRTM.text.toString()
                else -> binding.boxProgramCCTV.text.toString()
            }
            val uraianAplikasiItTol = binding.panggilUraianItdanTol.text.toString()

            //catatan dan shift
            val catatan = binding.panggilCatatan.text.toString()

            val shift = binding.panggilShift.text.toString()

//            //lokasi
//            val lokasi = binding.panggilLokasi.text.toString()
//            val selectedLocationId = lokasiMap[lokasi]

//            //kategori
//            val kategori = binding.panggilKategori.text.toString()
//            val selectedKategoriId = kategoriMap[kategori]
//            Log.i("selectedKategoriId", "Selected IT TOL : $selectedKategoriId")

            //kondisi akhir dan biaya
//            val biaya = binding.panggilBiaya.text.toString()

            //foto
//        val foto = binding.inpPhoto.toString()

            //foto
//        val foto = imagePath ?: selectedImageUri.toString()
            val foto = imagePath ?: ""


//            val retrofit = NetworkConfig().getServices()
//            val authToken = getTokenFromSharedPreferences()
            // Membuat instance Retrofit
            // Buat interceptor untuk menyertakan token dalam header permintaan
            // Buat interceptor untuk menyertakan token dalam header permintaan
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

            // Buat retrofit baru dengan OkHttpClient yang telah dikonfigurasi
            val retrofit = Retrofit.Builder()
                .baseUrl(NetworkConfig().BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            // Buat instance layanan API dengan retrofit yang telah dikonfigurasi
            val apiServicesWithToken = retrofit.create(ApiServices::class.java)



            if (company.isNotEmpty() && shift.isNotEmpty() &&
                selectedLokasiId != null && selectedIdKategori != null && imagePath != null && authToken != null
            ) {

                // Membuat header Accept & Authorization
//                val headers = HashMap<String, String>()
//                headers["Accept"] = "application/json"
//                headers["Authorization"] = "Bearer $authToken"

                val companyRequestBody = company.toRequestBody("text/plain".toMediaType())
//                val tanggalRequestBody = tanggal.toRequestBody("text/plain".toMediaType())
//                val jenisHardwareRequestBody =
//                    jenisHardware.toRequestBody("text/plain".toMediaType())
                // Buat payload untuk dikirim ke server

                val kategoriActivityRequestBody =
                    kategoriActivity.toRequestBody("text/plain".toMediaType())

//                val selectedHardwareRequestBody =
//                    selectedHardwareListValues.toRequestBody("text/plain".toMediaType())
//                val uraianJenisHardwareRequestBody =
//                    uraianJenisHardware.toRequestBody("text/plain".toMediaType())

                val jenisHardwareRequestBody = if (selectedHardwareListValues.isNotEmpty()) {
                    selectedHardwareListValues.toRequestBody("text/plain".toMediaType())
                } else {
                    null
                }
                val uraianJenisHardwareRequestBody = if (uraianJenisHardware.isNotEmpty()) {
                    uraianJenisHardware.toRequestBody("text/plain".toMediaType())
                } else {
                    null
                }

//                val standartAplikasiRequestBody =
//                    selectedSoftwareListValues.toRequestBody("text/plain".toMediaType())
                val standartAplikasiRequestBody = if (selectedSoftwareListValues.isNotEmpty()) {
                    selectedSoftwareListValues.toRequestBody("text/plain".toMediaType())
                } else {
                    null
                }
//                val uraianStandartAplikasiRequestBody =
//                    uraianStandartAplikasi.toRequestBody("text/plain".toMediaType())
                val uraianStandartAplikasiRequestBody = if (uraianStandartAplikasi.isNotEmpty()) {
                    uraianStandartAplikasi.toRequestBody("text/plain".toMediaType())
                } else {
                    null
                }

//                val aplikasiItTolRequestBody =
//                    selectedITdanTolListValues.toRequestBody("text/plain".toMediaType())
                val aplikasiItTolRequestBody = if (selectedITdanTolListValues.isNotEmpty()) {
                    selectedITdanTolListValues.toRequestBody("text/plain".toMediaType())
                } else {
                    null
                }
//                val uraianAplikasiItTolRequestBody =
//                    uraianAplikasiItTol.toRequestBody("text/plain".toMediaType())
                val uraianAplikasiItTolRequestBody = if (uraianAplikasiItTol.isNotEmpty()) {
                    uraianAplikasiItTol.toRequestBody("text/plain".toMediaType())
                } else {
                    null
                }
//                val catatanRequestBody = catatan.toRequestBody("text/plain".toMediaType())
                val catatanRequestBody = if (catatan.isNotEmpty()) {
                    catatan.toRequestBody("text/plain".toMediaType())
                } else {
                    null
                }


                val shiftRequestBody = shift.toRequestBody("text/plain".toMediaType())
                val lokasiIdRequestBody =
                    selectedLokasiId.toString().toRequestBody("text/plain".toMediaType())
                val kategoriIdRequestBody =
                    selectedIdKategori.toString().toRequestBody("text/plain".toMediaType())
//                val biayaRequestBody = biaya.toRequestBody("text/plain".toMediaType())
//                val processRequestBody = "process".toRequestBody("text/plain".toMediaType())

                val currentImagePath = imagePath ?: ""
                val fotoFile: File? = if (currentImagePath.isNotEmpty()) {
                    File(currentImagePath)
                } else if (selectedImageUri != null) {
                    File(selectedImageUri?.path ?: "")
                } else {
                    null
                }


                val fotoRequestBody: RequestBody?
                val fotoPart: MultipartBody.Part?

                if (fotoFile != null && fotoFile.exists()) {
                    fotoRequestBody = fotoFile.asRequestBody("image/*".toMediaType())
                    fotoPart = MultipartBody.Part.createFormData(
                        "foto_awal",
                        fotoFile.name,
                        fotoRequestBody
                    )
                } else {
                    // Penanganan jika foto tidak tersedia
                    fotoRequestBody = null
                    fotoPart = null
                    // Menampilkan pesan kesalahan kepada pengguna
                    Toast.makeText(requireContext(), "Foto tidak tersedia", Toast.LENGTH_SHORT)
                        .show()
                }


                if (fotoRequestBody != null && fotoPart != null) {


                    // Mendapatkan instance Retrofit
                    apiServicesWithToken.postToll(
                        companyRequestBody,
                        kategoriActivityRequestBody,
//                        tanggalRequestBody,
                        jenisHardwareRequestBody,
                        uraianJenisHardwareRequestBody,
                        standartAplikasiRequestBody,
                        uraianStandartAplikasiRequestBody,
                        aplikasiItTolRequestBody,
                        uraianAplikasiItTolRequestBody,
                        catatanRequestBody,
                        shiftRequestBody,
                        lokasiIdRequestBody,
                        kategoriIdRequestBody,
//                        biayaRequestBody,
                        fotoPart,

                    ).enqueue(object : Callback<SubmitReport> {
                        override fun onResponse(
                            call: Call<SubmitReport>,
                            response: Response<SubmitReport>
                        ) {
                            if (response.isSuccessful) {
                                val hasil = response.body()
                                hasil?.let {
                                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                                        .show()
                                } ?: run {
                                    Toast.makeText(
                                        requireContext(),
                                        "Data berhasil disimpan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                clearFormData()

                                // Log post success
                                Log.i("Post Success", "post success boss")

                                Log.i("ImagePath", "ImagePath: $imagePath")
                                Log.i("Company", "Company : $company")

                                Log.i(
                                    "Jenis Kategori Activity",
                                    "Selected Kategori Activity : $kategoriActivity"
                                )

//                                Log.i("Tanggal", "Tanggal : $tanggal")
                                Log.i(
                                    "Jenis Hardware",
                                    "Jenis Hardware : $selectedHardwareListValues"
                                )
                                Log.i(
                                    "Uraian Jenis Hardware",
                                    "Uraian Jenis Hardware : $uraianJenisHardware"
                                )
                                Log.i(
                                    "Standart Aplikasi",
                                    "Standart Aplikasi : $selectedSoftwareListValues"
                                )
                                Log.i(
                                    "Uraian Standart Aplikasi",
                                    "Uraian Standart Aplikasi : $uraianStandartAplikasi"
                                )
                                Log.i(
                                    "Aplikasi IT Tol",
                                    "Aplikasi IT Tol : $selectedITdanTolListValues"
                                )
                                Log.i(
                                    "Uraian Aplikasi IT Tol",
                                    "Uraian Aplikasi IT Tol : $uraianAplikasiItTol"
                                )
                                Log.i("Catatan", "Catatan : $catatan")
                                Log.i("Shift", "Shift : $shift")
                                Log.i(
                                    "Selected Location ID",
                                    "Selected Location ID : $selectedLokasiId"
                                )
                                Log.i(
                                    "Selected Kategori ID",
                                    "Selected Kategori ID : $selectedIdKategori"
                                )
//                                Log.i("Biaya", "Biaya : $biaya")

                            } else {
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = errorBody ?: "Unknown error"
                                Log.e("API Error", "Response not successful: $errorMessage")
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal menyimpan data: $errorMessage",
                                    Toast.LENGTH_SHORT
                                ).show()


                                Log.i("ImagePath", "ImagePath: $imagePath")
                                Log.i("Company", "Company : $company")

                                Log.i(
                                    "Jenis Kategori Activity",
                                    "Selected Kategori Activity : $kategoriActivity"
                                )

//                                Log.i("Tanggal", "Tanggal : $tanggal")
                                Log.i(
                                    "Jenis Hardware",
                                    "Jenis Hardware : $selectedHardwareListValues"
                                )
                                Log.i(
                                    "Uraian Jenis Hardware",
                                    "Uraian Jenis Hardware : $uraianJenisHardware"
                                )
                                Log.i(
                                    "Standart Aplikasi",
                                    "Standart Aplikasi : $selectedSoftwareListValues"
                                )
                                Log.i(
                                    "Uraian Standart Aplikasi",
                                    "Uraian Standart Aplikasi : $uraianStandartAplikasi"
                                )
                                Log.i(
                                    "Aplikasi IT Tol",
                                    "Aplikasi IT Tol : $selectedITdanTolListValues"
                                )
                                Log.i(
                                    "Uraian Aplikasi IT Tol",
                                    "Uraian Aplikasi IT Tol : $uraianAplikasiItTol"
                                )
                                Log.i("Catatan", "Catatan : $catatan")
                                Log.i("Shift", "Shift : $shift")
                                Log.i(
                                    "Selected Location ID",
                                    "Selected Location ID : $selectedLokasiId"
                                )
                                Log.i(
                                    "Selected Kategori ID",
                                    "Selected Kategori ID : $selectedIdKategori"
                                )
//                                Log.i("Biaya", "Biaya : $biaya")
                            }

                        }

                        override fun onFailure(call: Call<SubmitReport>, t: Throwable) {
                            Log.e("API Error", "Gagal menyimpan data: ${t.message}", t)
                            Toast.makeText(
                                requireContext(),
                                "Data Gagal di Simpan: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Log.e("ImagePath", "ImagePath is null or empty")
                    Log.e("Upload Foto", "Foto tidak tersedia")
                    Toast.makeText(requireContext(), "Foto tidak tersedia", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Lengkapi semua data terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
            }
//
        } else {
            Log.e("Token", "Token is null")
            // Handle null token here
        }

    }

}


//    private fun openImageChooser() {
//        Intent(Intent.ACTION_PICK).also {
//            it.type = "image/*"
//            val mimTypes = arrayOf("image/jpeg", "image/png")
//            it.putExtra(Intent.EXTRA_MIME_TYPES, mimTypes)
//            startActivityForResult(it, REQUEST_CODE_IMAGE)
//
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                REQUEST_CODE_IMAGE -> {
//                    selectedImageUri = data?.data
//                    imageView.setImageURI(selectedImageUri)
//                }
//            }
//        }
//    }
//


//                val fotoRequestBody: RequestBody?
//                val fotoPart: MultipartBody.Part?
//
//                if (fotoFile != null && fotoFile.exists()) {
//                    fotoRequestBody = fotoFile.asRequestBody("image/*".toMediaType())
//                    fotoPart =
//                        MultipartBody.Part.createFormData("photo", fotoFile.name, fotoRequestBody)
//                } else {
//                    // Penanganan jika foto tidak tersedia
//                    fotoRequestBody = null
//                    fotoPart = null
//                    // Anda mungkin ingin menampilkan pesan kesalahan atau mengambil tindakan lain sesuai kebutuhan aplikasi Anda
//                }
//
//    companion object {
//        const val REQUEST_CODE_IMAGE = 1000
//    }
//
//    private fun uploadImage() {
//        if (selectedImageUri == null) {
//            // Memanggil metode snackbar dari fragment saat ini
//            snackbar("Select an Image First")
//            return
//        }
//
//        val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(
//            selectedImageUri!!, "r", null
//        ) ?: return
//
//        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
//        val file = File(
//            requireContext().cacheDir,
//            requireContext().contentResolver.getFileName(selectedImageUri!!)
//        )
//        val outputStream = FileOutputStream(file)
//        inputStream.copyTo(outputStream)
////        progress_bar.progress = 0
//        val body = UploadRequestBody(file, "image", this)
//
//
//    }
//
//    // Menggunakan extension function pada Fragment untuk menampilkan snackbar
//    private fun Fragment.snackbar(message: String) {
//        view?.let { rootView ->
//            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).also { snackbar ->
//                snackbar.setAction("OK") {
//                    snackbar.dismiss()
//                }
//            }.show()
//        }
//    }


//    override fun onProgressUpdate(percentage: Int) {
//        TODO("Not yet implemented")
//    }


//    private fun pickImageGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, PICK_IMAGE_REQUEST)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            val selectedImageUri: Uri? = data?.data
//            selectedImageUri?.let { uri ->
//                try {
//                    // Dapatkan path lengkap file dari URI
//                    val imagePath = getRealPathFromUri(uri)
//
//                    // Tampilkan gambar di ImageView
//                    imageView.setImageURI(uri)
//
//                    // Simpan path lengkap file yang dipilih
//                    this.imagePath = imagePath  // Perbarui imagePath di sini
//
//                    // Cek nilai imagePath dan selectedImageUri
//                    Log.d("ImageFilePath", "ImageFilePath: $imagePath")
//                    Log.d("SelectedImageUri", "SelectedImageUri: $selectedImageUri")
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            val selectedImageUri: Uri? = data?.data
//            selectedImageUri?.let { uri ->
//                try {
//                    // Dapatkan path lengkap file dari URI
//                    val imagePath = getRealPathFromUri(uri)
//
//                    // Tampilkan gambar di ImageView
//                    imageView.setImageURI(uri)
//
//                    // Dapatkan nama file dari URI
//                    val fileName = getFileNameFromUri(uri)
//                    Log.d("FileName", "FileName: $fileName")
//
//                    // Simpan path lengkap file yang dipilih
//                    this.imagePath = imagePath  // Perbarui imagePath di sini
//
//                    // Cek nilai imagePath dan selectedImageUri
//                    Log.d("ImageFilePath", "ImageFilePath: $imagePath")
//                    Log.d("SelectedImageUri", "SelectedImageUri: $selectedImageUri")
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
//
//
//    private fun getRealPathFromUri(uri: Uri): String? {
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
//        cursor?.use {
//            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            it.moveToFirst()
//            return it.getString(columnIndex)
//        }
//        return null
//    }
//
//
//    private fun getFileNameFromUri(uri: Uri): String? {
//        val cursor: Cursor? = requireContext().contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val displayNameIndex: Int = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (displayNameIndex != -1) {
//                    val displayName: String = it.getString(displayNameIndex)
//                    return displayName
//                }
//            }
//        }
//        return null
//    }
//
//
//    private val requestPermLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                pickImageGallery()
//            } else {
//                showPermissionDeniedDialog()
//            }
//        }
//
//    private fun checkStoragePermission() {
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissionWithExplanation()
//        } else {
//            pickImageGallery()
//        }
//    }
//
//    private fun requestPermissionWithExplanation() {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Izin Diperlukan")
//            .setMessage("Aplikasi memerlukan izin akses penyimpanan untuk mengambil gambar. " +
//                    "Apakah Anda ingin memberikan izin tersebut?")
//            .setPositiveButton("Saya Memberi Akses") { _, _ ->
//                requestPermLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
//            .setNegativeButton("Saya Menolak") { dialog, _ ->
//                dialog.dismiss()
//                showMessage("Anda menolak memberikan izin.")
//            }
//            .show()
//    }
//
//    private fun showPermissionDeniedDialog() {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Izin Diperlukan")
//            .setMessage("Aplikasi memerlukan izin akses penyimpanan untuk mengambil gambar. " +
//                    "Silakan berikan izin tersebut di pengaturan aplikasi.")
//            .setPositiveButton("Pengaturan") { _, _ ->
//                openAppSettings()
//            }
//            .setNegativeButton("Batal") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
//
//    private fun openAppSettings() {
//        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//        val uri = Uri.fromParts("package", requireActivity().packageName, null)
//        intent.data = uri
//        startActivity(intent)
//    }
//
//    private fun showMessage(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//    }


//private fun ContentResolver.getFileName(selectedImageUri: Uri): String {
//    var name = ""
//    val returnCursor = this.query(selectedImageUri, null, null, null, null)
//    if (returnCursor != null) {
//        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//        returnCursor.moveToFirst()
//        name = returnCursor.getString(nameIndex)
//        returnCursor.close()
//    }
//    return name


//    private fun saveData() {
//        val authToken = getTokenFromSharedPreferences()
//
//        if (authToken != null) {
//            val company = binding.panggilCompany.text.toString()
//            val tanggal = binding.inpTanggal.text.toString()
//            val jenisHardware = when {
//                binding.boxPC.isChecked -> binding.boxPC.text.toString()
//                binding.boxPrinter.isChecked -> binding.boxPrinter.text.toString()
//                binding.boxServer.isChecked -> binding.boxServer.text.toString()
//                binding.boxInternet.isChecked -> binding.boxInternet.text.toString()
//                else -> binding.boxDll.text.toString()
//            }
//            val uraianJenisHardware = binding.panggilUraianHardware.text.toString()
//            val standartAplikasi = if (binding.boxSistemOperasi.isChecked) {
//                binding.boxSistemOperasi.text.toString()
//            } else {
//                binding.boxMicrosoft.text.toString()
//            }
//            val uraianStandartAplikasi = binding.panggilUraianStandartAplikasi.text.toString()
//            val aplikasiItTol = when {
//                binding.boxProgramPCS.isChecked -> binding.boxProgramPCS.text.toString()
//                binding.boxLTCS.isChecked -> binding.boxLTCS.text.toString()
//                binding.boxRTM.isChecked -> binding.boxRTM.text.toString()
//                else -> binding.boxProgramCCTV.text.toString()
//            }
//            val uraianAplikasiItTol = binding.panggilUraianItdanTol.text.toString()
//            val catatan = binding.panggilCatatan.text.toString()
//            val shift = binding.panggilShift.text.toString()
//            val lokasi = binding.panggilLokasi.text.toString()
//            val selectedLocationId = lokasiMap[lokasi]
//            val kategori = binding.panggilKategori.text.toString()
//            val selectedKategoriId = kategoriMap[kategori]
//            val biaya = binding.panggilBiaya.text.toString()
//            val foto = imagePath ?: ""
//
//            val tokenInterceptor = Interceptor { chain ->
//                val request = chain.request().newBuilder()
//                    .addHeader("Authorization", "Bearer $authToken")
//                    .build()
//                chain.proceed(request)
//            }
//
//            val okHttpClient = OkHttpClient.Builder()
//                .addInterceptor(tokenInterceptor)
//                .build()
//
//            val retrofit = Retrofit.Builder()
//                .baseUrl(NetworkConfig().BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
//                .build()
//
//            val apiServicesWithToken = retrofit.create(ApiServices::class.java)
//
//            if (company.isNotEmpty() && tanggal.isNotEmpty() && jenisHardware.isNotEmpty() && uraianJenisHardware.isNotEmpty() &&
//                standartAplikasi.isNotEmpty() && uraianStandartAplikasi.isNotEmpty() &&
//                aplikasiItTol.isNotEmpty() && uraianAplikasiItTol.isNotEmpty() &&
//                catatan.isNotEmpty() && shift.isNotEmpty() &&
//                selectedLocationId != null && selectedKategoriId != null && biaya.isNotEmpty() && authToken != null
//            ) {
//                val companyRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), company)
//                val tanggalRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tanggal)
//                val jenisHardwareRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), jenisHardware)
//                val uraianJenisHardwareRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), uraianJenisHardware)
//                val standartAplikasiRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), standartAplikasi)
//                val uraianStandartAplikasiRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), uraianStandartAplikasi)
//                val aplikasiItTolRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), aplikasiItTol)
//                val uraianAplikasiItTolRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), uraianAplikasiItTol)
//                val catatanRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), catatan)
//                val shiftRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), shift)
//                val lokasiIdRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedLocationId.toString())
//                val kategoriIdRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedKategoriId.toString())
//                val biayaRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), biaya)
//                val fotoFile: File = File(foto)
//                val fotoRequestBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), fotoFile)
//                val fotoAwalPart: MultipartBody.Part = MultipartBody.Part.createFormData("foto_awal", fotoFile.name, fotoRequestBody)
//
//
//                apiServicesWithToken.postToll(
//                    companyRequestBody,
//                    tanggalRequestBody,
//                    jenisHardwareRequestBody,
//                    uraianJenisHardwareRequestBody,
//                    standartAplikasiRequestBody,
//                    uraianStandartAplikasiRequestBody,
//                    aplikasiItTolRequestBody,
//                    uraianAplikasiItTolRequestBody,
//                    catatanRequestBody,
//                    shiftRequestBody,
//                    lokasiIdRequestBody,
//                    kategoriIdRequestBody,
//                    biayaRequestBody,
//                    fotoAwalPart,
//                    RequestBody.create("text/plain".toMediaTypeOrNull(), "process")
//                ).enqueue(object : Callback<SubmitReport> {
//                    override fun onResponse(call: Call<SubmitReport>, response: Response<SubmitReport>) {
//                        if (response.isSuccessful) {
//                            val hasil = response.body()
//                            hasil?.let {
//                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//                            } ?: run {
//                                Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
//                            }
//                        } else {
//                            val errorBody = response.errorBody()?.string()
//                            val errorMessage = errorBody ?: "Unknown error"
//                            Log.e("API Error", "Response not successful: $errorMessage")
//                            Toast.makeText(requireContext(), "Gagal menyimpan data: $errorMessage", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<SubmitReport>, t: Throwable) {
//                        Log.e("API Error", "Gagal menyimpan data: ${t.message}", t)
//                        Toast.makeText(requireContext(), "Data Gagal di Simpan: ${t.message}", Toast.LENGTH_SHORT).show()
//                    }
//                })
//            } else {
//                Toast.makeText(requireContext(), "Lengkapi semua data terlebih dahulu", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Log.e("Token", "Token is null")
//            // Handle null token here
//        }
//    }


//
//private fun pickImageGallery() {
//    val intent = Intent(Intent.ACTION_PICK)
//    intent.type = "image/*"
//    startActivityForResult(intent, IMAGE_REQUEST_CODE)
//}
//
//override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//    super.onActivityResult(requestCode, resultCode, data)
//    if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//        val selectedImageUri: Uri? = data?.data
//        selectedImageUri?.let { uri ->
//            try {
//                val inputStream = requireContext().contentResolver.openInputStream(uri)
//                inputStream?.let {
//                    val imageFile = File(requireContext().cacheDir, "selected_image")
//                    val outputStream = FileOutputStream(imageFile)
//                    inputStream.copyTo(outputStream)
//                    imagePath = imageFile.absolutePath
//                    imageView.setImageURI(uri)
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }
//}
//}


//    private fun saveData() {
//
//        //  Company
//        val company = binding.panggilCompany.text.toString()
//
//        //date
//
//        //jenis hardware
//        val pcLaptop = binding.boxPC
//        val printerPeriferal = binding.boxPrinter
//        val server = binding.boxServer
//        val internetJaringan = binding.boxInternet
//        val dll = binding.boxDll
//
//        val jenisHardware = if (pcLaptop.isChecked) {
//            pcLaptop.text.toString()
//        } else if (printerPeriferal.isChecked) {
//            printerPeriferal.text.toString()
//        } else if (server.isChecked) {
//            server.text.toString()
//        } else if (internetJaringan.isChecked) {
//            internetJaringan.text.toString()
//        } else {
//            dll.text.toString()
//        }
//
//        //  uraian Jenis Hardware
//        val uraianJenisHardware = binding.panggilUraianHardware.text.toString()
//
//
//        //  Standart Aplikasi
//        val sistemOperasi = binding.boxSistemOperasi
//        val microsofOffice = binding.boxMicrosoft
//
//        val standartAplikasi = if (sistemOperasi.isChecked) {
//            sistemOperasi.text.toString()
//        } else {
//            microsofOffice.text.toString()
//        }
//
//        //  uraian Standart Aplikasi
//        val uraianStandartAplikasi = binding.panggilUraianStandartAplikasi.text.toString()
//
//        //Aplikasi IT dan Peralatan Tol
//        val programPcs = binding.boxProgramPCS
//        val programLtcsTfi = binding.boxLTCS
//        val programRtm = binding.boxRTM
//        val programCctvVms = binding.boxProgramCCTV
//
//
//        val aplikasiItTol = if (programPcs.isChecked) {
//            programPcs.text.toString()
//        } else if (programLtcsTfi.isChecked) {
//            programLtcsTfi.text.toString()
//        } else if (programRtm.isChecked) {
//            programRtm.text.toString()
//        } else {
//            programCctvVms.text.toString()
//        }
//
//        // uraian Aplikasi IT TOL
//        val uraianAplikasiItTol = binding.panggilUraianItdanTol.text.toString()
//
//
//        // catatan
//        val catatan = binding.panggilCatatan.text.toString()
//
//        //Shift
//        val shift = binding.panggilShift.text.toString()
//
//        // lokasi
////        val lokasi = binding.panggilLokasi.text.toString()
//        val lokasi = binding.panggilLokasi.text.toString()
//        val selectedLocationId = lokasiMap[lokasi]
//
//        //kategori
//        val kategori = binding.panggilKategori.text.toString()
//        val selectedKategoriId = kategoriMap[kategori]
//
//
//        // Kondisi Akhir
//        val kondisiAkhir = binding.panggilKondisiAkhir.text.toString()
//
//
//        // Biaya
//        val biaya = binding.panggilBiaya.text
//
//        //photo
//        val foto = binding.panggilImg.toString()
//
//
//        val retrofit = NetworkConfig().getServices()
//        if (company.isNotEmpty() || jenisHardware.isNotEmpty() || uraianJenisHardware.isNotEmpty() || standartAplikasi.isNotEmpty() || uraianStandartAplikasi.isNotEmpty() ||
//            aplikasiItTol.isNotEmpty() || uraianAplikasiItTol.isNotEmpty() || catatan.isNotEmpty() || shift.isNotEmpty() || selectedLocationId.isNotEmpty()
//            || selectedKategoriId.isNotEmpty() || kondisiAkhir.isNotEmpty() || biaya.isNotEmpty() || foto.isNotEmpty()
//        ) {
//            retrofit.postToll(
//                company,
//                jenisHardware,
//                uraianJenisHardware,
//                standartAplikasi,
//                uraianStandartAplikasi,
//                aplikasiItTol,
//                uraianAplikasiItTol,
//                catatan,
//                shift,
//                selectedLocationId,
//                selectedKategoriId,
//                kondisiAkhir,
//                biaya,
//                foto
//            ).enqueue(object :
//                Callback<SubmitReport>)
//        }
//    }


//class HalamanAddLaporan : Fragment() {
//
//    private lateinit var binding: FragmentHalamanAddLaporanBinding
//    private lateinit var imageView: ImageView
//
//    companion object {
//        val IMAGE_REQUEST_CODE = 100
//    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentHalamanAddLaporanBinding.inflate(inflater, container, false)
//        val view = binding.root!!
//
//        val inputPhoto = binding.inpPhoto
//
//        inputPhoto.setOnClickListener {
//            pickImageGallery()
//        }
//
//
//
//        return view
//    }
//
//    private fun pickImageGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, IMAGE_REQUEST_CODE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
//            imageView.setImageURI(data?.data)
//        }
//    }
//
//}