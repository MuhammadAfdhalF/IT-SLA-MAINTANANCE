package com.acuy.sla_maintenance

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.acuy.sla_maintenance.databinding.RegistrasiBinding
import android.content.Intent
import android.widget.Toast
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.model.Registrasi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HalamanRegistrasi : AppCompatActivity() {

    private lateinit var binding: RegistrasiBinding


//    @Inject
//    lateinit var  tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //to Login
        val toLogin = binding.toLogin
        toLogin.setOnClickListener {
            startActivity(Intent(this@HalamanRegistrasi, HalamanLogin::class.java))
            finish()
        }

        // Pada bagian onClickListener untuk tombol "Daftar"
        binding.btnRegis.setOnClickListener {
            // Memanggil saveData() hanya jika semua field sudah terisi
            if (isFormValid()) {
                saveData()
                // Membuat Intent untuk perpindahan ke halaman login
                val intent = Intent(this@HalamanRegistrasi, HalamanLogin::class.java)
                // Memulai aktivitas baru (halaman login)
                startActivity(intent)
                // Menutup aktivitas saat ini (halaman registrasi)
                finish()
            } else {
                Toast.makeText(applicationContext, "Data Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun isFormValid(): Boolean {
        val nama = binding.panggilNama.text.toString()
        val email = binding.panggilEmail.text.toString()
        val password = binding.panggilPassword.text.toString()

        // Memeriksa apakah semua field telah terisi
        return nama.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
    }

    private fun saveData() {
        val nama = binding.panggilNama.text.toString()
        val email = binding.panggilEmail.text.toString()
        val password = binding.panggilPassword.text.toString()

        val retrofit = NetworkConfig().getServices()
        retrofit.register(nama, email, password)
            .enqueue(object : Callback<Registrasi> {
                override fun onResponse(call: Call<Registrasi>, response: Response<Registrasi>) {
                    if (response.isSuccessful) {
                        val hasil = response.body()
                        Toast.makeText(applicationContext, hasil?.message, Toast.LENGTH_SHORT).show()
                        // Selesai, panggil finish() setelah proses selesai
                        finish()
                    }
                }

                override fun onFailure(call: Call<Registrasi>, t: Throwable) {
                    Toast.makeText(
                        applicationContext, "Data Gagal Disimpan: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}




//        binding.btnRegis.setOnClickListener {
//            // Memanggil saveData() hanya jika semua field sudah terisi
//            if (isFormValid()) {
//                saveData()
//            } else {
//                Toast.makeText(applicationContext, "Data Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
//            }
//        }

//class HalamanRegistrasi : AppCompatActivity() {
//
//    private lateinit var binding: RegistrasiBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = RegistrasiBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        //to Login
//        val toLogin = binding.toLogin
//        toLogin.setOnClickListener {
//            startActivity(Intent(this@HalamanRegistrasi, HalamanLogin::class.java))
//            finish()
//        }
//
//        binding.btnRegis.setOnClickListener {
//            startActivity(Intent(this@HalamanRegistrasi, HalamanLogin::class.java))
//            finish()
//            saveData()
//        }
//    }
//
//    private fun saveData() {
//
//        val nama = binding.panggilNama.text.toString()
//        val email = binding.panggilEmail.text.toString()
//        val password = binding.panggilPassword.text.toString()
//
//        val retrofit = NetworkConfig().getServices()
//        if (nama.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()) {
//            retrofit.register(nama, email, password)
//                .enqueue(object : Callback<Registrasi> {
//                    override fun onResponse(
//                        call: Call<Registrasi>,
//                        response: Response<Registrasi>
//                    ) {
//                        if (response.isSuccessful) {
//                            val hasil = response.body()
//                            Toast.makeText(applicationContext, hasil!!.message, Toast.LENGTH_SHORT)
//                                .show()
//                            nama.isNotEmpty()
//                            email.isNotEmpty()
//                            password.isNotEmpty()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Registrasi>, t: Throwable) {
//                        Toast.makeText(
//                            applicationContext, "Data Gagal Disimpan: $(t.message)",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                })
//        } else {
//            Toast.makeText(applicationContext, "Data Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
//
//        }
//    }
//
//}

//        registrasi
//        val btnRegis = binding.btnRegis
//
//        btnRegis.setOnClickListener {
//            startActivity(Intent(this@HalamanRegistrasi, HalamanLogin::class.java))
//            finish()
//        }