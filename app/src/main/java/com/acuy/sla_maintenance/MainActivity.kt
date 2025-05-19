package com.acuy.sla_maintenance

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.acuy.sla_maintenance.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var berandaFragment: HalamanHome
    private lateinit var reportListFragment: HalamanReportList
    private lateinit var addLaporanFragment: HalamanAddLaporan
    private lateinit var historyFragment: HalamanHistory
    private lateinit var profileFragment: HalamanProfile
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi fragment
        berandaFragment = HalamanHome()
        reportListFragment = HalamanReportList()
        addLaporanFragment = HalamanAddLaporan()
        historyFragment = HalamanHistory()
        profileFragment = HalamanProfile()


        // Set background bottom navigation menjadi null
        binding.buttonNavigation.background = null
        binding.buttonNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> openFragment(berandaFragment)
                R.id.report -> openFragment(reportListFragment)
                R.id.history -> openFragment(historyFragment)
                R.id.profile -> openFragment(profileFragment)
            }
            true
        }

        fragmentManager = supportFragmentManager
        bottomNavigationView = findViewById(R.id.buttonNavigation)

        openFragment(berandaFragment)

//        binding.fab.setOnClickListener {
//            addLaporanFragment = HalamanAddLaporan()
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.fragment_container, addLaporanFragment)
//                .addToBackStack(null)  // Menambahkan ke back stack
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commitAllowingStateLoss()
//
//        }
        binding.fab.setOnClickListener {
            toggleAddReport()
        }


    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun toggleAddReport() {
        // Memberikan efek visual saat FAB ditekan
        binding.fab.scaleX = 0.9f
        binding.fab.scaleY = 0.9f

        // Delay untuk memberikan efek sementara
        Handler().postDelayed({
            // Kembali ke ukuran asli setelah delay
            binding.fab.scaleX = 1.0f
            binding.fab.scaleY = 1.0f

            // Ganti fragment saat FAB ditekan
            addLaporanFragment = HalamanAddLaporan()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, addLaporanFragment)
                .addToBackStack(null)  // Menambahkan ke back stack
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss()

            // Tekan item menu "Add Report" pada BottomNavigationView
            bottomNavigationView.menu.findItem(R.id.addReport)?.isChecked = true
        }, 100) // Delay dalam milidetik
    }
}
