package com.acuy.sla_maintenance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acuy.sla_maintenance.config.Constant
import com.acuy.sla_maintenance.config.NetworkConfig
import com.acuy.sla_maintenance.config.SharedPrafManager
import com.acuy.sla_maintenance.databinding.LoginBinding
import com.acuy.sla_maintenance.model.Login
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class HalamanLogin : AppCompatActivity() {
    private lateinit var sharedPrefManager: SharedPrafManager
    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefManager = SharedPrafManager(this@HalamanLogin)

        binding.btnLogin.setOnClickListener {
            mulaiLogin()
        }

        val toRegis = binding.toRegis
        toRegis.setOnClickListener {
            startActivity(Intent(this@HalamanLogin, HalamanRegistrasi::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (sharedPrefManager.getBoolean(Constant.PREF_IS_LOGIN)) {
            moveIntent()
        }
    }

    private fun mulaiLogin() {
        val email = binding.panggilEmail.text.toString()
        val password = binding.panggilPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            val retrofit = NetworkConfig().getServices()
            retrofit.login(email, password).enqueue(object : Callback<Login> {
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null) {
                            Toast.makeText(this@HalamanLogin, loginResponse.message, Toast.LENGTH_SHORT).show()
                            val token = loginResponse.token
                            val userId = loginResponse.user.id // Dapatkan id pengguna dari respons login
                            Log.d("HalamanLogin", "USER_ID dari respons login: $userId") // Tambahkan log di sini
                            saveSession(email, password)
                            saveToken(token)
                            saveUserId(userId) // Simpan userId ke SharedPreferences
                            moveIntent()
                        }
                    } else {
                        // Handle unsuccessful login
                        Toast.makeText(this@HalamanLogin, "User Tidak Terdaftar atau Password Salah", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Login>, t: Throwable) {
                    // Handle error
                    Toast.makeText(this@HalamanLogin, "Gagal melakukan login: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@HalamanLogin, "Email dan Password harus di isi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserId(userId: Int) {
        userId?.let {
            sharedPrefManager.putInt(Constant.USER_ID, it)
        }    }





    private fun saveSession(email: String, password: String) {
        sharedPrefManager.put(Constant.PREF_EMAIL, email)
        sharedPrefManager.put(Constant.PREF_PASSWORD, password)
        sharedPrefManager.put(Constant.PREF_IS_LOGIN, true)
    }

    private fun saveToken(token: String?) {
        token?.let {
            sharedPrefManager.put(Constant.USER_TOKEN, it)
        }

    }



    private fun moveIntent() {
        startActivity(Intent(this@HalamanLogin, MainActivity::class.java))
        finish()
    }
}




//class HalamanLogin : AppCompatActivity() {
//    private lateinit var sharedPrefManager: SharedPrafManager
//    private lateinit var binding: LoginBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = LoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        sharedPrefManager = SharedPrafManager(applicationContext)
//
//        binding.btnLogin.setOnClickListener {
//            mulaiLogin()
//        }
//
//        // Tombol untuk menuju ke halaman registrasi
//        val toRegis = binding.toRegis
//        toRegis.setOnClickListener {
//            startActivity(Intent(this@HalamanLogin, HalamanRegistrasi::class.java))
//            finish()
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if (sharedPrefManager.getBoolean(Constant.PREF_IS_LOGIN)) {
//            moveIntent()
//        }
//    }
//
//    private fun mulaiLogin() {
//        val email = binding.panggilEmail.text.toString()
//        val password = binding.panggilPassword.text.toString()
//        if (email.isNotEmpty() && password.isNotEmpty()) {
//            val retrofit = NetworkConfig.getServices()
//            retrofit.login(email, password).enqueue(object : Callback<Login> {
//                override fun onResponse(call: Call<Login>, response: Response<Login>) {
//                    val loginResponse = response.body()
//                    if (loginResponse != null) {
//                        Toast.makeText(applicationContext, loginResponse.message, Toast.LENGTH_SHORT).show()
//                        val token = loginResponse.token
//                        saveSession(email, password)
//                        saveToken(token)
//                        moveIntent()
//                    } else {
//                        Toast.makeText(applicationContext, "User Tidak Terdaftar", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<Login>, t: Throwable) {
//                    // Handle error
//                    Toast.makeText(applicationContext, "Gagal melakukan login: ${t.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } else {
//            Toast.makeText(applicationContext, "Email dan Password harus di isi", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun saveSession(email: String, password: String) {
//        sharedPrefManager.put(Constant.PREF_EMAIL, email)
//        sharedPrefManager.put(Constant.PREF_PASSWORD, password)
//        sharedPrefManager.put(Constant.PREF_IS_LOGIN, true)
//    }
//
//    private fun saveToken(token: String) {
//        sharedPrefManager.put(Constant.USER_TOKEN, token)
//    }
//
//    private fun moveIntent() {
//        startActivity(Intent(this@HalamanLogin, MainActivity::class.java))
//        finish()
//    }
//}


//class HalamanLogin : AppCompatActivity() {
//    private lateinit var sharedPrefManager: SharedPrafManager
//    private lateinit var binding: LoginBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = LoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        sharedPrefManager = SharedPrafManager(applicationContext)
//
//        binding.btnLogin.setOnClickListener {
//            mulaiLogin()
//        }
//
////        to Regis
//        val toRegis = binding.toRegis
//
//        toRegis.setOnClickListener {
//            startActivity(Intent(this@HalamanLogin, HalamanRegistrasi::class.java))
//            finish()
//        }
//
//    }
//
//    //    cek pengguna sudah login atau belum, jika sudah maka langsung diarahkan ke main activity
//    override fun onStart() {
//        super.onStart()
//        if (sharedPrefManager.getBoolean(Constant.PREF_IS_LOGIN)) {
//            moveIntent()
//        }
//    }
////
////    private fun mulaiLogin() {
////        val email = binding.panggilEmail.text.toString()
////        val password = binding.panggilPassword.text.toString()
////
////        if (email.isNotEmpty() && password.isNotEmpty()) {
////            val retrofit = NetworkConfig().getServices()
////            retrofit.login(email, password)
////                .enqueue(object : Callback<Login> {
////                    override fun onResponse(call: Call<Login>, response: Response<Login>) {
////                        val loginResponse = response.body()
////                        if (loginResponse != null) {
////                            val token = loginResponse.token ?: ""
////                            Toast.makeText(
////                                applicationContext,
////                                loginResponse.message,
////                                Toast.LENGTH_SHORT
////                            ).show()
////                            saveSession(email, password)
////                            NetworkConfig().setToken(token) // Simpan token ke header
////                            moveIntent()
////                        } else {
////                            Toast.makeText(
////                                applicationContext,
////                                "User Tidak Terdaftar",
////                                Toast.LENGTH_SHORT
////                            ).show()
////                        }
////                    }
////
////                    override fun onFailure(call: Call<Login>, t: Throwable) {
////                        Toast.makeText(
////                            applicationContext,
////                            "Gagal melakukan login: ${t.message}",
////                            Toast.LENGTH_SHORT
////                        ).show()
////                    }
////                })
////        } else {
////            Toast.makeText(
////                applicationContext,
////                "Email dan Password harus di isi",
////                Toast.LENGTH_SHORT
////            ).show()
////        }
////    }
//
//
//    //    function ini unutk proses validasi login
////    private fun mulaiLogin() {
////        val email = binding.panggilEmail.text.toString()
////        val password = binding.panggilPassword.text.toString()
////
////        if (email.isNotEmpty() && password.isNotEmpty()) {
////            val retrofit = NetworkConfig().getServices()
////            retrofit.login(email, password)
////                .enqueue(object : Callback<Login> {
////                    override fun onResponse(call: Call<Login>, response: Response<Login>) {
////                        val loginResponse = response.body()
////                        if (loginResponse != null) {
////                            Toast.makeText(
////                                applicationContext,
////                                loginResponse.message,
////                                Toast.LENGTH_SHORT
////                            ).show()
////                            saveSession(email, password)
////                            moveIntent()
////                        } else {
////                            Toast.makeText(
////                                applicationContext,
////                                "User Tidak Terdaftar",
////                                Toast.LENGTH_SHORT
////                            ).show()
////                        }
////                    }
////
////                    override fun onFailure(call: Call<Login>, t: Throwable) {
////                        // Handle error
////                        Toast.makeText(
////                            applicationContext,
////                            "Gagal melakukan login: ${t.message}",
////                            Toast.LENGTH_SHORT
////                        ).show()
////                    }
////                })
////        } else {
////            Toast.makeText(
////                applicationContext,
////                "Email dan Password harus di isi",
////                Toast.LENGTH_SHORT
////            ).show()
////        }
////    }
//
//
//    private fun mulaiLogin() {
//        val email = binding.panggilEmail.text.toString()
//        val password = binding.panggilPassword.text.toString()
//        if (email.isNotEmpty() && password.isNotEmpty()) {
//            val retrofit = NetworkConfig().getServices()
//            retrofit.login(email, password).enqueue(object : Callback<Login> {
//                override fun onResponse(call: Call<Login>, response: Response<Login>) {
//                    val loginResponse = response.body()
//                    if (loginResponse != null) {
//                        Toast.makeText(applicationContext, loginResponse.message, Toast.LENGTH_SHORT).show()
//                        val token = loginResponse.token
//                        saveToken(token) // Simpan token ke SharedPreferences
//                        moveIntent()
//                    } else {
//                        Toast.makeText(applicationContext, "User Tidak Terdaftar", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<Login>, t: Throwable) {
//                    // Handle error
//                    Toast.makeText(applicationContext, "Gagal melakukan login: ${t.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } else {
//            Toast.makeText(applicationContext, "Email dan Password harus di isi", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    //    menyimpan informasi login dengan sharedpreference
//    private fun saveSession(email: String, password: String) {
//        sharedPrefManager.put(Constant.PREF_EMAIL, email)
//        sharedPrefManager.put(Constant.PREF_PASSWORD, password)
//        sharedPrefManager.put(Constant.PREF_IS_LOGIN, true)
//    }
//
//    private fun moveIntent() {
//        startActivity(Intent(this@HalamanLogin, MainActivity::class.java))
//        finish()
//    }
//    // Pada fungsi onResponse() setelah login berhasil, simpan token ke dalam SharedPreferences
//    override fun onResponse(call: Call<Login>, response: Response<Login>) {
//        val loginResponse = response.body()
//        if (loginResponse != null) {
//            Toast.makeText(applicationContext, loginResponse.message, Toast.LENGTH_SHORT).show()
//            val token = loginResponse.token
//            saveToken(token) // Simpan token ke SharedPreferences
//            moveIntent()
//        } else {
//            Toast.makeText(applicationContext, "User Tidak Terdaftar", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // Fungsi untuk menyimpan token ke SharedPreferences
//    private fun saveToken(token: String) {
//        sharedPrefManager.put(Constant.USER_TOKEN, token)
//    }
//
//}


//class HalamanLogin : AppCompatActivity() {
//    private lateinit var sharePrafManager: SharedPrafManager
//    private lateinit var binding: LoginBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = LoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        sharePrafManager = SharedPrafManager(this)
//
//        binding.btnLogin.setOnClickListener {
//            mulaiLogin()
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if (sharePrafManager.getBoolean(Constant.PREF_IS_LOGIN)) {
//            moveIntent()
//        }
//    }
//
//    private fun mulaiLogin() {
//        val email = binding.panggilEmail.text.toString()
//        val password = binding.panggilPassword.text.toString()
//        val retrofit = NetworkConfig().getServices()
//
//        if (email.isNotEmpty() && password.isNotEmpty()) {
//            retrofit.login(email, password)
//                .enqueue(object : Callback<Login> {
//                    override fun onResponse(call: Call<Login>, response: Response<Login>) {
//                        val responseBody = response.body()
//                        if (responseBody != null) {
//                            if (!responseBody.error) {
//                                Toast.makeText(
//                                    applicationContext,
//                                    responseBody.message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                saveSession(email, password)
//                                moveIntent()
//                            } else {
//                                Toast.makeText(
//                                    applicationContext,
//                                    "Gagal: ${responseBody.message}",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Login>, t: Throwable) {
//                        val errorMessage = when (t) {
//                            is SocketTimeoutException -> "Timeout saat melakukan panggilan API"
//                            is IOException -> "Masalah koneksi jaringan"
//                            else -> "Gagal melakukan panggilan API: ${t.message}"
//                        }
//                        Toast.makeText(this@HalamanLogin, errorMessage, Toast.LENGTH_SHORT).show()
//                    }
//                })
//        } else {
//            Toast.makeText(applicationContext, "Email dan Password harus di isi", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun saveSession(email: String, password: String) {
//        sharePrafManager.put(Constant.PREF_EMAIL, email)
//        sharePrafManager.put(Constant.PREF_PASSWORD, password)
//        sharePrafManager.put(Constant.PREF_IS_LOGIN, true)
//    }
//
//    private fun moveIntent() {
//        startActivity(Intent(this@HalamanLogin, MainActivity::class.java))
//        finish()
//    }
//}


//
////Real dari Youtube
//
//class HalamanLogin : AppCompatActivity() {
//    lateinit var SharePrafManager: SharedPrafManager
//    private lateinit var binding: LoginBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = LoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        SharePrafManager = SharedPrafManager(this)
//
//        binding.btnLogin.setOnClickListener {
//            MulaiLogin()
//        }
//
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if (SharePrafManager.getBoolean(Constant.PREF_IS_LOGIN)) {
//            moveIntent()
//        }
//    }
//
//
//    private fun MulaiLogin() {
//        val email = binding.panggilEmail.text.toString()
//        val password = binding.panggilPassword.text.toString()
//        val retrofit = NetworkConfig().getServices()
//
//
//        if (email.isNotEmpty() && password.isNotEmpty()) {
////            retrofit
//            retrofit.login(email, password)
//                .enqueue(object : Callback<Login> {
//                    override fun onResponse(call: Call<Login>, response: Response<Login>) {
//                        val response = response.body()
//                        if (response != null) {
//                            if (response.error == false) {
//                                Toast.makeText(
//                                    applicationContext,
//                                    response.message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                saveSession(email, password)
//                                moveIntent()
//                            }
//                        }
//                    }
//                    override fun onFailure(call: Call<Login>, t: Throwable) {
//                        Toast.makeText(applicationContext,"${t.message}", Toast.LENGTH_SHORT).show()
//                    }
//
//                })
//        } else{
//            Toast.makeText(applicationContext, "Email dan Password harus di isi", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//
//    private fun saveSession(email: String, password: String) {
//        SharePrafManager.put(Constant.PREF_EMAIL, email)
//        SharePrafManager.put(Constant.PREF_PASSWORD, password)
//        SharePrafManager.put(Constant.PREF_IS_LOGIN, true)
//
//
//    }
//
//    private fun moveIntent() {
//        startActivity(Intent(this@HalamanLogin, MainActivity::class.java))
//        finish()
//    }
//
//}
//
//
//
//


//override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    binding = LoginBinding.inflate(layoutInflater)
//    setContentView(binding.root)
//
////        to Regis
//    val toRegis = binding.toRegis
//
//    toRegis.setOnClickListener {
//        startActivity(Intent(this@HalamanLogin, HalamanRegistrasi::class.java))
//        finish()
//    }
//
//    //        Login
//    val btnLogin = binding.btnLogin
//
//    btnLogin.setOnClickListener {
//        startActivity(Intent(this@HalamanLogin, MainActivity::class.java))
//        finish()
//    }
//
//
//}