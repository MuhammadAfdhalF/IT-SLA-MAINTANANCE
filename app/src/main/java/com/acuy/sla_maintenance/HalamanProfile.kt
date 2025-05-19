package com.acuy.sla_maintenance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.acuy.sla_maintenance.config.ApiServices
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.Images
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.FragmentHalamanProfileBinding
import com.acuy.sla_maintenance.model.PostUsers
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HalamanProfile : Fragment() {
    private lateinit var binding: FragmentHalamanProfileBinding
    private lateinit var apiServices: ApiServices
    private lateinit var sharedPrefManager: SharedPrafManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHalamanProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPrefManager = SharedPrafManager(requireContext())

        //        btnJamKerja
        val btnListJamKerja = binding.btnListJamKerja
        btnListJamKerja.setOnClickListener {
            val intent = Intent(requireActivity(), HalamanJamKerja::class.java)
            startActivity(intent)
        }

        binding.btnUpdateProfile.setOnClickListener{
            val intent = Intent(requireActivity(), UpdateProfile::class.java)
            startActivity(intent)

        }

        //logout
        val btnLogut = binding.btnLogout
        btnLogut.setOnClickListener {
            sharedPrefManager.clear()
            showMessage("Keluar")
            moveIntent()
        }

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

        return view
    }

    private fun moveIntent() {
        val intent = Intent(requireActivity(), HalamanLogin::class.java)
        startActivity(intent)
    }

    private fun showMessage(s: String) {

    }

    private fun getUsers(token: String) {
        apiServices.getUsers("Bearer $token").enqueue(object : Callback<PostUsers> {
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
        // Menampilkan detail pengguna sesuai kebutuhan aplikasi Anda
        binding.namaProfile.text = user.username
        binding.emailProfile.text = user.email

        val images = Images()
        val imageUrlAwal = "${images.BASE_URL}${user.foto}"
        Glide.with(requireContext())
            .load(imageUrlAwal) // URL gambar yang diberikan oleh respons API
            .placeholder(R.drawable.ic_launcher_foreground) // Gambar placeholder yang akan ditampilkan saat proses unduhan gambar berlangsung
            .error(R.drawable.clear) // Gambar yang akan ditampilkan jika terjadi kesalahan saat memuat gambar
            .into(binding.fotoUser) // Tampilkan gambar di CircleImageView

        val imageUrlAkhir = "${images.BASE_URL}${user.ttd}"
        Glide.with(this)
            .load(imageUrlAkhir) // URL gambar yang diberikan oleh respons API
            .placeholder(R.drawable.ic_launcher_foreground) // Gambar placeholder yang akan ditampilkan saat proses unduhan gambar berlangsung
            .error(R.drawable.clear) // Gambar yang akan ditampilkan jika terjadi kesalahan saat memuat gambar
            .into(binding.panggilTtd) // Tampilkan gambar di CircleImageView


    }


    private fun getTokenFromSharedPreferences(): String? {
        // Mengambil token dari SharedPreferences
        return sharedPrefManager.getString(Constant.USER_TOKEN)
    }
}


//class HalamanProfile : Fragment() {
//    private lateinit var binding: FragmentHalamanProfileBinding
//    lateinit var SharedPrafManager: SharedPrafManager
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentHalamanProfileBinding.inflate(inflater, container, false)
//        val view = binding.root!!
//
//        SharedPrafManager = SharedPrafManager((requireContext()))
//
//
////        btnJamKerja
//        val btnListJamKerja = binding.btnListJamKerja
//        btnListJamKerja.setOnClickListener {
//            val intent = Intent(requireActivity(), HalamanJamKerja::class.java)
//            startActivity(intent)
//        }
//
//        val btnLogut = binding.btnLogout
//
//        btnLogut.setOnClickListener {
//            SharedPrafManager.clear()
//            showMessage("Keluar")
//            moveIntent()
//
////            val intent = Intent(requireActivity(), HalamanLogin::class.java)
////            startActivity(intent)
//        }
//
//        return view
//    }
//
//    private fun showMessage(message: String) {
//
//    }
//
//    private fun moveIntent() {
//        val intent = Intent(requireActivity(), HalamanLogin::class.java)
//        startActivity(intent)
//    }
//
//}