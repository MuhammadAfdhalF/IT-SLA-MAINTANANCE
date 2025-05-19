package com.acuy.sla_maintenance

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.acuy.sla_maintenance.databinding.LoadingScreenBinding

class LoadingScreen : AppCompatActivity() {
    private lateinit var binding: LoadingScreenBinding
    private lateinit var progressBar: ProgressBar
    private val progressIncrement = 5
    private var currentProgress = 0
    private val delayMillis = 200 // Penundaan 3 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoadingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.loadingProgressBar
        progressBar.max = 100
        progressBar.progress = 5

        // Tambahkan penundaan sebelum ProgressBar dimulai
        Handler().postDelayed({
            // Mulai pembaruan ProgressBar setelah penundaan 3 detik
            startProgressBar()
        }, delayMillis.toLong())
    }

    private fun startProgressBar() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                currentProgress += progressIncrement
                progressBar.progress = currentProgress

                if (currentProgress < progressBar.max) {
                    handler.postDelayed(this, 100) // Setiap 100 milidetik
                } else {
                    // Ketika mencapai 100%, pindah ke HalamanLogin
                    val intent = Intent(this@LoadingScreen, HalamanLogin::class.java)
                    startActivity(intent)
                    finish() // Menutup aktivitas loading screen
                }
            }
        }

        // Mulai pembaruan ProgressBar
        handler.postDelayed(runnable, 0) // Mulai tanpa penundaan
    }
}


//
//package com.acuy.sla_maintenance
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import androidx.appcompat.app.AppCompatActivity
//import com.acuy.sla_maintenance.databinding.LoadingScreenBinding
//
//class LoadingScreen : AppCompatActivity() {
//    private lateinit var binding: LoadingScreenBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        binding = LoadingScreenBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        // codingan Loading
//        val delayMillis = 3000
//        Handler().postDelayed({
//            val intent = Intent(this@LoadingScreen, HalamanLogin::class.java)
//            startActivity(intent)
//            finish() // Menutup aktivitas loading screen
//        }, delayMillis.toLong())
//    }
//}
